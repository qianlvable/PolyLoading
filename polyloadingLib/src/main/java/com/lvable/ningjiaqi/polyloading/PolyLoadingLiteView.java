package com.lvable.ningjiaqi.polyloading;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ningjiaqi on 16/4/21.
 */
public class PolyLoadingLiteView extends View {
    private boolean mFilled;
    private Paint mPaint;
    private Path mPath;
    private ValueAnimator mProgressAnimator;

    private int mEdgeCount = 3;
    private int mDepth = 3;
    private int mCx;
    private int mCy;
    private float mProgress;
    private int mShapeColor = 0xff554433;
    private int mDuration;
    private boolean mEnableAlpha;
    private int mAlpha;

    private List<PointF> mPoints;
    private List<List<PointF>> mChildren;

    public PolyLoadingLiteView(Context context) {
        super(context);
        init();
    }

    public PolyLoadingLiteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PolyLoadingView,
                0, 0);
        try {
            mEdgeCount = a.getInt(R.styleable.PolyLoadingView_edgeCount,6);
            mDepth = a.getInt(R.styleable.PolyLoadingView_depth,3);
            mFilled = a.getBoolean(R.styleable.PolyLoadingView_filled,false);
            mEnableAlpha = a.getBoolean(R.styleable.PolyLoadingView_enableAlpha,false);
            mShapeColor = a.getColor(R.styleable.PolyLoadingView_shapeColor,0xff02C39A);
        } finally {
            a.recycle();
        }
        init();
    }


    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (mFilled){
            mPaint.setStyle(Paint.Style.FILL);
        }else {
            mPaint.setStyle(Paint.Style.STROKE);
        }
        mPaint.setStrokeWidth(9);
        mPaint.setColor(mShapeColor);

        mPath = new Path();
        mPoints = new ArrayList<>();
        mChildren = new ArrayList<>();

        mAlpha = 255 / mDepth;

        mDuration = 1200;
        mProgressAnimator = new ValueAnimator().ofFloat(1,0f).setDuration(mDuration);
        mProgressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mProgressAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCx == 0) {
            mCx = getWidth() / 2;
            mCy = getHeight() / 2;
            canvas.rotate(180, mCx, mCy);
            float radius = getWidth() / 2.8f;
            mPoints = getRegularPoints(mCx, mCy, mEdgeCount, radius);
        }

        List<List<PointF>> children = getCurrentShape(mProgress);
        for (int i =0;i < children.size();i++) {
            if (mEnableAlpha) {
                mPaint.setAlpha(mAlpha + mAlpha * i);
            }
            else {
                mPaint.setAlpha(255);
            }
            List<PointF> child = children.get(i);
            getPath(child);
            canvas.drawPath(mPath,mPaint);
        }
    }

    /**
     * @param filled set filled or stroke for poly
     * */
    public void setFill(boolean filled){
        if (filled){
            mPaint.setStyle(Paint.Style.FILL);
        }else {
            mPaint.setStyle(Paint.Style.STROKE);
        }
    }
    /**
     * @param radius set round corner for polygon`s edge
     * */
    public void setRoundRadius(float radius){
        mPaint.setPathEffect(new CornerPathEffect(radius));
    }

    public void start(){
        mProgressAnimator.start();
    }

    public void stop(){
        mProgressAnimator.end();
    }

    /**
     * @param enable enable alpha effect for child polygons
     * */
    public void enableAlphaEffect(boolean enable){
        mEnableAlpha = enable;
    }

    public void setReverse(boolean backward) {
        if (backward) {
            mProgressAnimator.setRepeatMode(ValueAnimator.REVERSE);
        }else {
            mProgressAnimator.setRepeatMode(ValueAnimator.RESTART);
        }
    }

    public void setInterpolator(Interpolator intepetor) {
        mProgressAnimator.setInterpolator(intepetor);
    }

    /**
     * @param millisecond set animation duration for one cycle
     * */
    public void setDuration(int millisecond) {
        mDuration = millisecond;
        mProgressAnimator.setDuration(mDuration);

    }
    /**
     * @param shapeColor set the main color of the polygon
     * */
    public void setShapeColor(int shapeColor) {
        this.mShapeColor = shapeColor;
        mPaint.setColor(shapeColor);
    }

    /**
     * @param edgeCount set edge count of the polygon
     * */
    public void setEdgeCount(int edgeCount) {
        if (mEdgeCount > 2) {
            this.mEdgeCount = edgeCount;
            invalidate();
        }
    }

    private List<PointF> getRegularPoints(int cx,int cy,int slide , float radius) {
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

    private List<PointF> getInscribedPoints(List<PointF> pts, float progress) {
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
        mChildren.clear();
        for (int i = 0; i < mDepth; i++) {
            List<PointF> pre;
            if (i == 0)
                pre = mPoints;
            else
                pre = mChildren.get(i-1);

            List<PointF> child = getInscribedPoints(pre,progress);
            mChildren.add(child);
        }
        return mChildren;
    }



}
