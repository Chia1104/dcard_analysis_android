package com.example.dcardtry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class ManagerPage extends AppCompatActivity {

    private static final String ACCOUNT_URL = "https://cguimfinalproject-test.herokuapp.com/Account_infoTable.php";
    private List<GetAccountInfo> accountList;
    RecyclerView accountRecyclerView;
    AccountAdapter accountAdapter;
    private DrawerLayout drawerLayout;
    String Name,Job,Account,Password;//接收登入頁面傳過來的資料
    TextView MDM_Tilte;//側邊選單標題 : 姓名+職稱

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_manager_page );

        //設定隱藏標題
        getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_FULLSCREEN);

        accountRecyclerView=findViewById( R.id.Account_List_RecycleView );
        accountList= new ArrayList<>();
        loadAccount();

        drawerLayout = (DrawerLayout) findViewById(R.id.managerDrawerLayout);

        //取得傳遞過來的資料
        Intent intent = this.getIntent();
        Name = intent.getStringExtra("name");
        Job = intent.getStringExtra( "job" );
        Account = intent.getStringExtra( "account" );
        Password = intent.getStringExtra("password");

        MDM_Tilte=findViewById( R.id.manager_drawer_menu_title );
        MDM_Tilte.setText( "\t"+Name+"\n"+Job+"\t\t 您好" );
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
                        account.setjobTitle(accountObject.getString("Job"));
                        account.setMail(accountObject.getString("Mail"));
                        accountList.add(account);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                accountRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                accountAdapter = new  AccountAdapter(getApplicationContext(), accountList);
                accountRecyclerView.setAdapter(accountAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        accountqueue.add(accountJsonArray);

    }

    //側邊選單code Strat
    public void ClickMenu(View view){
        openDrawer(drawerLayout);
    }

    public void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer( GravityCompat.START );
    }

    public void ClickCancle(View view){
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        //check condition
        if(drawerLayout.isDrawerOpen( GravityCompat.START )){
            //when drawer is open
            drawerLayout.closeDrawer( GravityCompat.START );
        }
    }

    public void ClickAddUser(View view){
        redirectActivity( this,Manager_AddAccount.class );
    }

    public void ClickManagerHome(View view){
        redirectActivity( this,ManagerPage.class );
    }

    public void Click_M_ChangePassword(View view){ redirectActivity( this,ManagerChangePassword.class ); }

    public void ClickLogout(View view){
        //回到登入頁面
        logout(this);
    }

    public void redirectActivity(Activity activity, Class aClass){
        //導到其他頁面
        //Initialize intent
        Intent intent=new Intent(activity,aClass);
        intent.putExtra( "name",Name );
        intent.putExtra( "job",Job );
        intent.putExtra( "account",Account );
        intent.putExtra( "password",Password );
        //start activity
        activity.startActivity(intent);
    }

    public static void logout(Activity activity){
        //Initialize alert dialog
        AlertDialog.Builder builder=new AlertDialog.Builder( activity );
        //set title
        builder.setTitle( "登出提醒" );
        //set message
        builder.setMessage( "確定要登出嗎?" );
        //Positive yes button
        builder.setPositiveButton( "是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Finish activity
                activity.finishAffinity();
                //回到登入頁面
                Intent intent=new Intent(activity,MainActivity.class);
                activity.startActivity( intent );
            }
        } );
        //Negative no button
        builder.setNegativeButton( "否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss dialog
                dialog.dismiss();
            }
        } );
        builder.show();
    }

    //側邊選單code End
}