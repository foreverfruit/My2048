package com.example.my2048;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class GameLayout extends RelativeLayout{
	
	private final int mColumn = 4;		// 棋盘阵列
	private int mMargin = 10;			// item的margin值
	private GameItem [] mItems;			// 游戏的所有方格
	private int mPadding;				// 棋盘的padding值
	
	private ArrayList<GameItem> emptyItems = new ArrayList<GameItem>();
	
	private GestureDetector mGestureDetector;	// 游戏手势识别
	private int mScore;					// 分数

	public GameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMargin, getResources().getDisplayMetrics());  
        
        mPadding = min(getPaddingLeft(), getPaddingTop(), getPaddingRight(),getPaddingBottom());  // 设置Layout的内边距，四边一致，设置为四内边距中的最小值  
		
		mGestureDetector = new GestureDetector(context, new MyGestureDecterListener());
	}

	public GameLayout(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public GameLayout(Context context) {
		this(context,null);
	}

	private boolean itemHasAdded;	// 格子被添加的标志
	// 测量容器尺寸，确定item的尺寸
	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		// 面板的长度（正方形）
		int mLength = Math.min(getMeasuredHeight(), getMeasuredWidth());
		// 计算item的长宽
		int iLength = (mLength - mPadding*2 - mMargin*(mColumn-1))/mColumn; 
		
		// 初始化各item
		if(!itemHasAdded){
			if (mItems == null){  
				mItems = new GameItem[mColumn * mColumn];  
            } 
			emptyItems.clear();
			for(int i=0;i<mItems.length;i++){
				GameItem item = new GameItem(getContext());
				mItems[i] = item;
				item.setId(i + 1);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(iLength,iLength);	// 直接用tableLayout ？ 
				
				// 设置横向边距
				if(item.getId() % mColumn != 0 ){
					// 不是最后一列
					lp.rightMargin = mMargin;
				}else{
					lp.rightMargin = 0;
				}
				
				// 纵向边距
				if(item.getId() > mColumn ){
					// 不是第一行
					lp.topMargin = mMargin;
				}else{
					lp.topMargin = 0;
				}
				
				// 位置关系:横向
				if(item.getId() % mColumn != 1){
					// 不是第一列
					lp.addRule(RelativeLayout.RIGHT_OF, mItems[i-1].getId());
				}
				
				// 位置关系：纵向
				if(item.getId() > mColumn){
					// 不是第一行
					lp.addRule(RelativeLayout.BELOW, item.getId() - mColumn);
				}
				addView(item, lp);
				emptyItems.add(item);
			}
			//  随机一个格子生成数字2
			createNumber();
		}
		itemHasAdded = true;  
        setMeasuredDimension(mLength, mLength);  
	}

	private int min(int a,int b,int c,int d){
		int temp = a < b ? a:b;
		int temp2 = temp < c ? temp:c;
		return temp2 < d ? temp2 : d;
	}
	
	private Random random = new Random();
	/**
	 * 随机一个空格子，让它生成数字
	 * 该方法调用之前需要先调用isFull，初始化emptyItems集合；
	 * 手势动作  - 合并/移动 棋盘  - 得分 - 判断是否FULL - 生成数字
	 */
	private void createNumber(){
		// 随机一个没有数字的格子
		if(emptyItems!=null && !emptyItems.isEmpty()){
			int index = random.nextInt(emptyItems.size()); 
			emptyItems.get(index).refreshNumber();
		}
	}
	
	/**
	 * 判断棋盘满了没有，同时返回空格子的列表
	 * @return
	 */
	public boolean isFull(){
		emptyItems.clear();
		for(int i=0;i<mItems.length;i++){
			if(mItems[i].getNumber() <= 0){
				emptyItems.add(mItems[i]);
			}
		}
		return emptyItems.isEmpty() ? true : false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		if(mShowScoreListener!=null){
			mShowScoreListener.showScore();
		}
		return true;
	}
	
	private ShowScoreListener mShowScoreListener;
	public void setShowScoreListener(ShowScoreListener listener){
		this.mShowScoreListener = listener;
	}
	public interface ShowScoreListener{
		void showScore();
	}
	
	/**
	 * 棋盘移动:目测算法可以统一，暂时未实现，先分开分析每一种操作
	 * 目前算法比较复杂，因为程序小，所以基本还没有影响性能：moveTo、margeItem、needToMarge，这三个方法是运算的主体，大量的遍历，重复造成算法复杂
	 * @param action
	 */
	public void moveTo(ACTION action) {
		ArrayList<GameItem> items = new ArrayList<GameItem>();
		switch (action) {
		case UP:
		case DOWN:	// 列操作
			System.out.println("up or down");
			
			for(int i=0;i<mColumn;i++){
				items.clear();
				for(int j=0;j<mItems.length;j++){
					if(j % mColumn == i ){
						items.add(mItems[j]);
					}
				}
				// 合并某行或某列
				margeItem(items,action);
			}
			break;
		case LEFT:
		case RIGHT:	// 行操作
			System.out.println("right or left");
			
			for(int i=0;i<mItems.length;i=i+mColumn){
				items.clear();
				for(int j=0;j<mColumn;j++){
					items.add(mItems[i+j]);
				}
				// 合并某行或某列
				margeItem(items,action);
			}
			break;
		}
		if(!isFull()){
			createNumber();
		}
	}
	
	// 合并
	private void margeItem(ArrayList<GameItem> items, ACTION action) {
		switch (action) {
		case UP:
		case LEFT:	// 向前合并
			while(needToMarge(items,action));
			break;
		case DOWN:
		case RIGHT:	// 向后合并
			while(needToMarge(items,action));
			break;
		}
	}

	// 判断是否需要合并
	private boolean needToMarge(ArrayList<GameItem> items, ACTION action) {
		switch (action) {
		case UP:
		case LEFT:	// 向前合并
			for(int i=0;i<items.size()-1;i++){
				if(items.get(i).getNumber() == items.get(i+1).getNumber() && items.get(i).getNumber() != 0 ){
					int s = items.get(i).getNumber();
					items.get(i).refreshNumber();
					items.get(i+1).refreshNumber(0);
					mScore += s;
					return true;
				}
				if(items.get(i).getNumber() == 0 && items.get(i+1).getNumber() != 0 ){
					items.get(i).refreshNumber(items.get(i+1).getIndex());
					items.get(i+1).refreshNumber(0);
					return true;
				}
			}
			break;
		case DOWN:
		case RIGHT:	// 向后合并
			for(int i=items.size()-1;i>0;i--){
				if(items.get(i).getNumber() == items.get(i-1).getNumber() && items.get(i).getNumber() != 0 ){
					int s = items.get(i).getNumber();
					items.get(i).refreshNumber();
					items.get(i-1).refreshNumber(0);
					mScore += s;
					return true;
				}
				if(items.get(i).getNumber() == 0 && items.get(i-1).getNumber() != 0 ){
					items.get(i).refreshNumber(items.get(i-1).getIndex());
					items.get(i-1).refreshNumber(0);
					return true;
				}
			}
			break;
		}
		return false;
	}

	// 游戏手势识别
	class MyGestureDecterListener extends GestureDetector.SimpleOnGestureListener{
		
		private final float FLING_MIN_DISTANCE = 5F;
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
			
			if(isFull()){
				return false;
			}
			
			float x = e2.getX() - e1.getX();
			float y = e2.getY() - e1.getY();
			
			if(Math.abs(velocityX) > Math.abs(velocityY)  && Math.abs(x) > FLING_MIN_DISTANCE){
				// 横向操作
				if(velocityX > 0){
					moveTo(ACTION.RIGHT);
				}else{
					moveTo(ACTION.LEFT);
				}
			}
			
			if(Math.abs(velocityY) > Math.abs(velocityX) && Math.abs(y) > FLING_MIN_DISTANCE){
				// 纵向操作
				if(velocityY > 0){
					moveTo(ACTION.DOWN);
				}else{
					moveTo(ACTION.UP);
				}
			}
			return true;
		}
	}
	
	public int getScore(){
		return mScore;
	}
	
	// 手势动作枚举
	private enum ACTION{
		LEFT,RIGHT,UP,DOWN
	}
}
