package com.example.dcardtry;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

public class Manager_entry extends AppCompatActivity {
 EditText MAInput,MPInput;
 TextView MLR;
     private String[]AccountList,PasswordList,NameList,JobList,AdministratorList;
     private boolean[] a;
     private String account, password,name,jobTitle,administrator;
     private boolean x;

    private static final String ACCOUNT_URL = "https://cguimfinalproject-test.herokuapp.com/Account_infoTable.php";
    private List<GetAccountInfo> MLoginAccountList;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_entry);

        //設定隱藏標題
        getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        MAInput=findViewById(R.id.ManagerAccountInput);
        MPInput=findViewById(R.id.ManagerPasswordInput);
        MLR=findViewById(R.id.MLResult);

        MLoginAccountList = new ArrayList<>();
        loadAccount();
    }

    private void loadAccount(){
        HttpsTrustManager.allowAllSSL();
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
                        account.setAdministrator( accountObject.getString("Administrator") );// 此處有問題
                        MLoginAccountList.add(account);
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


    public  void mlb(View view){
        String ac=MAInput.getText().toString();
        String ps=MPInput.getText().toString();

        int k=MLoginAccountList.size();
        AccountList = new String[k];
        PasswordList = new String[k];
        AdministratorList =new String[k];
        NameList = new String[k];
        JobList = new String[k];

        for(int a=0;a<k;a++){
            AccountList[a]=MLoginAccountList.get(a).getMail();
            PasswordList[a]=MLoginAccountList.get(a).getPassword();
            AdministratorList[a]=MLoginAccountList.get(a).getAdministrator();
            NameList[a]=MLoginAccountList.get(a).getName();
            JobList[a]=MLoginAccountList.get(a).getjobTitle();
        }
        String s=Integer.toString( k );
        MLR.setText( s );
        for(int f=0;f<k;f++) {

            account = AccountList[f];
            password = PasswordList[f];
            administrator = AdministratorList[f];
            name = NameList[f];
            jobTitle = JobList[f];


if(ac.equals( "" )){MLR.setText( "請輸入帳號" );break;}
else{
                    if (ac.equals( account )) {
                        if (ps.equals( "" )){MLR.setText( "請輸入密碼" );break;}
                        else{
                        if (ps.equals( password )) {

                                if(administrator.equals( "1" )){
                                MLR.setText( name +  "\t\t" +jobTitle + "\t\t\t" + "您好" );
                                //跳轉到管理者首頁
                                Intent registeredPage = new Intent(Manager_entry.this,ManagerPage.class);
                                registeredPage.putExtra( "name",name );
                                registeredPage.putExtra( "job",jobTitle );
                                registeredPage.putExtra( "account",account );
                                registeredPage.putExtra( "password",password );
                                startActivity(registeredPage);
                                break;
                                }
                                else{ MLR.setText( "非管理者" );break;}

                            }
                            else { MLR.setText( "密碼錯誤" );break;}
                        }
                    } else {MLR.setText( "查無此帳號" );}}

        }
    }

    public void gotoUentry(View view){
        Intent gouser=new Intent(this,MainActivity.class);
        startActivity( gouser );
    }
}
