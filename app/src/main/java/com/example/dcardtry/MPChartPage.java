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
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MPChartPage extends AppCompatActivity {

    PieChart pieChart;
    String DCARD_URL;
    private static final String APR_DCARD_URL = "https://cguimfinalproject-test.herokuapp.com/GetData5.php";
    private static final String TODAY_DCARD_URL = "https://cguimfinalproject-test.herokuapp.com/getTodayDcard.php";
    private static final String MONTH_DCARD_URL = "https://cguimfinalproject-test.herokuapp.com/getMonthDcard.php";
    private static final String WEEK_DCARD_URL = "https://cguimfinalproject-test.herokuapp.com/getWeekDcard.php";
    private static final String elementToFound_pos = "Positive";
    private static final String elementToFound_neu = "Neutral";
    private static final String elementToFound_neg = "Negative";
    List<Dcard> dcardList;
    List<String> chartValue;
    Integer neg, neu, pos;
    RecyclerView mRecyclerView;
    Adapter adapter;
    RecyclerView.LayoutManager mLayoutManager;
    ProgressBar progressBar;
    String Name,Job,Account,Password;//接收登入頁面傳過來的資料
    TextView DM_Tilte;//側邊選單標題 : 姓名+職稱
    private DrawerLayout drawerLayout;
    String twm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpchart_page);

        //設定隱藏標題
        getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        //取得傳遞過來的資料
        Intent intent = this.getIntent();
        Name = intent.getStringExtra("name");
        Job = intent.getStringExtra( "job" );
        Account = intent.getStringExtra( "account" );
        Password = intent.getStringExtra("password");

        //加上側邊選單姓名、職稱
        DM_Tilte=findViewById( R.id.drawer_menu_title );
        DM_Tilte.setText( "\t"+Name+"\n"+Job+"\t\t 您好" );

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        progressBar = findViewById(R.id.progressBar);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        dcardList = new ArrayList<>();
        chartValue = new ArrayList<>();

        twm = getIntent().getStringExtra("twm");
        if (twm == "today") {
            DCARD_URL = TODAY_DCARD_URL;
            loadDcardWithVolley();
        } else if (twm == "week") {
            DCARD_URL = WEEK_DCARD_URL;
            loadDcardWithVolley();
        } else if (twm == "month") {
            DCARD_URL = MONTH_DCARD_URL;
            loadDcardWithVolley();
        } else {
            DCARD_URL = APR_DCARD_URL;
            loadDcardWithVolley();
        }
    }

    private void filter1(String text) {
        ArrayList<Dcard> filteredList1 = new ArrayList<>();

        for (Dcard item : dcardList) {
            if (item.getSaclassnum().toLowerCase().contains(text.toLowerCase())) {
                filteredList1.add(item);
            }
        }
        adapter.filterList1(filteredList1);
    }

    public void loadDcardWithVolley(){
        HttpsTrustManager.allowAllSSL();
        RequestQueue queue = Volley.newRequestQueue(this);
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
                    switch (dcardObject.getString("SA_Class")){
                        case "Positive":
                            dcard.setSaclassnum("2.0");
                            break;
                        case "Neutral":
                            dcard.setSaclassnum("0.0");
                            break;
                        case "Negative":
                            dcard.setSaclassnum("1.0");
                            break;
                    }
                    dcardList.add(dcard);
                    chartValue.add(dcardObject.getString("SA_Class"));
                }
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new Adapter(getApplicationContext(), dcardList);
                mRecyclerView.setAdapter(adapter);
                int posCount = Collections.frequency(chartValue, elementToFound_pos);
                int neuCount = Collections.frequency(chartValue, elementToFound_neu);
                int negCount = Collections.frequency(chartValue, elementToFound_neg);

                pos = posCount;
                neu = neuCount;
                neg = negCount;

                showPieChart();
                progressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                Toast.makeText(MPChartPage.this, e.getMessage(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(MPChartPage.this, error.getMessage(),Toast.LENGTH_LONG).show();
            error.printStackTrace();
        });
        queue.add(jsonArrayRequest);
    }

    public void showPieChart(){
        pieChart = findViewById(R.id.pieChart_view);
        pieChart.getDescription().setEnabled(false);

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";

        //initializing data
        Map<String, Integer> typeAmountMap = new HashMap<>();
        typeAmountMap.put(elementToFound_pos,pos);
        typeAmountMap.put(elementToFound_neu,neu);
        typeAmountMap.put(elementToFound_neg,neg);

        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.neuColor));
        colors.add(getResources().getColor(R.color.negColor));
        colors.add(getResources().getColor(R.color.posColor));

        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(Objects.requireNonNull(typeAmountMap.get(type)).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true);

//        pieChart.setDrawSliceText(false);
        pieChart.setData(pieData);
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                e.getData();
                String txt = String.valueOf(h.getX());
                filter1(txt);
            }

            @Override
            public void onNothingSelected() {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new Adapter(getApplicationContext(), dcardList);
                mRecyclerView.setAdapter(adapter);
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