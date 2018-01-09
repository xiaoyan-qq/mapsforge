package org.mapsforge.samples.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhangdezhi1702 on 2018/1/8.
 */

public class OfflineMapRectDrawView extends View {
    private Paint mPaint;
    private Canvas mBufferCanvas;
    private Bitmap mBufferBitmap;

//    private RectF mRectF;
    private PointF startPoint,lastMovePoint/*最后一次的移动位置*/;

    public OfflineMapRectDrawView(Context context) {
        super(context);
    }

    public OfflineMapRectDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.GREEN);
        setBackgroundColor(Color.WHITE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                if (mBufferBitmap == null) {
                    mBufferBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
                    mBufferCanvas = new Canvas(mBufferBitmap);
                }
                startPoint=new PointF(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                //如果此次移动距离与上一次距离不足15个像素，则不处理
                if (lastMovePoint!=null&&Math.abs(lastMovePoint.x-event.getX())+Math.abs(lastMovePoint.y-event.getY())<12){
                    break;
                }
                lastMovePoint=new PointF(event.getX(),event.getY());

                float top,left,bottom,right;
                if (lastMovePoint.x>startPoint.x){
                    left=startPoint.x;
                    right=lastMovePoint.x;
                }else {
                    left=lastMovePoint.x;
                    right=startPoint.x;
                }

                if (lastMovePoint.y>startPoint.y){
                    top=startPoint.y;
                    bottom=lastMovePoint.y;
                }else {
                    top=lastMovePoint.y;
                    bottom=startPoint.y;
                }

                mBufferCanvas.drawRect(left,top,right,bottom,mPaint);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                startPoint=null;
                lastMovePoint=null;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBufferBitmap == null) {
            return;
        }
        canvas.drawBitmap(mBufferBitmap,0,0,null);
    }
}
