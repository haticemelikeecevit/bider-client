package com.hatice.productlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hatice.Constants;
import com.hatice.R;
import com.hatice.login.LoginActivity;
import com.hatice.login.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

    private ProductListAdapter productListAdapter;
    private ArrayList<ProductItem> productItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        setTitle("Products");

        productItems = new ArrayList<>();

        /*String imgUrl = "https://img-trendyol.mncdn.com/assets/product/images2/20190920/8/251784/56055961/11/11_org.jpg";
        for (int i = 0; i<25;i++) {
            productItems.add(new ProductItem(imgUrl,"Product Name " + i,"Product Description " + i,"10 TL"));
        }*/

        productListAdapter = new ProductListAdapter(this,productItems);

        GridView gridView = findViewById(R.id.gridView);
        gridView.setAdapter(productListAdapter);

        ProductListThread fetchProductList = new ProductListThread();
        fetchProductList.start();
    }

    class ProductListThread extends Thread {

        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;

        @Override
        public void run() {
            Log.d("Info","Start");
            try {
                socket = new Socket(Constants.URL, Constants.PORT);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                JSONObject requestObject = new JSONObject();
                requestObject.put("type","GET_PRODUCTS");

                writer.write(requestObject.toString());
                writer.newLine();
                writer.flush();

                String responseString;
                while ((responseString = reader.readLine()) != null) {
                    Log.d("Info","Response String: " + responseString);

                    JSONObject response = new JSONObject(responseString);
                    JSONArray productsArray = response.getJSONArray("products");
                    for(int i = 0; i<productsArray.length(); i++) {
                        JSONObject productObject = productsArray.getJSONObject(i);
                        String name = productObject.getString("name");
                        String description = productObject.getString("description");
                        String price = productObject.getString("price");
                        String imageUrl = productObject.getString("imageUrl");
                        productItems.add(new ProductItem(imageUrl,name,description,price));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            productListAdapter.notifyDataSetChanged();
                        }
                    });
                }

                socket.close();
                reader.close();
                writer.close();


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
