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
import android.widget.LinearLayout;
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

public class ArticlePage extends AppCompatActivity {
    private  List<GetDcardInfo> dcardList;
    RecyclerView Article_Summary;
    ArticleSummaryAdapter AS_Adapter;
    private static final String DCARD_URL = "http://192.168.56.1:13306/GetData4.php";
    private DrawerLayout drawerLayout;
    String Name,Job,Account,Password;//接收帳號相關資料
    TextView DM_Tilte;//側邊選單標題 : 姓名+職稱

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_article_page );
        //設定隱藏標題
        getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_FULLSCREEN);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        Article_Summary = findViewById(R.id.ArticlePage_RecyclerView);
        dcardList = new ArrayList<>();
        AP_LoadDcard();

        //取得傳遞過來的資料
        Intent intent = this.getIntent();
        Name = intent.getStringExtra("name");
        Job = intent.getStringExtra( "job" );
        Account = intent.getStringExtra( "account" );
        Password = intent.getStringExtra("password");

        //加上側邊選單姓名、職稱
        DM_Tilte=findViewById( R.id.drawer_menu_title );
        DM_Tilte.setText( Name+"\n"+Job+"\t\t 您好" );
    }

    private void AP_LoadDcard(){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, DCARD_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject dcardObject = response.getJSONObject(i);
                        GetDcardInfo dcard = new GetDcardInfo();
                        dcard.setTitle(dcardObject.getString("Title"));
                        dcard.setDate(dcardObject.getString("Date"));
                        dcard.setContent(dcardObject.getString("Content"));
                        dcardList.add(dcard);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Article_Summary.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                AS_Adapter = new  ArticleSummaryAdapter(getApplicationContext(), dcardList, dcardList.size() );
                Article_Summary.setAdapter(AS_Adapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(jsonArrayRequest);

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

    public void ClickHome(View view){
        //Restart activity_home_page.xml
        redirectActivity(this,HomePage.class);
    }

    public void ClickArticle(View view){
        //Redirect(重定向) activity to articlePage
        closeDrawer(drawerLayout);
    }

    public void ClickChart(View view){
        //Redirect(重定向) activity to chartPage
        //redirectActivity(this,);
    }

    public void ClickAccountInfo(View view){
        //Redirect(重定向) activity to accountPage(帳號管理頁面)
        redirectActivity(this,UserChangePassword.class);
    }

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

    @Override
    protected void onPause(){
        super.onPause();
        closeDrawer(drawerLayout);
    }
    //側邊選單code End

}