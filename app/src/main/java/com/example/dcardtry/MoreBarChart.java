package com.example.dcardtry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
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
    private static final String FULL_BARCHART_URL = "http://192.168.0.104:13306/GetData4.php";
    private static final String P3M_DCARD_URL = "http://192.168.0.104:13306/p3mDcard.php";
    List<Dcard> dcardList;
    List<String> barChartValue;
    BarChart barChart;
    BarDataSet barDataSet1, barDataSet2, barDataSet3;
    ArrayList barEntries;
    RecyclerView mRecyclerView;
    Adapter adapter;
    RecyclerView.LayoutManager mLayoutManager;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_bar_chart);
        progressBar = findViewById(R.id.progressBar);
        mRecyclerView = findViewById(R.id.recyclerView);
        dcardList = new ArrayList<>();
        barChartValue = new ArrayList<>();

        loadBarChartValue();
        loadDcard();
    }

    public void loadBarChartValue() {
        progressBar.setVisibility(View.VISIBLE);
        HttpsTrustManager.allowAllSSL();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, FULL_BARCHART_URL, null, response -> {
            try {
                barChartValue.clear();
                JSONObject dcardObject = response.getJSONObject(0);
                barChartValue.add(dcardObject.getString("m0"));
                barChartValue.add(dcardObject.getString("m0posCount"));
                barChartValue.add(dcardObject.getString("m0neuCount"));
                barChartValue.add(dcardObject.getString("m0negCount"));
                barChartValue.add(dcardObject.getString("m1"));
                barChartValue.add(dcardObject.getString("m1posCount"));
                barChartValue.add(dcardObject.getString("m1neuCount"));
                barChartValue.add(dcardObject.getString("m1negCount"));
                barChartValue.add(dcardObject.getString("m2"));
                barChartValue.add(dcardObject.getString("m2posCount"));
                barChartValue.add(dcardObject.getString("m2neuCount"));
                barChartValue.add(dcardObject.getString("m2negCount"));
                barChartValue.add(dcardObject.getString("m3"));
                barChartValue.add(dcardObject.getString("m3posCount"));
                barChartValue.add(dcardObject.getString("m3neuCount"));
                barChartValue.add(dcardObject.getString("m3negCount"));
                ShowBarChart();
                progressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MoreBarChart.this, e.getMessage(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MoreBarChart.this, error.getMessage(),Toast.LENGTH_LONG).show();
            error.printStackTrace();
        });
        queue.add(jsonArrayRequest);
    }

    public void ShowBarChart() {
        String[] month = new String[]{barChartValue.get(0), barChartValue.get(4), barChartValue.get(8), barChartValue.get(12)};
        String[] days = new String[]{"4", "3", "2", "1"};

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
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));

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

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                e.getData();
                float nh = (float) (Math.floor(h.getX() * 100.0) / 100.0);
                String txt = String.valueOf(nh);
                Toast.makeText(MoreBarChart.this, txt,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new Adapter(getApplicationContext(), dcardList);
                mRecyclerView.setAdapter(adapter);
            }
        });
    }

    // array list for first set
    private ArrayList<BarEntry> getBarEntriesOne() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, Float.parseFloat(barChartValue.get(1))));
        barEntries.add(new BarEntry(2f, Float.parseFloat(barChartValue.get(5))));
        barEntries.add(new BarEntry(3f, Float.parseFloat(barChartValue.get(9))));
        barEntries.add(new BarEntry(4f, Float.parseFloat(barChartValue.get(13))));
        return barEntries;
    }

    // array list for second set.
    private ArrayList<BarEntry> getBarEntriesTwo() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, Float.parseFloat(barChartValue.get(2))));
        barEntries.add(new BarEntry(2f, Float.parseFloat(barChartValue.get(6))));
        barEntries.add(new BarEntry(3f, Float.parseFloat(barChartValue.get(10))));
        barEntries.add(new BarEntry(4f, Float.parseFloat(barChartValue.get(14))));
        return barEntries;
    }

    private ArrayList<BarEntry> getBarEntriesThree() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, Float.parseFloat(barChartValue.get(3))));
        barEntries.add(new BarEntry(2f, Float.parseFloat(barChartValue.get(7))));
        barEntries.add(new BarEntry(3f, Float.parseFloat(barChartValue.get(11))));
        barEntries.add(new BarEntry(4f, Float.parseFloat(barChartValue.get(15))));
        return barEntries;
    }

    public void loadDcard() {
        HttpsTrustManager.allowAllSSL();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, P3M_DCARD_URL, null, response -> {
            try {
                dcardList.clear();
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
                }
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new Adapter(getApplicationContext(), dcardList);
                mRecyclerView.setAdapter(adapter);
            } catch (JSONException e) {
                Toast.makeText(MoreBarChart.this, e.getMessage(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(MoreBarChart.this, error.getMessage(),Toast.LENGTH_LONG).show();
            error.printStackTrace();
        });
        queue.add(jsonArrayRequest);
    }
}