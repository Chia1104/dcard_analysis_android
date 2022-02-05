package com.example.dcardtry.UIActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dcardtry.HttpsTrustManager;
import com.example.dcardtry.R;
import com.example.dcardtry.adapter.ArticleSummaryAdapter;
import com.example.dcardtry.model.Dcard;
import com.example.dcardtry.adapter.SliderAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class HomePage extends AppCompatActivity {
    LinearLayout dotsLayout;
    SliderAdapter adapter;
    ArticleSummaryAdapter AS_Adapter;
    ViewPager2 pager2;
    PieChart pieChart;
    String DCARD_URL;
    private static final String TODAY_DCARD_URL = "https://dcard-analysis-laravel-fdqsyjapma-de.a.run.app/api/date/today";
    private static final String MONTH_DCARD_URL = "https://dcard-analysis-laravel-fdqsyjapma-de.a.run.app/api/date/month";
    private static final String WEEK_DCARD_URL = "https://dcard-analysis-laravel-fdqsyjapma-de.a.run.app/api/date/week";
    private static final String BARCHART_URL = "https://dcard-analysis-laravel-fdqsyjapma-de.a.run.app/api/GBChart4Data";
    private static final String elementToFound_pos = "Positive";
    private static final String elementToFound_neu = "Neutral";
    private static final String elementToFound_neg = "Negative";
    Integer neg, neu, pos;
    int[] list,bannerpic;
    TextView[] dots;
    String[] bannertxt;
    RecyclerView Article_Summary;
    private  List<Dcard> dcardList;
    List<String> chartValue;
    List<String> barChartValue;
    BarChart barChart;
    BarDataSet barDataSet1, barDataSet2, barDataSet3;
    ArrayList barEntries;
    private DrawerLayout drawerLayout;
    String ptoken, pname;
    TextView DM_Tilte;//側邊選單標題 : 姓名+職稱
    TextView MSAccount,MSAverage,MSKey;//本月統計用
    ProgressBar progressBar1, progressBar2, progressBar3;
    int articleCount = 0, keywordCount = 0;
    float scoreSum = 0, avgScore = 0;
    ScrollView HPScroller;
    LinearLayout HomePage_ArticleSummary;
    SharedPreferences mPreferences;
    String sharedprofFile = "com.protocoderspoint.registration_login";
    private static final String URL_DETAILS = "https://dcard-analysis-laravel-fdqsyjapma-de.a.run.app/api/details";
    static SharedPreferences.Editor preferencesEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        dotsLayout=findViewById(R.id.dots_container);
        pager2=findViewById(R.id.view_pager2);
        DM_Tilte=findViewById( R.id.drawer_menu_title );
        MSAccount=findViewById( R.id.articleAmount );
        MSAverage=findViewById( R.id.averagePoint );
        MSKey=findViewById( R.id.keyword_Match_Amount );
        Article_Summary = findViewById(R.id.Article_Summary_RecyclerView);
        HomePage_ArticleSummary = findViewById(R.id.HomePage_ArticleSummary);
        dcardList = new ArrayList<>();
        chartValue = new ArrayList<>();

        //設定隱藏標題
        getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        details();

        //頂部快捷鍵開始
        HPScroller= (ScrollView) findViewById( R.id.HomePage_ScrollView );
        Runnable runnable = new Runnable() {

            public void run() {

            }
            public void neeee(View v){

            }

        };
        //頂部快捷鍵結束

        //Banner中底部Title
        bannertxt=new String[]{"即時掌握社群訊息","精準分析找出關鍵問題","視覺化呈現大量資料"};

        //Banner中圖片
        bannerpic = new int[]{
                R.drawable.banner_pic8,
                R.drawable.banner_pic7,
                R.drawable.banner_pic2,
        };

        adapter =new SliderAdapter(list,bannertxt,bannerpic);
        pager2.setAdapter(adapter);
        pager2.setCurrentItem(99);

        dots=new TextView[3];
        dotsIndicator();

        DCARD_URL = MONTH_DCARD_URL;
        loadDcard();
        barChartValue = new ArrayList<>();
        loadBarChartValue();

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectedIndicator(position);
                super.onPageSelected(position);
            }
        });
    }

    //頂部快捷鍵開始
    public void gotoTop(View view){
        HPScroller.scrollTo( 0,0 );
    }
    public void gotoArticleSummary(View view){
        HPScroller.scrollTo( 0,1500 );
    }
    public void gotoEmotionAmount(View view){
        HPScroller.scrollTo( 0,2800 );
    }
    public void gotoEmotionTrend(View view){
        HPScroller.scrollTo( 0,4950 );
    }
    //頂部快捷鍵結束

    private void dotsIndicator() {
        for(int i=0;i<dots.length;i++){
            dots[i]=new TextView(this);
            dots[i].setText(Html.fromHtml("&#9679;"));
            dots[i].setTextSize(18);
            dotsLayout.addView(dots[i]);//設定主圖背景顏色
        }
        //dots[0].setTextColor(getResources().getColor(R.color.gray));//line:61-64暫時解決初始畫面時Banner下方圓點看不到的問題
        for(int i=0;i<=2;i++){
            dots[i].setTextColor(getResources().getColor(R.color.gray));
        }
    }

    private void selectedIndicator(int position) {
        for(int i=0;i<dots.length;i++){
            if(i==position%3){
                dots[i].setTextColor(getResources().getColor(R.color.black));
            }
            else{
                dots[i].setTextColor(getResources().getColor(R.color.gray));
            }
        }
    }

    public void BannerBntToRight(View view){pager2.setCurrentItem(pager2.getCurrentItem()+1);}
    public void BannerBntToLeft(View view){pager2.setCurrentItem(pager2.getCurrentItem()-1);}

    public void details() {
        mPreferences = getSharedPreferences(sharedprofFile,MODE_PRIVATE);
        preferencesEditor = mPreferences.edit();
        ptoken = mPreferences.getString("token","null");
        HttpsTrustManager.allowAllSSL();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DETAILS,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");

                        if (message.equals("success")) {
                            String name = jsonObject.getString("name");
                            DM_Tilte.setText("Hello " + name);
                        }

                    } catch (Exception e) {
                        preferencesEditor.clear().commit();
                        Toast.makeText(getApplicationContext(), "請重新登入", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(HomePage.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, error -> {
            if (error.networkResponse.statusCode == 401) {
                preferencesEditor.clear().commit();
                Toast.makeText(getApplicationContext(), "請重新登入", Toast.LENGTH_LONG).show();
                Intent i = new Intent(HomePage.this, LoginActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Could not fetch!", Toast.LENGTH_LONG).show();
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Accept-Encoding", "gzip, deflate, br");
                params.put("Accept", "application/json");
                params.put("Conection", "keep-alive");
                params.put("Authorization", "Bearer " + ptoken);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void loadDcard(){
        progressBar1.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.VISIBLE);
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
                    dcardList.add(dcard);
                    chartValue.add(dcardObject.getString("SA_Class"));
                    if (dcardObject.getString("SA_Score") != "null") {
                        scoreSum += Float.parseFloat(dcardObject.getString("SA_Score"));
                    } else {
                        scoreSum = 0;
                    }
                    if (dcardObject.getString("Level") != "null") {
                        keywordCount += 1;
                    }
                }
                articleCount = response.length();
                MSAccount.setText(articleCount + "");
                avgScore = (float) (Math.round((scoreSum/articleCount) * 100.0) / 100.0);
                if (avgScore <= 0.45) {
                    MSAverage.setTextColor(getResources().getColor(R.color.negColor));
                } else if (avgScore >= 0.46 || avgScore <= 0.54 ) {
                    MSAverage.setTextColor(getResources().getColor(R.color.neuColor));
                } if (avgScore >= 0.55) {
                    MSAverage.setTextColor(getResources().getColor(R.color.posColor));
                }
                MSAverage.setText(avgScore + "");
                MSKey.setText(keywordCount + "");
                Article_Summary.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                AS_Adapter = new  ArticleSummaryAdapter(getApplicationContext(), dcardList,5);
                Article_Summary.setAdapter(AS_Adapter);
                int posCount = Collections.frequency(chartValue, elementToFound_pos);
                int neuCount = Collections.frequency(chartValue, elementToFound_neu);
                int negCount = Collections.frequency(chartValue, elementToFound_neg);

                pos = posCount;
                neu = neuCount;
                neg = negCount;

                showPieChart();
                progressBar1.setVisibility(View.GONE);
                progressBar2.setVisibility(View.GONE);
            } catch (JSONException e) {
                progressBar1.setVisibility(View.GONE);
                progressBar2.setVisibility(View.GONE);
                Toast.makeText(HomePage.this, "文章未更新",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }, error -> {
            progressBar1.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            Toast.makeText(HomePage.this, "文章未更新",Toast.LENGTH_LONG).show();
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
    }

    public void loadBarChartValue() {
        progressBar3.setVisibility(View.VISIBLE);
        HttpsTrustManager.allowAllSSL();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BARCHART_URL, null, response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject dcardObject = response.getJSONObject(i);
                    barChartValue.add(dcardObject.getString("Count"));
                    barChartValue.add(dcardObject.getString("newDate"));
                }
                try {
                    ShowBarChart();
                    progressBar3.setVisibility(View.GONE);
                } catch (Exception e) {
                    progressBar3.setVisibility(View.GONE);
                    Toast.makeText(HomePage.this, "資料未更新",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                progressBar3.setVisibility(View.GONE);
                Toast.makeText(HomePage.this, "資料未更新",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }, error -> {
            progressBar3.setVisibility(View.GONE);
            Toast.makeText(HomePage.this, "資料未更新",Toast.LENGTH_LONG).show();
            error.printStackTrace();
        });
        queue.add(jsonArrayRequest);
    }

    public void ShowBarChart() {
        String[] month = new String[]{barChartValue.get(1), barChartValue.get(3), barChartValue.get(5), barChartValue.get(7)};

        barChart = findViewById(R.id.bar_chart);

        // creating a new bar data set.
        barDataSet1 = new BarDataSet(getBarEntriesOne(), "Positive");
        barDataSet1.setColor(getApplicationContext().getResources().getColor(R.color.posColor));
        barDataSet2 = new BarDataSet(getBarEntriesTwo(), "Neutral");
        barDataSet2.setColor(getApplicationContext().getResources().getColor(R.color.neuColor));
        barDataSet3 = new BarDataSet(getBarEntriesThree(), "Negative");
        barDataSet3.setColor(getApplicationContext().getResources().getColor(R.color.negColor));

        // below line is to add bar data set to our bar data.
        BarData data = new BarData(barDataSet1, barDataSet2, barDataSet3);

        // after adding data to our bar data we
        // are setting that data to our bar chart.
        barChart.setData(data);

        // below line is to remove description
        // label of our bar chart.
        barChart.getDescription().setEnabled(false);

        // below line is to get x axis
        // of our bar chart.
        XAxis xAxis = barChart.getXAxis();

        // below line is to set value formatter to our x-axis and
        // we are adding our days to our x axis.
        xAxis.setValueFormatter(new IndexAxisValueFormatter(month));

        // below line is to set center axis
        // labels to our bar chart.
        xAxis.setCenterAxisLabels(true);

        // below line is to set position
        // to our x-axis to bottom.
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // below line is to set granularity
        // to our x axis labels.
        xAxis.setGranularity(1);

        // below line is to enable
        // granularity to our x axis.
        xAxis.setGranularityEnabled(true);

        // below line is to make our
        // bar chart as draggable.
        barChart.setDragEnabled(true);

        // below line is to make visible
        // range for our bar chart.
        barChart.setVisibleXRangeMaximum(3);

        // below line is to add bar
        // space to our chart.
        float barSpace = 0.05f;

        // below line is use to add group
        // spacing to our bar chart.
        float groupSpace = 0.55f;

        // we are setting width of
        // bar in below line.
        data.setBarWidth(0.1f);

        // below line is to set minimum
        // axis to our chart.
        barChart.getXAxis().setAxisMinimum(0);

        // below line is to
        // animate our chart.
        barChart.animate();

        // below line is to group bars
        // and add spacing to it.
        barChart.groupBars(0, groupSpace, barSpace);

        // below line is to invalidate
        // our bar chart.
        barChart.invalidate();
    }

    private ArrayList<BarEntry> getBarEntriesOne() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, Float.parseFloat(barChartValue.get(0))));
        barEntries.add(new BarEntry(2f, Float.parseFloat(barChartValue.get(2))));
        barEntries.add(new BarEntry(3f, Float.parseFloat(barChartValue.get(4))));
        barEntries.add(new BarEntry(4f, Float.parseFloat(barChartValue.get(6))));
        return barEntries;
    }

    private ArrayList<BarEntry> getBarEntriesTwo() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, Float.parseFloat(barChartValue.get(8))));
        barEntries.add(new BarEntry(2f, Float.parseFloat(barChartValue.get(10))));
        barEntries.add(new BarEntry(3f, Float.parseFloat(barChartValue.get(12))));
        barEntries.add(new BarEntry(4f, Float.parseFloat(barChartValue.get(14))));
        return barEntries;
    }

    private ArrayList<BarEntry> getBarEntriesThree() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, Float.parseFloat(barChartValue.get(16))));
        barEntries.add(new BarEntry(2f, Float.parseFloat(barChartValue.get(18))));
        barEntries.add(new BarEntry(3f, Float.parseFloat(barChartValue.get(20))));
        barEntries.add(new BarEntry(4f, Float.parseFloat(barChartValue.get(22))));
        return barEntries;
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
        closeDrawer(drawerLayout);
    }

    public void ClickArticle(View view){
        //Redirect(重定向) activity to articlePage
        redirectActivity(this, ArticlePage.class);
    }

    public void ClickToday(View view){
        DCARD_URL = TODAY_DCARD_URL;
        loadDcard();
    }

    public void ClickWeek(View view){
        DCARD_URL = WEEK_DCARD_URL;
        loadDcard();
    }

    public void ClickMonth(View view){
        DCARD_URL = MONTH_DCARD_URL;
        loadDcard();
    }

    public void ClickChart(View view){
        //Redirect(重定向) activity to chartPage
        redirectActivity(this, MPChartPage.class);
    }

    public void ClickBarChart(View view){
        //Redirect(重定向) activity to chartPage
        redirectActivity(this, MoreBarChart.class);
    }

    public void ClickTrend(View view){
        //Redirect(重定向) activity to chartPage
        redirectActivity(this,MoreBarChart.class);
    }

    public void ClickLogout(View view){
        //回到登入頁面
        logout(this);
    }

    public void redirectActivity(Activity activity,Class aClass){
         //導到其他頁面
         //Initialize intent
         Intent intent=new Intent(activity,aClass);
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