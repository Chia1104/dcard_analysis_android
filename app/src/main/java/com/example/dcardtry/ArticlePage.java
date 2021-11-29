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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArticlePage extends AppCompatActivity {
    private  List<Dcard> dcardList;
    RecyclerView ArticleRecyclerview;
    Adapter adapter;
    private static final String DCARD_URL = "https://fathomless-fjord-03751.herokuapp.com/getAllDcard";
    private static final String UPDATE_DCARD_URL = "https://fathomless-fjord-03751.herokuapp.com/getAllDcard/before/";
    private DrawerLayout drawerLayout;
    String Name,Job,Account,Password, rvitemId;//接收帳號相關資料
    TextView DM_Tilte;//側邊選單標題 : 姓名+職稱
    ProgressBar progressBar;
    Button searchArticle_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_article_page );
        //設定隱藏標題
        getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_FULLSCREEN);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        searchArticle_btn = findViewById(R.id.searchArticle_btn);
        searchArticle_btn.setOnClickListener(v -> {
            redirectActivity(this, SearchArticlePage.class);
        });

        ArticleRecyclerview = findViewById(R.id.ArticlePage_RecyclerView);
        dcardList = new ArrayList<>();
        AP_LoadDcard();
        updateDcard();

        //取得傳遞過來的資料
        Intent intent = this.getIntent();
        Name = intent.getStringExtra("name");
        Job = intent.getStringExtra( "job" );
        Account = intent.getStringExtra( "account" );
        Password = intent.getStringExtra("password");

        //加上側邊選單姓名、職稱
        DM_Tilte=findViewById( R.id.drawer_menu_title );
        DM_Tilte.setText( Name+"\n"+Job+"\t\t 您好" );
        progressBar = findViewById(R.id.progressBar);
    }

    private void AP_LoadDcard(){
//        progressBar.setVisibility(View.VISIBLE);
        HttpsTrustManager.allowAllSSL();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, DCARD_URL, null, response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject dcardObject = response.getJSONObject(i);
                    Dcard dcard = new Dcard();
                    dcard.setSascore(dcardObject.getString("SA_Score"));
                    dcard.setSaclass(dcardObject.getString("SA_Class"));
                    dcard.setTitle(dcardObject.getString("Title"));
                    dcard.setDate(dcardObject.getString("CreatedAt"));
                    dcard.setContent(dcardObject.getString("Content"));
                    dcard.setId(dcardObject.getString("Id"));
                    dcard.setLv1(dcardObject.getString("KeywordLevel1"));
                    dcard.setLv2(dcardObject.getString("KeywordLevel2"));
                    dcard.setLv3(dcardObject.getString("KeywordLevel3"));
                    dcardList.add(dcard);
                    if (i == 29) {
                        rvitemId = dcardObject.getString("Id");
                    }
                }
                ArticleRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new Adapter(getApplicationContext(), dcardList);
                ArticleRecyclerview.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ArticlePage.this, "" + e.getMessage(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ArticlePage.this, "" + error.getMessage(),Toast.LENGTH_LONG).show();
            error.printStackTrace();
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void updateDcard(){
        ArticleRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    progressBar.setVisibility(View.VISIBLE);
                    HttpsTrustManager.allowAllSSL();
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, UPDATE_DCARD_URL + rvitemId, null, response -> {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject dcardObject = response.getJSONObject(i);
                                Dcard dcard = new Dcard();
                                dcard.setSascore(dcardObject.getString("SA_Score"));
                                dcard.setSaclass(dcardObject.getString("SA_Class"));
                                dcard.setTitle(dcardObject.getString("Title"));
                                dcard.setDate(dcardObject.getString("CreatedAt"));
                                dcard.setContent(dcardObject.getString("Content"));
                                dcard.setId(dcardObject.getString("Id"));
                                dcard.setLv1(dcardObject.getString("KeywordLevel1"));
                                dcard.setLv2(dcardObject.getString("KeywordLevel2"));
                                dcard.setLv3(dcardObject.getString("KeywordLevel3"));
                                dcardList.add(dcard);
                                if (i == 29) {
                                    rvitemId = dcardObject.getString("Id");
                                }
                            }
                            ArticleRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            adapter = new Adapter(getApplicationContext(), dcardList);
                            ArticleRecyclerview.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ArticlePage.this, "" + e.getMessage(),Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }, error -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ArticlePage.this, "" + error.getMessage(),Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    });
                    requestQueue.add(jsonArrayRequest);
                }
            }
        });
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
        redirectActivity(this,MPChartPage.class);
    }

    public void ClickTrend(View view){
        //Redirect(重定向) activity to chartPage
        redirectActivity(this,MoreBarChart.class);
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