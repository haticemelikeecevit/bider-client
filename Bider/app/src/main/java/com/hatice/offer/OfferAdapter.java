package com.hatice.offer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatTextView;

import com.hatice.R;

import java.util.ArrayList;

public class OfferAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<OfferItem> offerItems;

    public OfferAdapter(Context context, ArrayList<OfferItem> offerItems) {
        this.context = context;
        this.offerItems = offerItems;
    }

    @Override
    public int getCount() {
        return this.offerItems.size();
    }

    @Override
    public Object getItem(int i) {
        return this.offerItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_offer, viewGroup, false);
        }

        OfferItem item = this.offerItems.get(i);

        AppCompatTextView from = view.findViewById(R.id.from);
        from.setText(item.getFrom());

        AppCompatTextView offerValue = view.findViewById(R.id.offerValue);
        offerValue.setText(item.getOfferValue());

        return view;
    }
}
