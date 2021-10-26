package com.example.dcardtry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{
    String name, account, password, jobTitle;
    TextView result;
    private List<GetAccountInfo> LoginAccountList;
    private static final String ACCOUNT_URL = "http://192.168.56.1:13306/Account_infoTable.php";

    EditText accountInput, passwordInput;
    String[]NameList,AccountList,JobList,PasswordList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //設定隱藏標題
        getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        /*argb(66, 255, 255, 0)半透明霧面*/

        LoginAccountList = new ArrayList<>();

        loadAccount();
    }

    public void gotoM(View view) {
        Intent registeredPage = new Intent(MainActivity.this, Manager_entry.class);
        startActivity(registeredPage);
    }

    private void loadAccount(){
        RequestQueue accountqueue = Volley.newRequestQueue(this);
        JsonArrayRequest accountJsonArray = new JsonArrayRequest( Request.Method.GET, ACCOUNT_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject accountObject = response.getJSONObject(i);
                        GetAccountInfo account = new GetAccountInfo();
                        account.setName(accountObject.getString("Name"));
                        account.setPassword(accountObject.getString("Password"));
                        account.setjobTitle(accountObject.getString("Job"));
                        account.setMail(accountObject.getString("Mail"));
                        LoginAccountList.add(account);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        accountqueue.add(accountJsonArray);

    }

    public void login(View view) {
        accountInput = findViewById(R.id.accountInput);
        String acc = " "+accountInput.getText().toString();
        passwordInput = findViewById(R.id.passwordInput);
        String pass = " "+passwordInput.getText().toString();
        result = findViewById(R.id.loginresult);


        int k=LoginAccountList.size();
        String s=Integer.toString( k );
        result.setText( s );
        NameList = new String[k];
        AccountList = new String[k];
        PasswordList = new String[k];
        JobList = new String[k];

        for(int a=0;a<k;a++){
            NameList[a]=LoginAccountList.get(a).getName();
            AccountList[a]=LoginAccountList.get(a).getMail();
            PasswordList[a]=LoginAccountList.get(a).getPassword();
            JobList[a]=LoginAccountList.get(a).getjobTitle();
        }

        for(int f=0;f<k;f++) {
            account = AccountList[f];
            password = PasswordList[f];
            name=NameList[f];
            jobTitle=JobList[f];

            if (acc.equals( account )) {
                if (pass.equals( password )) {
                    result.setText( name +  "\t\t" +jobTitle + "\t\t\t" + "您好" );
                    //跳轉到首頁
                    Intent registeredPage = new Intent(MainActivity.this,HomePage.class);
                    registeredPage.putExtra( "name",name );
                    registeredPage.putExtra( "job",jobTitle );
                    registeredPage.putExtra( "account",account );
                    registeredPage.putExtra( "password",password );
                    startActivity(registeredPage);
                    break;
                } else {
                    result.setText( "密碼錯誤" );
                    break;
                }
            } else {result.setText( "沒有此帳號" ); }
        }
    }
}