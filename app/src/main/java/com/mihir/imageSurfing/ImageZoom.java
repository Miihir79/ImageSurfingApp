package com.mihir.imageSurfing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageZoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);

        ImageView imageview = findViewById(R.id.myZoomImage);

        Glide.with(this).load(getIntent().getStringExtra("image")).into(imageview);
    }
}