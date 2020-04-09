package com.farzana.farzanacollection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView tShirts, sortsTShirts, femaleDresses, sweaters;
    private ImageView glasses, hatsCaps, walletsBags, shoes;
    private ImageView headPhone, laptop, watch, mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_catagori);

        findViewById(R.id.t_shirtId).setOnClickListener((View view)->{
            Intent intent = new Intent(getApplicationContext(),AdminAddNewProductActivity.class);
            intent.putExtra("category","T-Shirts");
            startActivity(intent);
        });
        findViewById(R.id.sport_t_shirtId).setOnClickListener((View view)->{
            Intent intent = new Intent(getApplicationContext(),AdminAddNewProductActivity.class);
            intent.putExtra("category","Sorts TShirts");
            startActivity(intent);
        });
        findViewById(R.id.femaleId).setOnClickListener((View view)->{
            Intent intent = new Intent(getApplicationContext(),AdminAddNewProductActivity.class);
            intent.putExtra("category","Female Dresses");
            startActivity(intent);
        });
        findViewById(R.id.sweatersId).setOnClickListener((View view)->{
            Intent intent = new Intent(getApplicationContext(),AdminAddNewProductActivity.class);
            intent.putExtra("category","Sweaters");
            startActivity(intent);
        });
        findViewById(R.id.glassId).setOnClickListener((View view)->{
            Intent intent = new Intent(getApplicationContext(),AdminAddNewProductActivity.class);
            intent.putExtra("category","Glasses");
            startActivity(intent);
        });
        findViewById(R.id.hatsId).setOnClickListener((View view)->{
            Intent intent = new Intent(getApplicationContext(),AdminAddNewProductActivity.class);
            intent.putExtra("category","Hats Caps");
            startActivity(intent);
        });
        findViewById(R.id.walletsBagsId).setOnClickListener((View view)->{
            Intent intent = new Intent(getApplicationContext(),AdminAddNewProductActivity.class);
            intent.putExtra("category","Wallets Bags");
            startActivity(intent);
        });
        findViewById(R.id.showesId).setOnClickListener((View view)->{
            Intent intent = new Intent(getApplicationContext(),AdminAddNewProductActivity.class);
            intent.putExtra("category","Shoes");
            startActivity(intent);
        });
        findViewById(R.id.headphoneId).setOnClickListener((View view)->{
            Intent intent = new Intent(getApplicationContext(),AdminAddNewProductActivity.class);
            intent.putExtra("category","Head Phone");
            startActivity(intent);
        });
        findViewById(R.id.lapotpId).setOnClickListener((View view)->{
            Intent intent = new Intent(getApplicationContext(),AdminAddNewProductActivity.class);
            intent.putExtra("category","Laptop");
            startActivity(intent);
        });
        findViewById(R.id.watchId).setOnClickListener((View view)->{
            Intent intent = new Intent(getApplicationContext(),AdminAddNewProductActivity.class);
            intent.putExtra("category","Watch");
            startActivity(intent);
        });
        findViewById(R.id.mobileId).setOnClickListener((View view)->{
            Intent intent = new Intent(getApplicationContext(),AdminAddNewProductActivity.class);
            intent.putExtra("category","Mobile Phone");
            startActivity(intent);
        });

    }
}
