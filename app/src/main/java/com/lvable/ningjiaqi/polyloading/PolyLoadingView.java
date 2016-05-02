package com.lvable.ningjiaqi.polyloading;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
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
    private List<List<PointF>> mChildren;

    private int mShapeColor;
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
        init();
    }

    public PolyLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mShapeColor = 0xff02C39A;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mPaint.setStrokeWidth(5);
        mPaint.setColor(mShapeColor);

        // 什么时候需要调用这个api
      //  setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
        mPath = new Path();
        mPoints = new ArrayList<>();
        mChildren = new ArrayList<>();
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
                        ,0,1,toLow,toHigh);
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
            mPoints = ShapeUtil.getRegularPoints(cx, cy, slide, radius);
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

    public List<List<PointF>> getCurrentShape(float progress){
        List<List<PointF>> result = new ArrayList<>();
        for (int i =0;i < depth;i++) {
            List<PointF> pre;
            if (i == 0)
                pre = mPoints;
            else
                pre = result.get(i-1);

            List<PointF> child = ShapeUtil.getInscribedPoints(pre,progress);
            result.add(child);
        }
        return result;
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
    }

    public void setReverse(boolean backward) {
        // TODO: 16/4/25 need to implement
    }

    public void setShapeColor(int shapeColor) {
        this.mShapeColor = shapeColor;
        mPaint.setColor(shapeColor);
    }

    public void setSlide(int slide) {
        if (slide > 2) {
            this.slide = slide;
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
}
