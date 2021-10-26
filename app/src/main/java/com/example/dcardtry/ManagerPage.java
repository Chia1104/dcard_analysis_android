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

    private static final String ACCOUNT_URL = "http://192.168.56.1:13306/Account_infoTable.php";
    private List<GetAccountInfo> accountList;
    RecyclerView accountRecyclerView;
    AccountAdapter accountAdapter;
    private DrawerLayout drawerLayout;

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

    public void ClickAccountInfo(View view){

    }

    public void ClickLogout(View view){
        //回到登入頁面
        logout(this);
    }

    public void redirectActivity(Activity activity, Class aClass){
        //導到其他頁面
        //Initialize intent
        Intent intent=new Intent(activity,aClass);
        //set flag
        //intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
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