package com.lvable.ningjiaqi.polyloading;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ningjiaqi on 16/4/21.
 */
public class ShapeUtil {



    public static Path getRegularPath(List<PointF> pts, int cx, int cy, int slide , float radius) {
        Path path = new Path();
        pts.clear();
        for (int i = 0;i < slide;i++) {
            float x = (float) (radius * Math.sin(i * 2 * Math.PI / slide));
            float y = (float) (radius * Math.cos(i * 2 * Math.PI / slide));
            x += cx;
            y += cy;
            pts.add(new PointF(x,y));
            if (i == 0) path.moveTo(x,y);
            else path.lineTo(x,y);
        }
        path.close();
        return path;
    }

    public static List<PointF> getRegularPoints(int cx,int cy,int slide , float radius) {
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

    public static List<PointF> getInscribedPoints(List<PointF> pts, float progress) {
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



    private static PointF getInterpolatedPoint(PointF start, PointF end, float progress) {
        float dx = end.x - start.x;
        float dy = end.y - start.y;

        float newX = start.x + dx * progress;
        float newY = start.y + dy * progress;

        return new PointF(newX,newY);
    }



}
