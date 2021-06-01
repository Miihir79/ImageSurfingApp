package com.mihir.imageSurfing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mihir.imageSurfing.adapters.ImageAdapter;
import com.mihir.imageSurfing.api.ApiUtilities;
import com.mihir.imageSurfing.model.ImageModel;
import com.mihir.imageSurfing.model.SearchModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<ImageModel> list;
    private GridLayoutManager manager;
    private ImageAdapter adapter;
    private int page =1;
    private ProgressDialog dialog;

    private final int pagesize = 30;
    private boolean isLoading;
    private boolean isLastPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        list = new ArrayList<>();
        adapter = new ImageAdapter(this,list);
        manager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

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


                if(!isLoading && !isLastPage){
                    if ((visibleItem + firstVisiblePosition  >= totalItem) && firstVisiblePosition>=0 && totalItem>=pagesize){
                        page++;
                        getData();
                    }
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
                    Log.i("TAG", "onResponse: reached here ");
                    list.addAll(response.body());
                    adapter.notifyDataSetChanged();

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
            public void onFailure(Call<List<ImageModel>> call, Throwable t) {
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
                dialog.show();
                searchData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void searchData(String query) {
        ApiUtilities.getApiInterface().searchImage(query).enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse( Call<SearchModel> call,  Response<SearchModel> response) {
                list.clear();
                Log.i("TAG", "onResponse: "+response.raw());
                list.addAll(response.body().getResults());
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onFailure( Call<SearchModel> call,  Throwable t) {
                Log.i("TAG", "onFailure: "+ t.getMessage());
            }


        });

    }
}