package com.lvable.ningjiaqi.polyloading;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PolyLoadingView loadView1 = (PolyLoadingView) findViewById(R.id.test1);
        loadView1.setResizePercent(0.5f);

        final PolyLoadingView loadView2 = (PolyLoadingView) findViewById(R.id.test2);
        loadView2.setSlide(5);
        loadView2.setShapeColor(0xff554433);
        loadView2.configSpring(100,20);
        loadView2.setRoundCorner(5);

        final PolyLoadingView loadView3 = (PolyLoadingView) findViewById(R.id.test3);
        loadView3.setSlide(5);
        loadView3.enableAlphaEffect(false);
        loadView3.setFill(false);
        loadView3.setDepth(6);
        loadView3.setRoundCorner(15);
        loadView3.setShapeColor(0xff354433);
        loadView3.configSpring(6,3);


        final PolyLoadingView loadView4 = (PolyLoadingView) findViewById(R.id.test4);
        loadView4.setSlide(8);
        loadView4.enableAlphaEffect(false);
        loadView4.setFill(false);
        loadView4.setDepth(3);
        loadView4.setShapeColor(0xff394cc6);
        loadView4.configSpring(11,3);


        loadView1.setOnClickListener(this);
        loadView2.setOnClickListener(this);
        loadView3.setOnClickListener(this);
        loadView4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        PolyLoadingView loadView = (PolyLoadingView) v;
        if (loadView.isRunning()){
            loadView.stop();
        }else {
            loadView.startLoading();
        }
    }
}
