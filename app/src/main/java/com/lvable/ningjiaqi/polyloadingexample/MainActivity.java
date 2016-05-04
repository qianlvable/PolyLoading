package com.lvable.ningjiaqi.polyloadingexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lvable.ningjiaqi.polyloading.PolyLoadingView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PolyLoadingView loadView1 = (PolyLoadingView) findViewById(R.id.test1);
        loadView1.setResizePercent(0.5f);
        loadView1.setEdgeCount(3);
        loadView1.setFill(true);
        loadView1.enableAlphaEffect(true);
        loadView1.setDepth(3);
        loadView1.setResizePercent(0.5f);

        final PolyLoadingView loadView2 = (PolyLoadingView) findViewById(R.id.test2);
        loadView2.setEdgeCount(5);
        loadView2.setFill(true);
        loadView2.enableAlphaEffect(true);
        loadView2.setShapeColor(0xff554433);
        loadView2.configSpring(45,12);
        loadView2.setRoundCorner(5);
        loadView2.startLoading();

        final PolyLoadingView loadView3 = (PolyLoadingView) findViewById(R.id.test3);
        loadView3.setEdgeCount(4);
        loadView3.enableAlphaEffect(false);
        loadView3.setFill(false);
        loadView3.setDepth(2);
        loadView3.setRoundCorner(12);
        loadView3.setShapeColor(0xff354433);
        loadView3.configSpring(9,5);
       // loadView3.startLoading();


        final PolyLoadingView loadView4 = (PolyLoadingView) findViewById(R.id.test4);
        loadView4.setEdgeCount(6);
        loadView4.enableAlphaEffect(false);
        loadView4.setFill(false);
        loadView4.setDepth(3);
        loadView4.setShapeColor(0xff394cc6);
        loadView4.configSpring(25,6);


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
