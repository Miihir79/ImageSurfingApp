package com.mihir.imageSurfing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequestBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;

public class ImageZoom extends AppCompatActivity {


    private ImageButton downloadBtn;
    private String ImageURL;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);

        ImageView imageview = findViewById(R.id.myZoomImage);
        ImageURL = getIntent().getStringExtra("image");
        PRDownloader.initialize(getApplicationContext());
        Glide.with(this).load(ImageURL).into(imageview);

         downloadBtn = findViewById(R.id.DownloadButton);

         downloadBtn.setOnClickListener(v->{
             checkPermission();
         });

         checkPermission();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
                        .build());

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        adView = findViewById(R.id.adView);

        // Create an ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

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

    //no longer needed
    private void downloadImage() {

        ProgressDialog pd = new ProgressDialog(this);

        pd.setMessage("Downloading...");
        pd.setCancelable(false);
        pd.show();

        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        PRDownloader.download(ImageURL,file.getPath(), URLUtil.guessFileName(ImageURL,null,null).replace(".bin","") +".png")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                }).setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel() {

            }
        }).setOnProgressListener(new OnProgressListener() {
            @Override
            public void onProgress(Progress progress) {

                //calculating the progress of download
                long percent = progress.currentBytes*100/progress.totalBytes;

                pd.setMessage("Downloading:"+ percent+"%");

            }
        }).start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                pd.dismiss();
                Toast.makeText(ImageZoom.this,"Download Complete",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Error error) {
                pd.dismiss();
                Toast.makeText(ImageZoom.this,"Could not download",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void downloadImageUrl(){
        DownloadManager downloadManager=null;
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
