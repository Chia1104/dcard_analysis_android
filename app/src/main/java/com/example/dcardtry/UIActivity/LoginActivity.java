package com.example.dcardtry.UIActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dcardtry.HttpsTrustManager;
import com.example.dcardtry.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText accountInput, passwordInput;
    Button login_btn, registerpage_btn;
    String email, password;
    private static final String URL_LOGIN = "https://dcard-analysis-laravel-fdqsyjapma-de.a.run.app/api/login";
    SharedPreferences mPreferences;
    String sharedprofFile = "com.protocoderspoint.registration_login";
    SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        Objects.requireNonNull(getSupportActionBar()).hide(); // hide the title bar
        setContentView(R.layout.activity_login);

        mPreferences=getSharedPreferences(sharedprofFile,MODE_PRIVATE);
        preferencesEditor = mPreferences.edit();

        accountInput = findViewById(R.id.accountInput);
        passwordInput = findViewById(R.id.passwordInput);
        login_btn = findViewById(R.id.login_btn);
        registerpage_btn = findViewById(R.id.registerpage_btn);

        login_btn.setOnClickListener(v -> {
            email = accountInput.getText().toString().trim();
            password = passwordInput.getText().toString().trim();
            login();
        });

        registerpage_btn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    public void login() {
        HttpsTrustManager.allowAllSSL();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
                        if (message.equals("success")) {
                            String token = jsonObject.getString("token");
                            String name = jsonObject.getString("name");
                            preferencesEditor.putString("token", token);
                            preferencesEditor.putString("name", name);
                            preferencesEditor.apply();
                            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                            Intent i1 = new Intent(LoginActivity.this, HomePage.class);
                            startActivity(i1);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "登入失敗", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "登入失敗", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            if (error.networkResponse.statusCode == 401) {
                Toast.makeText(getApplicationContext(), "登入失敗", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Could not fetch!", Toast.LENGTH_LONG).show();
            }
        }) {
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Accept-Encoding", "gzip, deflate, br");
                params.put("Accept", "application/json");
                params.put("Conection", "keep-alive");
                return params;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}