package com.mihir.imageSurfing.ui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mihir.imageSurfing.helper.CheckInternet;
import com.mihir.imageSurfing.R;
import com.mihir.imageSurfing.adapters.ImageAdapter;
import com.mihir.imageSurfing.api.ApiUtilities;
import com.mihir.imageSurfing.model.ImageModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ImageModel> list;
    private GridLayoutManager manager;
    private ImageAdapter adapter;
    private int page =1;
    private ProgressDialog dialog;

    private final int pagesize = 30;
    private boolean isLoading;
    private boolean isLastPage;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.statusbar_color));
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setBackgroundDrawable(getDrawable(R.drawable.appbarcolor));


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        list = new ArrayList<>();
        Button button = findViewById(R.id.RandomButton);
        adapter = new ImageAdapter(this,list);
        manager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        checkNetworkState();


        button.setOnClickListener(v->
                {
                    Intent intent = new Intent(this, ImageZoom.class);
                    intent.putExtra("random",true);
                    startActivity(intent);
                });


        getData();
        //to keep track of loading data in background
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

                Log.i("TAG", "onScrolled: " + isLastPage +isLoading);
                if(!isLoading && !isLastPage){
                    Log.i("TAG", "onScrolled:  first if case ke andar");
                    //Log.i("TAG", "onScrolled:" + visibleItem + firstVisiblePosition + totalItem + pagesize);
                    if ((visibleItem + firstVisiblePosition  >= totalItem) && firstVisiblePosition>=0 && totalItem>=pagesize){
                        Log.i("TAG", "onScrolled: inside second");
                        page++;
                        getData();
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

    private void getData() {

        isLoading= true;
        ApiUtilities.getApiInterface().getImages(page,30).enqueue(new Callback<List<ImageModel>>() {
            @Override
            public void onResponse(@NotNull Call<List<ImageModel>> call, @NotNull Response<List<ImageModel>> response) {
                if(response.body()!= null){
                    Log.i("TAG", "onResponse: reached here " + response.raw());
                    list.addAll(response.body());
                    adapter.notifyItemInserted(list.size());

                }else{
                    Log.i("TAG", "onResponse:" + response.raw());
                }
                isLoading = false;
                dialog.dismiss();

                if (list.size() > 0 ){
                    isLastPage = list.size() < pagesize;

                }
                else{
                    isLastPage = true;
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ImageModel>> call, @NotNull Throwable t) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this,"Error:"+t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent intent = new Intent(MainActivity.this,Search.class);
                intent.putExtra("query",query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

}