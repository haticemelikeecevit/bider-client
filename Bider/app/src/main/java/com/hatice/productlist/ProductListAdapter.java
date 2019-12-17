package com.hatice.productlist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hatice.R;
import com.hatice.offer.OfferActivity;

import java.util.ArrayList;

public class ProductListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ProductItem> productItems;

    public ProductListAdapter(Context context, ArrayList<ProductItem> productItems) {
        this.context = context;
        this.productItems = productItems;
    }

    @Override
    public int getCount() {
        return productItems.size();
    }

    @Override
    public Object getItem(int i) {
        return productItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.product_item, viewGroup, false);
        }
        final ProductItem item = productItems.get(i);

        ImageView productImage = view.findViewById(R.id.productImage);
        Glide.with(this.context).load(item.getImageUrl()).into(productImage);

        TextView productName = view.findViewById(R.id.productName);
        productName.setText(item.getName());

        TextView productDescription = view.findViewById(R.id.productDescription);
        productDescription.setText(item.getDescription());

        TextView productPrice = view.findViewById(R.id.productPrice);
        productPrice.setText(item.getPrice() + " TL");

        Button offerButton = view.findViewById(R.id.offerButton);
        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Info", "Clicked: " + i);
                startOfferActivity(item);
            }
        });

        return view;
    }

    private void startOfferActivity(ProductItem item) {
        Intent intent = new Intent(this.context, OfferActivity.class);
        intent.putExtra("ProductItem", item);
        this.context.startActivity(intent);
    }
}
