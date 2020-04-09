package com.farzana.farzanacollection.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farzana.farzanacollection.Interface.ItemClickListener;
import com.farzana.farzanacollection.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public TextView productName, productDescription, productPrice;
    public ImageView productImage;
    public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView)
    {
        super(itemView);

        productName = itemView.findViewById(R.id.product_name);
        productDescription = itemView.findViewById(R.id.product_description);
        productPrice = itemView.findViewById(R.id.product_price);
        productImage = itemView.findViewById(R.id.product_imageId);

    }
    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener=listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
