package com.farzana.farzanacollection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.farzana.farzanacollection.Provalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText edt_name, edt_phone, edt_address, edt_city;
    private String totalPrice = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        edt_name = findViewById(R.id.edt_confirm_nameId);
        edt_phone = findViewById(R.id.edt_confirm_phone);
        edt_address = findViewById(R.id.edt_confirm_address);
        edt_city = findViewById(R.id.edt_confirm_cityName);

        totalPrice = getIntent().getStringExtra("price");

        findViewById(R.id.btn_confirm).setOnClickListener((View view)->{
            check();
        });
    }

    private void check() {
        if (TextUtils.isEmpty(edt_name.getText().toString())){
            Toast.makeText(getApplicationContext(), "Please write your name...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(edt_phone.getText().toString())){
            Toast.makeText(getApplicationContext(), "Please write your phone...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(edt_address.getText().toString())){
            Toast.makeText(getApplicationContext(), "Please write your address...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(edt_city.getText().toString())){
            Toast.makeText(getApplicationContext(), "Please write your City...", Toast.LENGTH_SHORT).show();
        }else{
            confirmOrder();
        }
    }

    private void confirmOrder() {
        final String saveCurrentTime, saveCurrentDate;

        Calendar calDateTime = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calDateTime.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calDateTime.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        final HashMap<String, Object> orderMap = new HashMap<>();

        orderMap.put("totalPrice", totalPrice);
        orderMap.put("name", edt_name.getText().toString());
        orderMap.put("phone", edt_phone.getText().toString());
        orderMap.put("address", edt_address.getText().toString());
        orderMap.put("city", edt_city.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("time", saveCurrentTime);
        orderMap.put("state", "Not shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ConfirmFinalOrderActivity.this, "Your Order Confirm Successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                    });
                }
            }
        });
    }
}
