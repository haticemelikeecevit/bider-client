package com.hatice.productlist;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductItem implements Parcelable {
    private String imageUrl;
    private String name;
    private String description;
    private String price;

    public ProductItem(String imageUrl, String name, String description, String price) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.imageUrl);
        parcel.writeString(this.name);
        parcel.writeString(this.description);
        parcel.writeString(this.price);
    }

    public static final Parcelable.Creator<ProductItem> CREATOR = new Parcelable.Creator<ProductItem>() {
        public ProductItem createFromParcel(Parcel in) {
            return new ProductItem(in);
        }

        public ProductItem[] newArray(int size) {
            return new ProductItem[size];
        }
    };

    private ProductItem(Parcel in) {
        this.imageUrl = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.price = in.readString();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
