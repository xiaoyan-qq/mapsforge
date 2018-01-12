package org.mapsforge.samples.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RectF;
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

    private RectF mRectF;
    private PointF startPoint, lastMovePoint/*最后一次的移动位置*/;

    public OfflineMapRectDrawView(Context context) {
        super(context);
    }

    public OfflineMapRectDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GREEN);
        mRectF=new RectF();
        setBackgroundColor(Color.argb(33, 0, 0, 0));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mBufferBitmap == null) {
                    mBufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                    mBufferCanvas = new Canvas(mBufferBitmap);
                } else {
                    mBufferCanvas.drawColor(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.CLEAR);
                    mRectF.set(0,0,0,0);
                }
                startPoint = new PointF(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                mBufferCanvas.drawColor(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.CLEAR);
                //如果此次移动距离与上一次距离不足15个像素，则不处理
                if (lastMovePoint != null && Math.abs(lastMovePoint.x - event.getX()) + Math.abs(lastMovePoint.y - event.getY()) < 12) {
                    break;
                }
                lastMovePoint = new PointF(event.getX(), event.getY());

                if (lastMovePoint.x > startPoint.x) {
                    mRectF.left = startPoint.x;
                    mRectF.right = lastMovePoint.x;
                } else {
                    mRectF.left = lastMovePoint.x;
                    mRectF.right = startPoint.x;
                }

                if (lastMovePoint.y > startPoint.y) {
                    mRectF.top = startPoint.y;
                    mRectF.bottom = lastMovePoint.y;
                } else {
                    mRectF.top = lastMovePoint.y;
                    mRectF.bottom = startPoint.y;
                }

                mBufferCanvas.drawRect(mRectF, mPaint);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                startPoint = null;
                lastMovePoint = null;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBufferBitmap == null) {
            return;
        }
        canvas.drawBitmap(mBufferBitmap, 0, 0, null);
    }

    /**
     * 返回绘制的矩形
     * */
    public RectF getDrawRect(){
        if (mRectF.height()==0||mRectF.width()==0){
            return null;
        }
        return mRectF;
    }
}
