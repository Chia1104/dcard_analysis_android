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

public class RegisterActivity extends AppCompatActivity {
    EditText nameInput, emailInput, passwordInput2, cpasswordInput;
    String name, email, password, cpassword;
    Button loginpage_btn, register_btn;
    private static final String URL_REGISTER = "https://dcardanalysislaravel-sedok4caqq-de.a.run.app/api/register";
    SharedPreferences mPreferences;
    String sharedprofFile = "com.protocoderspoint.registration_login";
    SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);

        mPreferences=getSharedPreferences(sharedprofFile,MODE_PRIVATE);
        preferencesEditor = mPreferences.edit();

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput2 = findViewById(R.id.passwordInput2);
        cpasswordInput = findViewById(R.id.cpasswordInput);
        loginpage_btn = findViewById(R.id.loginpage_btn);
        register_btn = findViewById(R.id.register_btn);

        loginpage_btn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
        register_btn.setOnClickListener(v -> {
            name = nameInput.getText().toString().trim();
            email = emailInput.getText().toString().trim();
            password = passwordInput2.getText().toString().trim();
            cpassword = cpasswordInput.getText().toString().trim();
            register();
        });
    }

    public void register() {
        HttpsTrustManager.allowAllSSL();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER,
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
                            Intent i = new Intent(RegisterActivity.this, HomePage.class);
                            startActivity(i);
                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }, error -> {

            if (error.networkResponse.statusCode == 401) {
                Toast.makeText(getApplicationContext(), "註冊錯誤!", Toast.LENGTH_LONG).show();
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
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("c_password", cpassword);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}