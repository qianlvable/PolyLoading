package com.lvable.ningjiaqi.polyloadingexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lvable.ningjiaqi.polyloading.PolyLoadingLiteView;

public class AnotherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);

        PolyLoadingLiteView polyLoadingLiteView = (PolyLoadingLiteView) findViewById(R.id.aha);
        polyLoadingLiteView.setReverse(true);
        polyLoadingLiteView.enableAlphaEffect(true);
        polyLoadingLiteView.start();
    }
}
