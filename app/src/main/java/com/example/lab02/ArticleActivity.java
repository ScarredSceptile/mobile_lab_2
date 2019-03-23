package com.example.lab02;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        final TextView text = findViewById(R.id.articleText);
        final WebView web = findViewById(R.id.articleWeb);



        Intent intent = getIntent();
        final String title = intent.getStringExtra(MainActivity.EXTRA_TITLE);
        final String link = intent.getStringExtra(MainActivity.EXTRA_LINK);
        final String description = intent.getStringExtra(MainActivity.EXTRA_DESCRIPTION);

        if (link != null) {
            web.setVisibility(View.VISIBLE);
            web.loadUrl(link);
        } else {
            final String article = title + "\n\n" + description;
            text.setText(article);
            web.setVisibility(View.INVISIBLE);
        }
    }
}
