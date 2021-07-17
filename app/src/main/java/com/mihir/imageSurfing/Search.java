package com.mihir.imageSurfing;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.material.snackbar.Snackbar;
import com.mihir.imageSurfing.adapters.ImageAdapter;
import com.mihir.imageSurfing.api.ApiUtilities;
import com.mihir.imageSurfing.model.ImageModel;
import com.mihir.imageSurfing.model.SearchModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import kotlin.jvm.internal.Intrinsics;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<ImageModel> list;
    private GridLayoutManager manager;
    private ImageAdapter adapter;
    private int page =1;
    private ProgressDialog dialog;

    private String Query;

    private final int pagesize = 30;
    private boolean isLoading;
    private boolean isLastPage;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.statusbar_color));
        setContentView(R.layout.activity_search);

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setBackgroundDrawable(getDrawable(R.drawable.appbarcolor));

        Query = getIntent().getStringExtra("query");
        recyclerView = findViewById(R.id.recyclerViewSearch);
        list = new ArrayList<>();
        adapter = new ImageAdapter(this,list);
        manager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        checkNetworkState();

        searchData(Query);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItem = manager.getChildCount();
                int totalItem = manager.getItemCount();
                int firstVisiblePosition = manager.findFirstVisibleItemPosition();


                if(!isLoading && !isLastPage){
                    if ((visibleItem + firstVisiblePosition  >= totalItem) && firstVisiblePosition>=0 && totalItem>=pagesize){
                        page++;
                        searchData(Query);
                    }
                }
            }
        });
    }

    private void checkNetworkState() {
        @SuppressLint("ShowToast") Snackbar snackbarNoNetwork = Snackbar.make(
                findViewById(android.R.id.content),
                "no internet",
                Snackbar.LENGTH_INDEFINITE
        );


        Application var10003 = this.getApplication();
        Intrinsics.checkNotNullExpressionValue(var10003, "application");
        CheckInternet var6 = new CheckInternet(var10003);
        if (var6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("networkConnectivity");
        }

        var6.observe(this, new Observer() {
            // $FF: synthetic method
            // $FF: bridge method
            public void onChanged(Object var1) {
                this.onChanged((Boolean)var1);
            }

            public final void onChanged(Boolean isConnected) {
                if (Intrinsics.areEqual(isConnected, true)) {
                    snackbarNoNetwork.dismiss();
                    Log.i("Tag", "onCreate: internet is connected");
                } else if (Intrinsics.areEqual(isConnected, false)) {
                    snackbarNoNetwork.show();
                    snackbarNoNetwork.setAnimationMode(Snackbar.ANIMATION_MODE_FADE);
                    snackbarNoNetwork.setAction("Dismiss",v -> snackbarNoNetwork.dismiss());
                }

            }
        });

    }

    private void searchData(String query) {
        ApiUtilities.getApiInterface().searchImage(query,30,page).enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                Log.i("TAG", "onResponse: "+response.raw());
                list.addAll(response.body().getResults());
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                isLoading = false;

                if (list.size() > 0 ){
                    isLastPage = list.size() < pagesize;

                }
                else{
                    isLastPage = true;
                }
            }

            @Override
            public void onFailure( Call<SearchModel> call,  Throwable t) {
                dialog.dismiss();
                Log.i("TAG", "onFailure: "+ t.getMessage());
            }


        });

    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Query = query;
                searchData(Query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }*/
}