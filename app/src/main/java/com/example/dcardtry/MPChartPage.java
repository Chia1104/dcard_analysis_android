package com.example.dcardtry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MPChartPage extends AppCompatActivity {

    PieChart pieChart;
    String DCARD_URL;
    private static final String UPDATE_DCARD_URL = "https://dcardanalysislaravel-sedok4caqq-de.a.run.app/date/";
    private static final String TODAY_DCARD_URL = "https://dcardanalysislaravel-sedok4caqq-de.a.run.app/date/today";
    private static final String MONTH_DCARD_URL = "https://dcardanalysislaravel-sedok4caqq-de.a.run.app/date/month";
    private static final String WEEK_DCARD_URL = "https://dcardanalysislaravel-sedok4caqq-de.a.run.app/date/week";
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
    String  date1, date2, pname;//接收登入頁面傳過來的資料
    TextView DM_Tilte, date1_txt, date2_txt;//側邊選單標題 : 姓名+職稱
    private DrawerLayout drawerLayout;
    Button getToday_btn, getWeek_btn, getMonth_btn, search_btn;
    SharedPreferences mPreferences;
    String sharedprofFile = "com.protocoderspoint.registration_login";
    static SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpchart_page);

        //設定隱藏標題
        getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        DM_Tilte=findViewById( R.id.drawer_menu_title );
        details();

        progressBar = findViewById(R.id.progressBar);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        date1_txt = findViewById(R.id.date1_txt);
        date1_txt.setOnClickListener(v -> {
            date1Picker();
        });
        date2_txt = findViewById(R.id.date2_txt);
        date2_txt.setOnClickListener(v -> {
            date2Picker();
        });
        search_btn = findViewById(R.id.search_btn);
        search_btn.setOnClickListener(v -> {
            updateDcard();
        });
        nowMonth();

        dcardList = new ArrayList<>();
        chartValue = new ArrayList<>();

        DCARD_URL = MONTH_DCARD_URL;
        loadDcardWithVolley();
        getToday_btn = findViewById(R.id.getToday_btn);
        getToday_btn.setOnClickListener(v -> {
            DCARD_URL = TODAY_DCARD_URL;
            loadDcardWithVolley();
        });

        getWeek_btn = findViewById(R.id.getWeek_btn);
        getWeek_btn.setOnClickListener(v -> {
            DCARD_URL = WEEK_DCARD_URL;
            loadDcardWithVolley();
        });

        getMonth_btn = findViewById(R.id.getMonth_btn);
        getMonth_btn.setOnClickListener(v -> {
            DCARD_URL = MONTH_DCARD_URL;
            loadDcardWithVolley();
        });
    }

    public void details() {
        mPreferences = getSharedPreferences(sharedprofFile,MODE_PRIVATE);
        preferencesEditor = mPreferences.edit();
        pname = mPreferences.getString("name","null");
        DM_Tilte.setText("Hello " + pname);
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
        progressBar.setVisibility(View.VISIBLE);
        HttpsTrustManager.allowAllSSL();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, DCARD_URL, null, response -> {
            try {
                dcardList.clear();
                chartValue.clear();
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
                        case "null":
                            dcard.setSaclassnum("3.0");
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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MPChartPage.this, "文章未更新",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MPChartPage.this, "文章未更新",Toast.LENGTH_LONG).show();
            error.printStackTrace();
        });
        queue.add(jsonArrayRequest);
    }

    public void updateDcard(){
        date1 = date1_txt.getText().toString();
        date2 = date2_txt.getText().toString();
        DCARD_URL = UPDATE_DCARD_URL;
        progressBar.setVisibility(View.VISIBLE);
        HttpsTrustManager.allowAllSSL();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, DCARD_URL + date1 + "/" + date2, null, response -> {
            try {
                dcardList.clear();
                chartValue.clear();
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
                        case "null":
                            dcard.setSaclassnum("3.0");
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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MPChartPage.this, "文章未更新",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MPChartPage.this, "文章未更新",Toast.LENGTH_LONG).show();
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

    public void nowMonth() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat m0d1Format = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat m0d31Format = new SimpleDateFormat("yyyy-MM-dd");
        date1_txt.setText(m0d1Format.format(calendar.getTime()) + "-01");
        date2_txt.setText(m0d31Format.format(calendar.getTime()));
    }

    public void date1Picker(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, day1) -> {
            month1 += 1;
            String month2, day2;
            if (month1 < 10) {
                month2 = "0";
            } else {
                month2 = "";
            }
            if (day1 < 10) {
                day2 = "0";
            } else {
                day2 = "";
            }
            String dateTime = year1 +"-"+ month2 + month1 +"-"+ day2 + day1;
            date1_txt.setText(dateTime);
        }, year, month, day).show();
    }

    public void date2Picker(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, day1) -> {
            month1 += 1;
            String month2, day2;
            if (month1 < 10) {
                month2 = "0";
            } else {
                month2 = "";
            }
            if (day1 < 10) {
                day2 = "0";
            } else {
                day2 = "";
            }
            String dateTime = year1 +"-"+ month2 + month1 +"-"+ day2 + day1;
            date2_txt.setText(dateTime);
        }, year, month, day).show();
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
        finish();
    }

    public void ClickArticle(View view){
        //Redirect(重定向) activity to articlePage
        redirectActivity( this,ArticlePage.class );
        finish();
    }

    public void ClickChart(View view){
        //Redirect(重定向) activity to chartPage
        closeDrawer(drawerLayout);
    }

    public void ClickTrend(View view){
        //Redirect(重定向) activity to chartPage
        redirectActivity(this,MoreBarChart.class);
        finish();
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
                preferencesEditor.clear().commit();
                //Finish activity
                activity.finishAffinity();
                //回到登入頁面
                Intent intent=new Intent(activity,LoginActivity.class);
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