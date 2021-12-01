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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchArticlePage extends AppCompatActivity {
    private List<Dcard> dcardList;
    RecyclerView ArticleRecyclerview;
    Adapter adapter;
    private static final String SEARCH_DCARD_URL = "https://dcardanalysislaravel-sedok4caqq-de.a.run.app/getAllDcard/search/";
    String searchContent;
    ProgressBar progressBar;
    EditText edtxt;
    Button searchArticle_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_article_page);

        //設定隱藏標題
        getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        edtxt = findViewById(R.id.search_EdText);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        searchArticle_btn = findViewById(R.id.searchArticle_btn);
        searchArticle_btn.setOnClickListener(v -> {
            searchContent = edtxt.getText().toString();
            searchDcard();
        });

        ArticleRecyclerview = findViewById(R.id.ArticlePage_RecyclerView);
        dcardList = new ArrayList<>();
    }

    private void searchDcard() {

        HttpsTrustManager.allowAllSSL();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        progressBar.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, SEARCH_DCARD_URL + searchContent, null, response -> {
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
                ArticleRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new Adapter(getApplicationContext(), dcardList);
                ArticleRecyclerview.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SearchArticlePage.this, "未找到文章",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(SearchArticlePage.this, "未找到文章",Toast.LENGTH_LONG).show();
            error.printStackTrace();
        });
        requestQueue.add(jsonArrayRequest);
    }
}