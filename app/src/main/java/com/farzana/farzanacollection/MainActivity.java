package com.farzana.farzanacollection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.farzana.farzanacollection.Model.Users;
import com.farzana.farzanacollection.Provalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);
        progressDialog = new ProgressDialog(this);

        findViewById(R.id.btn_first_loginId).setOnClickListener((View view)->{
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        });
        findViewById(R.id.btn_joinId).setOnClickListener((View view)->{
            startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
        });

        String UserPhoneKey = Paper.book().read(Prevalent.userPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.userPasswordKey);

        if (UserPhoneKey != "" && UserPasswordKey != "")
        {
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserPhoneKey, UserPasswordKey);
                progressDialog.setTitle("Already Logged in.");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }
        }
    }

    private void AllowAccess(final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("Users").child(phone).exists())
                {
                    Users userdata = dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if (userdata.getPhone().equals(phone)){
                        if (userdata.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "You are already logged in.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else
                {
                    Toast.makeText(MainActivity.this, "Account with this"+phone+"number don't exists.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
