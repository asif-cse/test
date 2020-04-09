package com.farzana.farzanacollection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private EditText edt_name, edt_phone, edt_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);

        edt_name = findViewById(R.id.edt_nameId);
        edt_phone = findViewById(R.id.edt_phoneId);
        edt_password = findViewById(R.id.edt_passwordId);

        findViewById(R.id.btn_createId).setOnClickListener((View view)->{
            CreateAccount();
        });
    }

    private void CreateAccount() {
        String name = edt_name.getText().toString().trim();
        String phone = edt_phone.getText().toString().trim();
        String password = edt_password.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(getApplicationContext(), "Please write your name...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(), "Please write your phone...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Please write your password...", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.setTitle("Create Account");
            progressDialog.setMessage("Please wait, while we are checking the credentials");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            ValidatePhoneNumber(name, phone, password);
        }
    }


    private void ValidatePhoneNumber(String name, String phone, String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Users").child(phone).exists())){
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);

                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                    }else
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network error: please try again after some time...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }else {
                    Toast.makeText(getApplicationContext(), "This"+phone+"already exists,", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Please try again using another phone number.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
