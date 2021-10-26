package com.example.dcardtry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DcardDetailActivity extends AppCompatActivity {
    private String title, content, date;
    private TextView tvTitle, tvContent, tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcard_detail);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        date = getIntent().getStringExtra("date");


        tvTitle = (TextView) findViewById(R.id.txt_title);
        tvTitle.setText(title);
        tvContent = (TextView) findViewById(R.id.txt_content);
        tvContent.setText(content);
        tvDate = (TextView) findViewById(R.id.txt_date);
        tvDate.setText(date);
    }
}
