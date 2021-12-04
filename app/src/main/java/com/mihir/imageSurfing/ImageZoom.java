package com.mihir.imageSurfing;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.downloader.PRDownloader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mihir.imageSurfing.api.ApiUtilities;
import com.mihir.imageSurfing.model.ImageModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageZoom extends AppCompatActivity {

    private List<ImageModel> list_rand;
    private Boolean isRandomImage;
    private int currPos=0;
    private String ImageURL;
    private String UserName;
    private ImageView imageview;
    private TextView text_url;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();

        window.setStatusBarColor(getResources().getColor(R.color.statusbar_color));

        setContentView(R.layout.activity_image_zoom);

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setBackgroundDrawable(getDrawable(R.drawable.appbarcolor));

        imageview= findViewById(R.id.myZoomImage);
        ImageView backButton = findViewById(R.id.btn_nextRandomImage);
        ImageView prevButton = findViewById(R.id.btn_prevRandomImage);
        text_url = findViewById(R.id.Url);
        ImageURL = getIntent().getStringExtra("image");
        UserName = getIntent().getStringExtra("UserName");
        isRandomImage = getIntent().getBooleanExtra("random",false);
        PRDownloader.initialize(getApplicationContext());
        text_url.setText("Image by "+UserName+" on Unsplash");

        if (isRandomImage){
            backButton.setVisibility(View.VISIBLE);
            prevButton.setVisibility(View.VISIBLE);
            getRandom();
            backButton.setOnClickListener( v->{
                if(list_rand.size() < currPos){
                    getRandom();
                }
                currPos++;
                ImageURL = list_rand.get(currPos).getUrls().getRegular();
                Glide.with(this).load(ImageURL).into(imageview);
                text_url.setText("Image by "+list_rand.get(currPos).getUser().getUsername()+" on Unsplash");
            });

            prevButton.setOnClickListener(v->{
                if(currPos != 0){
                    currPos--;
                    ImageURL = list_rand.get(currPos).getUrls().getRegular();
                    Glide.with(this).load(ImageURL).into(imageview);
                }
            });
        }
        else{
            Glide.with(this).load(ImageURL).into(imageview);
        }

        text_url.setOnClickListener(v->{
            Uri uri = Uri.parse(ImageURL);
            startActivity(new Intent(Intent.ACTION_VIEW,uri));
        });

        ImageButton downloadBtn = findViewById(R.id.DownloadButton);

         downloadBtn.setOnClickListener(v-> checkPermission());

         checkNetworkState();

        MobileAds.initialize(this, initializationStatus -> {});

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("ABCDEF012345"))
                        .build());

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        AdView adView = findViewById(R.id.adView);

        // Create an ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

    }


    private void getRandom(){

        ApiUtilities.getApiInterface_random().randomImage(10).enqueue(new Callback<List<ImageModel>>() {
            @Override
            public void onResponse(@NotNull Call<List<ImageModel>> call, @NotNull Response<List<ImageModel>> response) {
                Log.i("TAG", "onResponse:"+response.raw());
                if(response.body() != null){
                    list_rand = response.body();
                }
                ImageURL = list_rand.get(currPos).getUrls().getRegular();
                Glide.with(getApplicationContext()).load(ImageURL).into(imageview);
                text_url.setText("Image by "+list_rand.get(currPos).getUser().getUsername()+" on Unsplash");
            }
            @Override
            public void onFailure(@NotNull Call<List<ImageModel>> call, @NotNull Throwable t) {

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

    private void checkPermission(){
        Dexter.withContext(this).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()){
                    downloadImageUrl();
                }else{
                    Toast.makeText(ImageZoom.this,"Please allow all the permissions",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();
    }


    private void downloadImageUrl(){
        DownloadManager downloadManager;
        downloadManager =(DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(ImageURL);

        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setMimeType("image/png")
                .setTitle(URLUtil.guessFileName(ImageURL,null,"image/png"))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,File.separator+
                        URLUtil.guessFileName(ImageURL,null,"image/png"));

        downloadManager.enqueue(request);

        Toast.makeText(this,"Downloaded!",Toast.LENGTH_LONG).show();
    }
}
