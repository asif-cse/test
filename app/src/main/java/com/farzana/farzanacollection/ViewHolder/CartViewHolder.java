package com.farzana.farzanacollection.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farzana.farzanacollection.Interface.ItemClickListener;
import com.farzana.farzanacollection.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView productName, productPrice, productQuantity;
    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        productName = itemView.findViewById(R.id.cart_product_name);
        productQuantity = itemView.findViewById(R.id.cart_product_quantity);
        productPrice = itemView.findViewById(R.id.cart_product_price);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
