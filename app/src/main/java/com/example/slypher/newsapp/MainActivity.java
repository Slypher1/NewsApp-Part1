package com.example.slypher.newsapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.slypher.newsapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String URL_GUARDIAN_API = "https://content.guardianapis.com/search?q=mcu&section=film&order-by=newest&page-size=20&api-key=test";
    private static final int NEWS_LOADER_ID = 1;

    ActivityMainBinding binding;
    NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        // Set my custom toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");

        // Create a new adapter that takes an empty list of news as input
        adapter = new NewsAdapter(this, new ArrayList<News>());

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        binding.newsListView.setEmptyView(binding.emptyTextView);

        // check internet connection
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            getLoaderManager().initLoader(NEWS_LOADER_ID,null, this);
        } else {
            binding.loadingSpinner.setVisibility(View.GONE);
            binding.emptyTextView.setText(R.string.no_internet_connection);
        }

    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        return new NewsLoader(this, URL_GUARDIAN_API);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        binding.loadingSpinner.setVisibility(View.GONE);
        binding.emptyTextView.setText(R.string.no_news_found);

        if (news != null && !news.isEmpty()){
            adapter.clear();
            adapter = new NewsAdapter((Activity) binding.newsListView.getContext(), (ArrayList<News>) news);
        }

        updateUI();
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        adapter.clear();
    }

    private void updateUI(){

        binding.newsListView.setAdapter(adapter);

        binding.newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri webpage = Uri.parse(adapter.getItem(i).getWebUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }
}
