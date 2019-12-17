package com.hatice.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hatice.Constants;
import com.hatice.R;
import com.hatice.productlist.ProductListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        final EditText userName = findViewById(R.id.et_username);
        final EditText password = findViewById(R.id.et_password);
        Button button = findViewById(R.id.btn_login);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin(userName.getText().toString(),password.getText().toString());
            }
        });

    }

    private void showLoginError(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setTitle("Error");
        dialogBuilder.setMessage(message);
        dialogBuilder.setNeutralButton("Ok",null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void userLogin(String username,String password) {
        if (username.length() == 0 || password.length() == 0) {
            // Show error message
            showLoginError("You must fill all the fields!");
        } else {
            LoginRequestThread loginRequest = new LoginRequestThread(username,password);
            loginRequest.start();
        }
    }

    class LoginRequestThread extends Thread {

        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;

        private String username;
        private String password;

        public LoginRequestThread(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public void run() {
            Log.d("Info","Start");
            try {
                socket = new Socket(Constants.URL, Constants.PORT);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


                JSONObject requestObject = new JSONObject();
                JSONObject userObject = new JSONObject();
                userObject.put("username", this.username);
                userObject.put("password", this.password);
                requestObject.put("user",userObject);
                requestObject.put("type","LOGIN");

                Log.d("Info","JSON String" + requestObject.toString());

                writer.write(requestObject.toString());
                writer.newLine();
                writer.flush();

                String responseString;
                while ((responseString = reader.readLine()) != null) {
                    Log.d("Info","Response String: " + responseString);

                    JSONObject response = new JSONObject(responseString);
                    int loginStatus = response.getInt("login");

                    Log.d("Info","Login status: " + loginStatus);

                    if (loginStatus == 0) {
                        // unsuccess
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showLoginError("Username or password is wrong!");
                            }
                        });
                    } else if (loginStatus == 1){
                        // Success
                        User.setUser(new User(this.username));

                        Intent intent = new Intent(LoginActivity.this, ProductListActivity.class);
                        startActivity(intent);
                    }
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

