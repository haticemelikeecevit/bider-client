package com.hatice.offer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import com.hatice.Constants;
import com.hatice.R;
import com.hatice.login.User;
import com.hatice.productlist.ProductItem;

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
import java.util.Collections;
import java.util.Comparator;

public class OfferActivity extends AppCompatActivity {

    private OfferAdapter offerAdapter;
    private ArrayList<OfferItem> offerItems;
    private User user;
    private ProductItem productItem;
    private ProgressBar progressBar;
    private TextView maxOfferTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        setTitle("Offers");

        offerItems = new ArrayList<>();
        offerAdapter = new OfferAdapter(this,offerItems);
        user = User.getUser();
        productItem = getIntent().getParcelableExtra("ProductItem");
        progressBar = findViewById(R.id.progressBar);
        maxOfferTextView = findViewById(R.id.maxOfferText);

        ListView offerListView = findViewById(R.id.offerListView);

        offerListView.setAdapter(offerAdapter);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Error");
        dialogBuilder.setMessage("You must write offer value!");
        dialogBuilder.setNeutralButton("Ok",null);

        final AlertDialog dialog = dialogBuilder.create();

        final AppCompatEditText offerValue = findViewById(R.id.offerValue);
        AppCompatImageView offerSend = findViewById(R.id.offerSend);
        offerSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String offerValueText = offerValue.getText().toString();
                if(offerValueText.length() == 0) {
                    dialog.show();
                } else {
                    Log.d("Info","Offer: " + offerValueText + " from " + user.getName());
                    CreateOfferThread createOffer = new CreateOfferThread(offerValueText);
                    createOffer.start();
                }
            }
        });

        OfferListThread fetchOfferList = new OfferListThread();
        fetchOfferList.start();
    }

    private int getMaxOfferIndex() {
        int maxIndex = -1;
        int maxOffer = Integer.MIN_VALUE;
        for (int i = 0; i<offerItems.size(); i++) {
            int currentOffer = Integer.parseInt(offerItems.get(i).getOfferValue());
            if (currentOffer > maxOffer) {
                maxOffer = currentOffer;
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    class OfferListThread extends Thread {

        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
            Log.d("Info","Start");
            try {
                socket = new Socket(Constants.URL, Constants.PORT);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                JSONObject requestObject = new JSONObject();
                JSONObject productObject = new JSONObject();
                productObject.put("name",productItem.getName());
                productObject.put("description", productItem.getDescription());
                productObject.put("price",productItem.getPrice());
                productObject.put("imageUrl",productItem.getImageUrl());
                requestObject.put("product",productObject);
                requestObject.put("type","GET_BIDS");

                writer.write(requestObject.toString());
                writer.newLine();
                writer.flush();

                String responseString;
                while ((responseString = reader.readLine()) != null) {
                    Log.d("Info","Response String: " + responseString);

                    JSONObject response = new JSONObject(responseString);
                    JSONArray offersArray = response.getJSONArray("bids");
                    for(int i = 0; i<offersArray.length(); i++) {
                        JSONObject offerObject = offersArray.getJSONObject(i);
                        String username = offerObject.getString("username");
                        String amount = offerObject.getString("amount");
                        offerItems.add(new OfferItem(amount,username));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            OfferItem maxOffer = offerItems.get(getMaxOfferIndex());
                            maxOfferTextView.setText("Maximum bid " +maxOffer.getOfferValue() +" TL from " + maxOffer.getFrom() );

                            offerAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);
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

    class CreateOfferThread extends Thread {

        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;
        private String amount;

        public CreateOfferThread(String amount) {
            this.amount = amount;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
            Log.d("Info","Start");
            try {
                socket = new Socket(Constants.URL, Constants.PORT);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                JSONObject requestObject = new JSONObject();
                JSONObject bidObject = new JSONObject();
                bidObject.put("amount",this.amount);
                bidObject.put("username", user.getName());
                bidObject.put("productName",productItem.getName());
                requestObject.put("bid",bidObject);
                requestObject.put("type","CREATE_BID");

                writer.write(requestObject.toString());
                writer.newLine();
                writer.flush();

                String responseString;
                while ((responseString = reader.readLine()) != null) {
                    JSONArray bidArray = new JSONArray(responseString);
                    offerItems.clear();
                    for(int i = 0; i<bidArray.length(); i++) {
                        JSONObject offerObject = bidArray.getJSONObject(i);
                        String username = offerObject.getString("username");
                        String amount = offerObject.getString("amount");
                        offerItems.add(new OfferItem(amount,username));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            OfferItem maxOffer = offerItems.get(getMaxOfferIndex());
                            maxOfferTextView.setText("Maximum bid " +maxOffer.getOfferValue() +" TL from " + maxOffer.getFrom() );

                            offerAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }

                socket.close();
                reader.close();
                writer.close();


            } catch (IOException | JSONException e) {
                // Error
                e.printStackTrace();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
}
