package com.lvable.ningjiaqi.polyloading;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class another extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);

        TriangleView triangleView = (TriangleView) findViewById(R.id.aha);
       // triangleView.setReverse(true);
       // triangleView.enableAlphaEffect(true);
        triangleView.start();
    }
}
