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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MoreBarChart extends AppCompatActivity {
    private static final String FULL_BARCHART_URL = "https://dcardanalysislaravel-sedok4caqq-de.a.run.app/GBChart12Data";
    private static final String FULL_LINECHART_URL = "https://dcardanalysislaravel-sedok4caqq-de.a.run.app/LineChart12Data";
    List<String> barChartValue, lineChartValue;
    BarChart barChart;
    BarDataSet barDataSet1, barDataSet2, barDataSet3;
    ArrayList barEntries;
    ProgressBar progressBar;
    ArrayList<Entry> values;
    LineChart mLineChart;
    String Name,Job,Account,Password;//接收登入頁面傳過來的資料
    TextView DM_Tilte;//側邊選單標題 : 姓名+職稱
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_bar_chart);

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
        DM_Tilte.setText( Name+"\n"+Job+"\t\t 您好" );

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        
        progressBar = findViewById(R.id.progressBar);
        mLineChart = findViewById(R.id.lineChart);
        barChartValue = new ArrayList<>();
        lineChartValue = new ArrayList<>();
        values = new ArrayList<>();

        loadBarChartValue();
        loadLineChartValue();
    }

    public void loadBarChartValue() {
        progressBar.setVisibility(View.VISIBLE);
        HttpsTrustManager.allowAllSSL();
        RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, FULL_BARCHART_URL, null, response -> {
            try {
                barChartValue.clear();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject dcardObject = response.getJSONObject(i);
                    barChartValue.add(dcardObject.getString("Count"));
                    barChartValue.add(dcardObject.getString("newDate"));
                }
                try {
                    ShowBarChart();
                    progressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MoreBarChart.this, "資料未更新",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MoreBarChart.this, "資料未更新",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MoreBarChart.this, "資料未更新",Toast.LENGTH_LONG).show();
            error.printStackTrace();
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
        queue.add(jsonArrayRequest);
    }

    public void loadLineChartValue() {
        HttpsTrustManager.allowAllSSL();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, FULL_LINECHART_URL, null, response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject dcardObject = response.getJSONObject(i);
                    lineChartValue.add(dcardObject.getString("avgScore"));
                    lineChartValue.add(dcardObject.getString("newDate"));
                }
                try {
                    mplinechart(); //設定數據源
                    initX();
                    initY();
                } catch (Exception e) {
                    Toast.makeText(MoreBarChart.this, "資料未更新",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                Toast.makeText(MoreBarChart.this, "資料未更新",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(MoreBarChart.this, "資料未更新",Toast.LENGTH_LONG).show();
            error.printStackTrace();
        });
        queue.add(jsonArrayRequest);
    }

    public void ShowBarChart() {
        String[] month = new String[]{barChartValue.get(1), barChartValue.get(3), barChartValue.get(5), barChartValue.get(7), barChartValue.get(9), barChartValue.get(11), barChartValue.get(13), barChartValue.get(15), barChartValue.get(17), barChartValue.get(19), barChartValue.get(21), barChartValue.get(23)};

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
        barEntries.add(new BarEntry(5f, Float.parseFloat(barChartValue.get(8))));
        barEntries.add(new BarEntry(6f, Float.parseFloat(barChartValue.get(10))));
        barEntries.add(new BarEntry(7f, Float.parseFloat(barChartValue.get(12))));
        barEntries.add(new BarEntry(8f, Float.parseFloat(barChartValue.get(14))));
        barEntries.add(new BarEntry(9f, Float.parseFloat(barChartValue.get(16))));
        barEntries.add(new BarEntry(10f, Float.parseFloat(barChartValue.get(18))));
        barEntries.add(new BarEntry(11f, Float.parseFloat(barChartValue.get(20))));
        barEntries.add(new BarEntry(12f, Float.parseFloat(barChartValue.get(22))));
        return barEntries;
    }

    private ArrayList<BarEntry> getBarEntriesTwo() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, Float.parseFloat(barChartValue.get(24))));
        barEntries.add(new BarEntry(2f, Float.parseFloat(barChartValue.get(26))));
        barEntries.add(new BarEntry(3f, Float.parseFloat(barChartValue.get(28))));
        barEntries.add(new BarEntry(4f, Float.parseFloat(barChartValue.get(30))));
        barEntries.add(new BarEntry(5f, Float.parseFloat(barChartValue.get(32))));
        barEntries.add(new BarEntry(6f, Float.parseFloat(barChartValue.get(34))));
        barEntries.add(new BarEntry(7f, Float.parseFloat(barChartValue.get(36))));
        barEntries.add(new BarEntry(8f, Float.parseFloat(barChartValue.get(38))));
        barEntries.add(new BarEntry(9f, Float.parseFloat(barChartValue.get(40))));
        barEntries.add(new BarEntry(10f, Float.parseFloat(barChartValue.get(42))));
        barEntries.add(new BarEntry(11f, Float.parseFloat(barChartValue.get(44))));
        barEntries.add(new BarEntry(12f, Float.parseFloat(barChartValue.get(46))));
        return barEntries;
    }

    private ArrayList<BarEntry> getBarEntriesThree() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, Float.parseFloat(barChartValue.get(48))));
        barEntries.add(new BarEntry(2f, Float.parseFloat(barChartValue.get(50))));
        barEntries.add(new BarEntry(3f, Float.parseFloat(barChartValue.get(52))));
        barEntries.add(new BarEntry(4f, Float.parseFloat(barChartValue.get(54))));
        barEntries.add(new BarEntry(5f, Float.parseFloat(barChartValue.get(56))));
        barEntries.add(new BarEntry(6f, Float.parseFloat(barChartValue.get(58))));
        barEntries.add(new BarEntry(7f, Float.parseFloat(barChartValue.get(60))));
        barEntries.add(new BarEntry(8f, Float.parseFloat(barChartValue.get(62))));
        barEntries.add(new BarEntry(9f, Float.parseFloat(barChartValue.get(64))));
        barEntries.add(new BarEntry(10f, Float.parseFloat(barChartValue.get(66))));
        barEntries.add(new BarEntry(11f, Float.parseFloat(barChartValue.get(68))));
        barEntries.add(new BarEntry(12f, Float.parseFloat(barChartValue.get(70))));
        return barEntries;
    }

    public void mplinechart() {
        //顯示邊界
        mLineChart.setDrawBorders(false);
        //設置描述文本不顯示
        mLineChart.getDescription().setEnabled(false);
        //設置是否顯示錶格背景
        mLineChart.setDrawGridBackground(true);
        //設置是否可以觸摸
        mLineChart.setTouchEnabled(true);
        mLineChart.setDragDecelerationFrictionCoef(0.9f);
        //設置是否可以拖拽
        mLineChart.setDragEnabled(true);
        //設置是否可以縮放
        mLineChart.setScaleEnabled(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setHighlightPerDragEnabled(true);
        mLineChart.setPinchZoom(true);

        //設置一頁最大顯示個數爲6，超出部分就滑動
        float ratio = (float) 6;
        //顯示的時候是按照多大的比率縮放顯示,1f表示不放大縮小
        mLineChart.zoom(ratio,1f,0,0);
        //設定資料
        values.add(new Entry(1.5f, Float.parseFloat(lineChartValue.get(0))));
        values.add(new Entry(2.5f, Float.parseFloat(lineChartValue.get(2))));
        values.add(new Entry(3.5f, Float.parseFloat(lineChartValue.get(4))));
        values.add(new Entry(4.5f, Float.parseFloat(lineChartValue.get(6))));
        values.add(new Entry(5.5f, Float.parseFloat(lineChartValue.get(8))));
        values.add(new Entry(6.5f, Float.parseFloat(lineChartValue.get(10))));
        values.add(new Entry(7.5f, Float.parseFloat(lineChartValue.get(12))));
        values.add(new Entry(8.5f, Float.parseFloat(lineChartValue.get(14))));
        values.add(new Entry(9.5f, Float.parseFloat(lineChartValue.get(16))));
        values.add(new Entry(10.5f, Float.parseFloat(lineChartValue.get(18))));
        values.add(new Entry(11.5f, Float.parseFloat(lineChartValue.get(20))));
        values.add(new Entry(12.5f, Float.parseFloat(lineChartValue.get(22))));

        //一個LineDataSet就是一條線
        LineDataSet lineDataSet = new LineDataSet(values, "情緒平均分數");
        LineData dataline = new LineData(lineDataSet);

        mLineChart.setData(dataline);
        final LineDataSet set;
        // dataLine
        set = new LineDataSet(values, "情緒平均分數");
        set.setMode(LineDataSet.Mode.LINEAR);//類型為折線
        set.setColor(getResources().getColor(R.color.negColor));//線的顏色
        set.setLineWidth(2.5f);//線寬
        set.setDrawCircles(false); //顯示相應座標點的小圓圈(預設顯示)
        set.setDrawValues(true);//顯示座標點對應Y軸的數字
        set.setValueTextSize(10f);

        //理解爲多條線的集合
        LineData data = new LineData(set);
        mLineChart.setData(data);//一定要放在最後
        mLineChart.invalidate();//繪製圖表
    }

    private void initX() {
        String[]month= new String[]{"", lineChartValue.get(1), lineChartValue.get(3), lineChartValue.get(5), lineChartValue.get(7), lineChartValue.get(9), lineChartValue.get(11), lineChartValue.get(13), lineChartValue.get(15), lineChartValue.get(17), lineChartValue.get(19), lineChartValue.get(21), lineChartValue.get(23)};
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X軸標籤顯示位置(預設顯示在上方，分為上方內/外側、下方內/外側及上下同時顯示)
        xAxis.setValueFormatter(new IndexAxisValueFormatter(month));
        xAxis.setCenterAxisLabels(true);

        // below line is to set granularity
        // to our x axis labels.
        xAxis.setGranularity(1f);


        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(13, true);
        mLineChart.setDragEnabled(true);
        xAxis.setAxisMinimum(1f);
        xAxis.setAxisMaximum(13f);
        xAxis.setTextColor(Color.GRAY);//X軸標籤顏色
        xAxis.setTextSize(12);//X軸標籤大小
        xAxis.setLabelCount(13);//X軸標籤個數
        xAxis.setSpaceMin(1f);//折線起點距離左側Y軸距離
        xAxis.setSpaceMax(0.0f);//折線終點距離右側Y軸距離
        xAxis.setDrawGridLines(false);//顯示每個座標點對應X軸的線

        //設定所需特定標籤資料

        List<String> xList = new ArrayList<>();
        for (int i = 0; i < month.length; i++) {
            xList.add(month[i]);
        }

    }

    private void initY() {
        YAxis rightAxis = mLineChart.getAxisRight();//獲取右側的軸線
        rightAxis.setEnabled(false);//不顯示右側Y軸
        YAxis leftAxis = mLineChart.getAxisLeft();//獲取左側的軸線

        leftAxis.setLabelCount(6);//Y軸標籤個數
        leftAxis.setTextColor(Color.GRAY);//Y軸標籤顏色
        leftAxis.setTextSize(10);//Y軸標籤大小

        leftAxis.setAxisMinimum(0.0f);//Y軸標籤最小值
        leftAxis.setAxisMaximum(1.0f);//Y軸標籤最大值
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
        redirectActivity(this,ArticlePage.class);
        finish();
    }

    public void ClickChart(View view){
        //Redirect(重定向) activity to chartPage
        redirectActivity(this, MPChartPage.class);
        finish();
    }

    public void ClickBarChart(View view){
        //Redirect(重定向) activity to chartPage
        redirectActivity(this, MoreBarChart.class);
    }

    public void ClickTrend(View view){
        //Redirect(重定向) activity to chartPage
        closeDrawer( drawerLayout );
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
         //start activity
         activity.startActivity(intent);
    }
    /*
    public void itntTWM(Activity activity,Class aClass){
        //導到其他頁面
        //Initialize intent
        Intent intent_twm_txt=new Intent(activity,aClass);
        intent_twm_txt.putExtra("twm", twm_txt.getText().toString());
        //start activity
        activity.startActivity(intent_twm_txt);
    }

     */

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