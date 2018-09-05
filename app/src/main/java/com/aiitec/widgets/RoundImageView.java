package com.aiitec.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * 圆角ImageView
 * 
 * @author shc
 * 
 */
public class RoundImageView extends AppCompatImageView {

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RoundImageView(Context context) {
		super(context);
		init();
	}

	private final RectF roundRect = new RectF();
	private float rect_adius = 6;
	private float ratio;
	private boolean isNeedLine = true;
	private final Paint maskPaint = new Paint();
	private final Paint zonePaint = new Paint();
	private final Paint linePaint = new Paint();

	public void setNeedLine(boolean needLine) {
		isNeedLine = needLine;
	}

	private void init() {
		maskPaint.setAntiAlias(true);
		maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		//
		zonePaint.setAntiAlias(true);
		zonePaint.setColor(Color.WHITE);

		linePaint.setAntiAlias(true);
		linePaint.setColor(0xFFDDDDDD);
		linePaint.setStyle(Paint.Style.STROKE);
		//
		float density = getResources().getDisplayMetrics().density;
		rect_adius = rect_adius * density;
	}

	public void setRectAdius(float adius) {
		rect_adius = adius;
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (ratio > 0) {
			int height = MeasureSpec.getSize(widthMeasureSpec);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
					MeasureSpec.EXACTLY);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int w = getWidth();
		int h = getHeight();
		roundRect.set(0, 0, w, h);
	}

	// @Override
	// public void draw(Canvas canvas) {
	// try {
	// canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
	// canvas.drawRoundRect(roundRect, rect_adius, rect_adius, zonePaint);
	// //
	// canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
	// super.draw(canvas);
	// canvas.restore();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	@Override
	protected void onDraw(Canvas canvas) {
		if (rect_adius > 0) {
			try {
				canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
				canvas.drawRoundRect(roundRect, rect_adius, rect_adius, zonePaint);
				canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
				super.onDraw(canvas);
				canvas.restore();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			super.onDraw(canvas);
		}
		if(isNeedLine){
			canvas.drawRoundRect(roundRect, rect_adius, rect_adius, linePaint);
		}

	}

	public float getRatio() {
		return ratio;
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
		invalidate();
	}

}
