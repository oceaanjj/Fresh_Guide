package com.example.freshguide.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.example.freshguide.R;

public class RoutePathOverlayView extends View {

    private final Paint routePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint originCircleFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint originCircleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path routePath = new Path();
    private final float pinSizePx;
    private final float pinTipOffsetPx;
    private final float originCircleRadiusPx;

    @Nullable
    private PointF startPoint;
    @Nullable
    private PointF endPoint;
    @Nullable
    private Float bendX;

    @Nullable
    private Drawable originPinDrawable;
    @Nullable
    private Drawable destinationPinDrawable;

    public RoutePathOverlayView(Context context) {
        this(context, null);
    }

    public RoutePathOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        routePaint.setStyle(Paint.Style.STROKE);
        routePaint.setStrokeWidth(dpToPx(2));
        routePaint.setColor(ContextCompat.getColor(context, R.color.green_primary));
        routePaint.setPathEffect(new android.graphics.DashPathEffect(
                new float[]{dpToPx(6), dpToPx(5)}, 0f));
        routePaint.setStrokeCap(Paint.Cap.ROUND);
        routePaint.setStrokeJoin(Paint.Join.ROUND);

        originCircleFillPaint.setStyle(Paint.Style.FILL);
        originCircleFillPaint.setColor(ContextCompat.getColor(context, R.color.green_primary));

        originCircleStrokePaint.setStyle(Paint.Style.STROKE);
        originCircleStrokePaint.setStrokeWidth(dpToPx(2));
        originCircleStrokePaint.setColor(ContextCompat.getColor(context, android.R.color.white));

        originPinDrawable = null;
        destinationPinDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_map_room_pin_red);
        pinSizePx = dpToPx(28);
        pinTipOffsetPx = dpToPx(1);
        originCircleRadiusPx = dpToPx(8);

        setWillNotDraw(false);
    }

    public void clearRoute() {
        startPoint = null;
        endPoint = null;
        bendX = null;
        invalidate();
    }

    public void setRoute(@Nullable PointF startPoint, @Nullable PointF endPoint, @Nullable Float bendX) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.bendX = bendX;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (startPoint == null || endPoint == null) {
            return;
        }

        routePath.reset();
        routePath.moveTo(startPoint.x, startPoint.y);

        if (bendX != null
                && (Math.abs(startPoint.x - bendX) > dpToPx(4)
                || Math.abs(endPoint.x - bendX) > dpToPx(4))) {
            routePath.lineTo(bendX, startPoint.y);
            routePath.lineTo(bendX, endPoint.y);
        }

        routePath.lineTo(endPoint.x, endPoint.y);
        canvas.drawPath(routePath, routePaint);

        drawOriginMarker(canvas, startPoint);
        drawPin(canvas, endPoint, destinationPinDrawable);
    }

    private void drawOriginMarker(@NonNull Canvas canvas, @NonNull PointF anchor) {
        canvas.drawCircle(anchor.x, anchor.y, originCircleRadiusPx, originCircleFillPaint);
        canvas.drawCircle(anchor.x, anchor.y, originCircleRadiusPx, originCircleStrokePaint);
    }

    private void drawPin(@NonNull Canvas canvas, @NonNull PointF anchor, @Nullable Drawable drawable) {
        if (drawable == null) {
            return;
        }
        int halfWidth = Math.round(pinSizePx / 2f);
        int left = Math.round(anchor.x) - halfWidth;
        int top = Math.round(anchor.y - pinSizePx + pinTipOffsetPx);
        int right = left + Math.round(pinSizePx);
        int bottom = top + Math.round(pinSizePx);
        drawable.setBounds(left, top, right, bottom);
        drawable.draw(canvas);
    }

    private float dpToPx(int dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
