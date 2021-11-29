package com.example.dcardtry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText accountInput, passwordInput;
    Button login_btn, register_btn;
    String email, password;
    private static final String URL_LOGIN = "https://fathomless-fjord-03751.herokuapp.com/api/login";
    SharedPreferences mPreferences;
    String sharedprofFile = "com.protocoderspoint.registration_login";
    SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountInput = findViewById(R.id.accountInput);
        passwordInput = findViewById(R.id.passwordInput);
        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);

        login_btn.setOnClickListener(v -> {
            email = accountInput.getText().toString().trim();
            password = passwordInput.getText().toString().trim();
            login();
        });
    }

    public void login() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                response -> {
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        Toast.makeText(getApplicationContext(),"Logged In  Success" + success,Toast.LENGTH_LONG).show();
                        Intent i = new Intent(LoginActivity.this,HomePage.class);
                        startActivity(i);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Login Error", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
                    Toast.makeText(getApplicationContext(),"Login Error", Toast.LENGTH_LONG).show();
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}