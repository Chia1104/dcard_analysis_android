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
    private static final String FULL_BARCHART_URL = "https://fathomless-fjord-03751.herokuapp.com/GBChart12Data";
    List<String> barChartValue, barChartValue1;
    BarChart barChart;
    BarDataSet barDataSet1, barDataSet2, barDataSet3;
    ArrayList barEntries;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_bar_chart);
        progressBar = findViewById(R.id.progressBar);
        barChartValue = new ArrayList<>();
        barChartValue1 = new ArrayList<>();

        loadBarChartValue();
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
                ShowBarChart();
                progressBar.setVisibility(View.GONE);
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

    // array list for first set
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

    // array list for second set.
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
}