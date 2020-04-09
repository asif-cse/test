package com.farzana.farzanacollection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, PName, PDescription, PPrice, saveCurrentTime, saveCurrentDate, productRandomKey, downloadImageUrl;
    private EditText edt_product_name, edt_product_des, edt_product_price;
    private ImageView InputProductImage;
    private static final int GalleryPic = 1 ;
    private Uri ImageUri;
    // instance for firebase storage and StorageReference
    private StorageReference ProductImageRef;
    private DatabaseReference ProductRef;
    private ProgressDialog progressDialog;
    private Button AddNewProduct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        CategoryName = getIntent().getExtras().get("category").toString();

        edt_product_name = findViewById(R.id.edt_product_nameId);
        edt_product_des = findViewById(R.id.edt_product_descriptionId);
        edt_product_price = findViewById(R.id.edt_product_priceId);
        InputProductImage = findViewById(R.id.select_product_imageId);
        AddNewProduct = findViewById(R.id.btn_add_new_productId);
        progressDialog = new ProgressDialog(this);

        // Create a storage reference from our app
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Product_Images");
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");

        InputProductImage.setOnClickListener((View view)->{
            OpenGallery();
        });
        AddNewProduct.setOnClickListener((View view)->{
            ValidateProductData();
        });
    }
    private void OpenGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GalleryPic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPic && resultCode == RESULT_OK && data!=null){
            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }

    private void ValidateProductData()
    {
        PName = edt_product_name.getText().toString().trim();
        PDescription = edt_product_des.getText().toString().trim();
        PPrice = edt_product_price.getText().toString().trim();

        if (ImageUri == null)
        {
            Toast.makeText(getApplicationContext(), "Product Image is mandatory...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(PName))
        {
            Toast.makeText(getApplicationContext(), "Please enter product name", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(PDescription))
        {
            Toast.makeText(getApplicationContext(), "Please enter product description", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(PPrice))
        {
            Toast.makeText(getApplicationContext(), "Please enter product price", Toast.LENGTH_SHORT).show();
        }else
        {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {

        progressDialog.setTitle("Add new Product");
        progressDialog.setMessage("Please wait, while we are adding the new product...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;


        StorageReference filePath = ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Product_Images");
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product Image upload successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product upload successfully", Toast.LENGTH_SHORT).show();
                            saveProductInfoDatabase();
                        }
                    }
                });
            }
        });

    }

    private void saveProductInfoDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", PDescription);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", CategoryName);
        productMap.put("price", PPrice);
        productMap.put("pname", PName);

        ProductRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            startActivity(new Intent(getApplicationContext(), AdminCategoryActivity.class));
                            progressDialog.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product information added successfully...", Toast.LENGTH_SHORT).show();
                        }else
                        {
                            progressDialog.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
