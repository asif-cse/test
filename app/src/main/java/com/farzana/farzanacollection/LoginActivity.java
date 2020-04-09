package com.farzana.farzanacollection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.farzana.farzanacollection.Model.Users;
import com.farzana.farzanacollection.Provalent.Prevalent;
import com.farzana.farzanacollection.ui.home.HomeFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;


public class LoginActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private EditText edt_login_phone, edt_login_password;
    private String parentDBName = "Users";
    private CheckBox checkboxRememberMe;
    private TextView AdminLink, NotAdminLink;
    private Button LoginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edt_login_phone = findViewById(R.id.edt_login_phoneId);
        edt_login_password = findViewById(R.id.edt_login_passwordId);
        LoginBtn = findViewById(R.id.btn_loginId);
        AdminLink = findViewById(R.id.admin_panelId);
        NotAdminLink = findViewById(R.id.not_admin_panelId);

        checkboxRememberMe = findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        progressDialog = new ProgressDialog(this);

        findViewById(R.id.btn_loginId).setOnClickListener((View view)->{
            LoginAccount();
        });

        findViewById(R.id.admin_panelId).setOnClickListener((View view)->{
            LoginBtn.setText("Login Admin");
            AdminLink.setVisibility(View.INVISIBLE);
            NotAdminLink.setVisibility(View.VISIBLE);
            parentDBName = "Admins";
        });
        findViewById(R.id.not_admin_panelId).setOnClickListener((View view)->{
            LoginBtn.setText("Login");
            AdminLink.setVisibility(View.VISIBLE);
            NotAdminLink.setVisibility(View.INVISIBLE);
            parentDBName = "Users";
        });
    }

    private void LoginAccount() {
        String phone = edt_login_phone.getText().toString().trim();
        String password = edt_login_password.getText().toString().trim();

        if (TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(), "Please write your phone...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Please write your password...", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.setTitle("Login Account");
            progressDialog.setMessage("Please wait, while we are checking tha credentials");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            AllowAccessToAccount(phone, password);
        }
    }

    private void AllowAccessToAccount(String phone, String password)
    {
        if (checkboxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.userPhoneKey, phone);
            Paper.book().write(Prevalent.userPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDBName).child(phone).exists())
                {
                    Users userdata = dataSnapshot.child(parentDBName).child(phone).getValue(Users.class);
                    if (userdata.getPhone().equals(phone)){
                        if (userdata.getPassword().equals(password)){

                            if (parentDBName.equals("Admins")){
                                Toast.makeText(LoginActivity.this, "Welcome Admin, you are login Successfully...", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), AdminCategoryActivity.class));
                            }else if (parentDBName.equals("Users")){
                                Toast.makeText(LoginActivity.this, "Login Successfully...", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                Prevalent.currentOnlineUser = userdata;
                                startActivity(intent);
                            }
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else
                {
                    Toast.makeText(LoginActivity.this, "Account with this"+phone+"number don't exists.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
