package com.example.my2048;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
/**
 * 2048 的每一个格子
 * 显然玩到尽头就是数组越界崩溃……
 *
 */
public class GameItem extends View{

	private int mNumber;	// 数字
	private int mColorBg;	// 格子背景颜色
	private Paint mPaint;	// 画笔
	private Rect mRect;		// drawText区域
	
	// 方格数字与对应的颜色
	private int index = 0;
	private int [] numbers = {0,2,4,8,16,32,64,128,512,1024,2048};
	private int [] colors = {0xFFCCC0B3,0xFFEEE4DA,0xFFEDE0C8,0xFFF2B179,0xFFF49563,0xFFF5794D,0xFFF55D37,0xFFEEE863,0xFFEDB04D,0xFFECB04D,0xFFEB9437,0xFFEA7821};
	
	public GameItem(Context context) {
		this(context,null);
	}

	public GameItem(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public GameItem(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
		
		mRect = new Rect();
		
		mNumber = numbers[index];
		mColorBg = colors[index];
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mPaint.setTextSize(getWidth()/4); 
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 画背景格子
		mPaint.setColor(mColorBg);
		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
		
		// 画数字
		if(mNumber != 0 ){
			drawNumber(canvas);
		}
			
	}
	
	// 绘制数字
	private void drawNumber(Canvas canvas){
		String str = mNumber + "";
		mPaint.setColor(Color.BLACK);  
		mPaint.getTextBounds(str, 0, str.length(), mRect);
		
		float x = (getWidth() - mRect.width()) / 2;  
        float y = getHeight() / 2 + mRect.height() / 2;  
        canvas.drawText(str, x, y, mPaint);  
	}
	
	public void refreshNumber(){
		index ++ ;
		mNumber = numbers[index];
		mColorBg = colors[index];
		
		postInvalidate();
	}
	
	public void refreshNumber(int index){
		this.index = index;
		mNumber = numbers[index];
		mColorBg = colors[index];
		
		postInvalidate();
	}
	
	public int getIndex(){
		return index;
	}
	
	public int getNumber(){
		return mNumber;
	}
}
