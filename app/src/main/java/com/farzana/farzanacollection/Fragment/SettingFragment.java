package com.farzana.farzanacollection.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.farzana.farzanacollection.HomeActivity;
import com.farzana.farzanacollection.MainActivity;
import com.farzana.farzanacollection.Provalent.Prevalent;
import com.farzana.farzanacollection.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class SettingFragment extends Fragment {

    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn,  closeTextBtn, saveTextButton;

    private Uri imageUri;
    private String myUrl = "";
    private static final int GalleryPic = 1 ;
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = (CircleImageView) root.findViewById(R.id.setting_profile_picture);
        fullNameEditText = (EditText) root.findViewById(R.id.edt_setting_nameId);
        userPhoneEditText = (EditText) root.findViewById(R.id.edt_setting_phoneId);
        addressEditText = (EditText) root.findViewById(R.id.edt_setting_address);
        profileChangeTextBtn = (TextView) root.findViewById(R.id.profile_image_change);
        closeTextBtn = (TextView) root.findViewById(R.id.close_setting_btn);
        saveTextButton = (TextView) root.findViewById(R.id.update_setting_btn);

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);

        closeTextBtn.setOnClickListener((View view)->{
            finish();
        });
        saveTextButton.setOnClickListener((View view)->{
            if (checker.equals("clicked"))
            {
                userInfoSaved();
            }
            else
            {
                updateOnlyUserInfo();
            }
        });
        //test
        profileChangeTextBtn.setOnClickListener((View view)->{
            checker = "clicked";
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, GalleryPic);
        });
        return root;
    }

    private void finish() {
        startActivity(new Intent(getContext(), HomeActivity.class));
    }

    private void updateOnlyUserInfo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", fullNameEditText.getText().toString());
        userMap. put("address", addressEditText.getText().toString());
        userMap. put("phoneOrder", userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(getContext(), SettingFragment.class));
        Toast.makeText(getContext(), "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPic && resultCode == RESULT_OK && data!=null){
            imageUri = data.getData();

            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(getContext(), "Error, Try Again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), SettingFragment.class));
            finish();
        }
    }

    private void userInfoSaved()
    {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(getContext(), "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(getContext(), "Enter Address.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(getContext(), "Enter another phone.", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }
    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePictureRef.child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap. put("name", fullNameEditText.getText().toString());
                        userMap. put("address", addressEditText.getText().toString());
                        userMap. put("phoneOrder", userPhoneEditText.getText().toString());
                        userMap. put("image", myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
                        progressDialog.dismiss();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        Toast.makeText(getContext(), "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Error.", Toast.LENGTH_SHORT).show();
                        }
                }
            });
        }
        else
        {
            Toast.makeText(getContext(), "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }
    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
