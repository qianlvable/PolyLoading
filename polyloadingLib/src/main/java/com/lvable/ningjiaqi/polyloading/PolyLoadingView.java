package com.lvable.ningjiaqi.polyloading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ningjiaqi on 16/4/21.
 */
public class PolyLoadingView extends View {
    private Paint mPaint;
    private int slide = 3;
    private int depth = 4;
    private int cx;
    private int cy;
    private List<PointF> mPoints;
    private float mProgress;
    private Path mPath;

    private int mShapeColor = 0xff02C39A;
    private boolean mEnableAlpha = true;

    private int mAlpha;
    private float mResetAt = 1f;

    private Spring mSpring;
    float toLow = 0;
    float toHigh = mResetAt;
    private int mTensition = 20;
    private int mFriction = 6;

    private SimpleSpringListener mUpdateListener;
    private boolean mFilled = true;

    private boolean mBufferCompleted;
    private List<List<List<PointF>>> mFrameBuffer;
    private int mFrameIndex = 0;


    public PolyLoadingView(Context context) {
        super(context);
        init();
    }

    public PolyLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PolyLoadingView,
                0, 0);
        try {
            slide = a.getInt(R.styleable.PolyLoadingView_slide,6);
            depth = a.getInt(R.styleable.PolyLoadingView_depth,3);
            mFilled = a.getBoolean(R.styleable.PolyLoadingView_filled,false);
            mEnableAlpha = a.getBoolean(R.styleable.PolyLoadingView_enableAlpha,false);
            mShapeColor = a.getColor(R.styleable.PolyLoadingView_shapeColor,0xff02C39A);
            mTensition = a.getInt(R.styleable.PolyLoadingView_tensition,20);
            mFriction = a.getInt(R.styleable.PolyLoadingView_friction,6);
        } finally {
            a.recycle();
        }
        init();
    }


    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (mFilled) {
            mPaint.setStyle(Paint.Style.FILL);
        }else {
            mPaint.setStyle(Paint.Style.STROKE);
        }

        mPaint.setStrokeWidth(5);
        mPaint.setColor(mShapeColor);

        // 什么时候需要调用这个api
      //  setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
        mPath = new Path();
        mPoints = new ArrayList<>();
        mFrameBuffer = new ArrayList<>();

        SpringSystem springSystem = SpringSystem.create();
        mSpring = springSystem.createSpring();
        mSpring.setSpringConfig(new SpringConfig(mTensition, mFriction));
        mAlpha = 255 / depth;
        mUpdateListener = new SimpleSpringListener(){
            @Override
            public void onSpringUpdate(Spring spring) {
                float curVal = (float) spring.getCurrentValue();

                if (curVal == mSpring.getEndValue()) {
                    if (mResetAt != 1) {
                        changeMapRange();
                    }
                    mSpring.setCurrentValue(0f);
                    mSpring.setEndValue(1);
                    curVal = (float) spring.getCurrentValue();
                    mFrameIndex = 0;
                    mBufferCompleted = true;
                }

                mProgress = (float) SpringUtil.mapValueFromRangeToRange(curVal
                        ,1,0,toLow,toHigh);
                invalidate();
            }
        };

    }

    private void changeMapRange() {
        if (toLow == mResetAt) {
            toLow = 0;
            toHigh = mResetAt;
        } else {
            toLow = mResetAt;
            toHigh = 1;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (cx == 0) {
            cx = getWidth() / 2;
            cy = getHeight() / 2;
            canvas.rotate(180, cx, cy);
            float radius = getWidth() / 2.8f;
            mPoints = getRegularPoints(cx, cy, slide, radius);
        }

        List<List<PointF>> children;
        if (mBufferCompleted) {
            children = mFrameBuffer.get(mFrameIndex%mFrameBuffer.size());
            mFrameIndex++;
        }else {
            children = getCurrentShape(mProgress);
            mFrameBuffer.add(children);
        }
        for (int i =0;i < children.size();i++) {
            List<PointF> child = children.get(i);
            getPath(child);

            if (mEnableAlpha) {
                mPaint.setAlpha(mAlpha + mAlpha * i);
            }
            else {
                mPaint.setAlpha(255);
            }
            if (mFilled){
                mPaint.setStyle(Paint.Style.FILL);
            }else {
                mPaint.setStyle(Paint.Style.STROKE);
            }
            canvas.drawPath(mPath,mPaint);
        }
    }

    private void getPath(List<PointF> child) {
        mPath.reset();
        for (int j =0;j < child.size();j++){
            PointF point = child.get(j);

            if (j == 0){
                mPath.moveTo(point.x,point.y);
            } else {
                mPath.lineTo(point.x,point.y);
            }
        }
        mPath.close();
    }

    private List<List<PointF>> getCurrentShape(float progress){
        List<List<PointF>> result = new ArrayList<>();
        for (int i =0;i < depth;i++) {
            List<PointF> pre;
            if (i == 0)
                pre = mPoints;
            else
                pre = result.get(i-1);

            List<PointF> child = getInscribedPoints(pre,progress);
            result.add(child);
        }
        return result;
    }

    private void discardCache(){
        mFrameIndex = 0;
        mBufferCompleted = false;
        mFrameBuffer.clear();
    }

    public void startLoading(){
        mSpring.setEndValue(1);
        mSpring.addListener(mUpdateListener);
    }

    public boolean isRunning(){
        return mSpring.getEndValue() == 1;
    }

    public void stop(){
        mSpring.setEndValue(0);
        mSpring.removeAllListeners();
    }

    public void enableAlphaEffect(boolean enable){
        mEnableAlpha = enable;
        discardCache();
    }

    public void setShapeColor(int shapeColor) {
        this.mShapeColor = shapeColor;
        mPaint.setColor(shapeColor);
        discardCache();
    }

    public void setSlide(int slide) {
        if (slide > 2) {
            this.slide = slide;
            discardCache();
        }
    }

    public void setFill(boolean enable){
        mFilled = enable;
    }

    public void setRoundCorner(float radius){
        mPaint.setPathEffect(new CornerPathEffect(radius));
    }

    public void setDepth(int depth) {
        this.depth = depth;

        mAlpha = 255 / depth;
        discardCache();
    }

    public void configSpring(int tension, int friction) {
        mSpring.setSpringConfig(new SpringConfig(tension,friction));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mSpring.removeAllListeners();
    }

    public void setResizePercent(float resize){
        if (resize< 0 || resize > 1) {
            resize = 1;
        }
        mResetAt = resize;
        if (mResetAt != 1) {
            changeMapRange();
        }
    }


    public List<PointF> getRegularPoints(int cx,int cy,int slide , float radius) {
        List<PointF> pts = new ArrayList<>();
        for (int i = 0;i < slide;i++) {
            float x = (float) (radius * Math.sin(i * 2 * Math.PI / slide));
            float y = (float) (radius * Math.cos(i * 2 * Math.PI / slide));
            x += cx;
            y += cy;
            pts.add(new PointF(x,y));
        }
        return pts;
    }

    public List<PointF> getInscribedPoints(List<PointF> pts, float progress) {
        List<PointF> inscribedPoints = new ArrayList<>();

        for (int i=0;i<pts.size();i++){
            PointF start = pts.get(i);
            PointF end;
            if (i < pts.size()-1) end = pts.get(i+1);
            else end = pts.get(0);

            inscribedPoints.add(getInterpolatedPoint(start,end,progress));
        }
        return inscribedPoints;
    }



    private PointF getInterpolatedPoint(PointF start, PointF end, float progress) {
        float dx = end.x - start.x;
        float dy = end.y - start.y;

        float newX = start.x + dx * progress;
        float newY = start.y + dy * progress;

        return new PointF(newX,newY);
    }

}
