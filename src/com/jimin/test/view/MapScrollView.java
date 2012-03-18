package com.jimin.test.view;

import java.util.ArrayList;
import java.util.Random;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.jimin.test.R;

public class MapScrollView extends ViewGroup{

	private static final String TAG = "MyViewGroup";
	
	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 480;
	
	/**
	 * 地图X向坐标间距修正值
	 */
	private static final int DELTA_X = 30;
	/**
	 * 地图Y向坐标间距修正值
	 */
	private static final int DELTA_Y = 10;
	
	private static final int GOE_MAP_TILE_COUNT_X = 7;;
	private static final int GOE_MAP_TILE_COUNT_Y = 15;
	private static final int GOE_MAP_DATA_COUNT_X = 13;
	private static final int GOE_MAP_DATA_COUNT_Y = 27;
	public static final int GOE_MAP_TILE_WIDTH = 240;
	public static final int GOE_MAP_TILE_HEIGHT = 120;
	public static final int GOE_MAP_TILE_WIDTH_HALF = 120;
	public static final int GOE_MAP_TILE_HEIGHT_HALF = 60;
	private static final int GOE_MAP_LABEL_OFFSET = 55;
	private static final int GOE_MAP_LABEL_WIDTH = 40;
	private static final int GOE_MAP_LABEL_HEIGHT = 20;
	private static final int GOE_MAP_TILE_MAX_X = 15;
	private static final int GOE_MAP_TILE_MAX_Y = 30;
	
	private static final int GOE_MAP_X_RANGE = (GOE_MAP_TILE_WIDTH - DELTA_X)* (GOE_MAP_DATA_COUNT_X - 1);
	private static final int GOE_MAP_Y_RANGE = (GOE_MAP_TILE_HEIGHT_HALF - DELTA_Y)* (GOE_MAP_DATA_COUNT_Y - 1);
	
	
	private Context mContext;
	
	private float mLastX;
	private float mLastY;

	private float mClickX;
	private float mClickY;
	
	private int centerX;
	private int centerY;
	
	private boolean isRequestingData = false;
	
	private int lastCenterX;
	private int lastCenterY;
	

	private ArrayList<ArrayList<TileImageView>> imageXYList;
	
	public MapScrollView(Context context) {
		super(context);
		mContext = context;
		initTileImage();
	}

	public MapScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initTileImage();
		
	}
	
	public MapScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initTileImage();
	}
	
	private void initTileImage() {
		int index = 0;
		
		imageXYList = new ArrayList<ArrayList<TileImageView>>();
		for (int j = -7; j <= 7; j ++) {
			
			ArrayList<TileImageView> xList = new ArrayList<TileImageView>();
			
			for (int i = -3; i <= 3; i ++) {
				TileImageView image = getView(null, i, j);
				image.setImageResource(R.drawable.empty_1);
				addView(image, index ++);
				
				xList.add(image);
			}
			imageXYList.add(xList);
		}
		
		// 初始时中心点坐标
		centerX = SCREEN_WIDTH / 2;
		centerY = SCREEN_HEIGHT / 2;
		
		// 请求数据
//		requestData();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		Log.d(TAG, "onMeasure called, width = " + width + ", height = " + height);
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d(TAG, "onLayout called, " + l + "," + t + "," + r + "," + b);
		int index = 0;
		for (int j = -7; j <= 7; j ++) {
			for (int i = -3; i <= 3; i ++) {
				TileImageView child = (TileImageView) getChildAt(index ++);
				layoutTile(child, i, j);
			}
		}
		
		lastCenterX = getScrollX();
		lastCenterY = getScrollY();
	}
	
	private void requestData() {
		if (isRequestingData) return;
		isRequestingData = true;
		
		final ProgressDialog dialog = new ProgressDialog(mContext);
		dialog.setTitle("Loading");
		dialog.setMessage("Loading Map Data ...");
		dialog.show();
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
				onLoad();
				isRequestingData = false;
			}
		}, 3000);
	}
	
	private void onLoad() {
		clearAllTile();
		scrollTo(0, 0);
		int count = GOE_MAP_DATA_COUNT_X * GOE_MAP_DATA_COUNT_Y;
		for (int i = 0; i < count; i++) {
			TileImageView image = (TileImageView) getChildAt(i);
			image.setImageResource(getRandomDrawable());
		}
	}
	
	/**
	 * 将地图块上所有图片资源清空为空地
	 */
	private void clearAllTile() {
		int count = GOE_MAP_DATA_COUNT_X * GOE_MAP_DATA_COUNT_Y;
		for (int i = 0; i < count; i++) {
			TileImageView image = (TileImageView) getChildAt(i);
			image.setImageResource(R.drawable.empty_1);
		}
	}
	
	/**
	 * 到达边界
	 */
	private void onReachBoundary() {
		int center[] = getCenter();
		requestData();
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastX = x;
			mLastY = y;
			mClickX = x;
			mClickY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			int offsetX = (int) (mLastX - x);
			int offsetY = (int) (mLastY - y);
			scrollBy(offsetX, offsetY);
			
			mLastX = x;
			mLastY = y;
			break;
		case MotionEvent.ACTION_UP:
			if (x == mClickX && y == mClickY) {
				onTileClicked();
			}
			break;
		}
		return true;
	}
	
	@Override
	public void scrollBy(int x, int y) {
		super.scrollBy(x, y);
	}
	
	@Override
	public void scrollTo(int x, int y) {
		
		/*if ((x < 0 && (SCREEN_WIDTH/2 - x) > GOE_MAP_X_RANGE / 2) || 
				(x > 0 && (SCREEN_WIDTH/2 + x) > GOE_MAP_X_RANGE / 2) || 
				(y < 0 && (SCREEN_HEIGHT/2 - y) > GOE_MAP_Y_RANGE / 2) ||
				(y > 0 && (SCREEN_HEIGHT/2 + y) > GOE_MAP_Y_RANGE / 2)) {
			// 到达边界
			onReachBoundary();
			return;
		}*/

		centerX = SCREEN_WIDTH/2 + x;
		centerY = SCREEN_HEIGHT/2 + y;
		
		super.scrollTo(x, y);
		
		checkBoundary(x, y);
	}

	private void checkBoundary(int x, int y) {
		if (x - lastCenterX > GOE_MAP_TILE_WIDTH) {
			// 重用i坐标最小的view
			for (ArrayList<TileImageView> xList : imageXYList) {
				TileImageView view = xList.remove(0);
				Point p = (Point) view.getTag();
				Log.d(TAG, "begin reuse image orig is " + p.x + "," + p.y);
				p.x += GOE_MAP_TILE_COUNT_X;
				TileImageView convertView = getView(view, p.x, p.y);
				
				layoutTile(convertView, p.x, p.y);
				
				xList.add(convertView);
			}
		}
		
		if (x - lastCenterX < -GOE_MAP_TILE_WIDTH) {
			// 重用i坐标最大的view
			for (ArrayList<TileImageView> xList : imageXYList) {
				TileImageView view = xList.remove(xList.size() - 1);
				Point p = (Point) view.getTag();
				Log.d(TAG, "begin reuse image orig is " + p.x + "," + p.y);
				p.x -= GOE_MAP_TILE_COUNT_X;
				TileImageView convertView = getView(view, p.x, p.y);
				
				layoutTile(convertView, p.x, p.y);
				
				xList.add(0, convertView);
			}
		}
		
		if (y - lastCenterY > GOE_MAP_TILE_HEIGHT) {
			// 重用j坐标最小的view
			ArrayList<TileImageView> xList = imageXYList.remove(0);
			for (TileImageView view : xList) {
				
				Point p = (Point) view.getTag();
				Log.d(TAG, "begin reuse image orig is " + p.x + "," + p.y);
				p.y += GOE_MAP_TILE_COUNT_Y;
				TileImageView convertView = getView(view, p.x, p.y);
				
				layoutTile(convertView, p.x, p.y);
			}
			imageXYList.add(xList);
		}
		
		if (y - lastCenterY < -GOE_MAP_TILE_HEIGHT) {
			// 重用j坐标最大的view
			ArrayList<TileImageView> xList = imageXYList.remove(imageXYList.size() - 1);
			for (TileImageView view : xList) {
				
				Point p = (Point) view.getTag();
				Log.d(TAG, "begin reuse image orig is " + p.x + "," + p.y);
				p.y -= GOE_MAP_TILE_COUNT_Y;
				TileImageView convertView = getView(view, p.x, p.y);
				
				layoutTile(convertView, p.x, p.y);
			}
			imageXYList.add(0, xList);
		}
	}
	
	
	private void layoutTile(TileImageView view, int i, int j) {
		int offsetX;
		int offsetY;
		int left, top;
		
		offsetX = i * GOE_MAP_TILE_WIDTH + (Math.abs(j % 2) - 1) * GOE_MAP_TILE_WIDTH_HALF;
		offsetY = (j - 1) * GOE_MAP_TILE_HEIGHT_HALF - j * DELTA_Y;
		
		if (i == 0 && j % 2 == 0) {
			// 中心线
		} else {
		
			if (Math.abs(j % 2) == 1) {
				offsetX = offsetX - (2 * i + 1) * DELTA_X;
			} else {
				offsetX = offsetX - 2 * i * DELTA_X;
			}
		}
		
		left = SCREEN_WIDTH / 2 + offsetX;
		top = SCREEN_HEIGHT / 2 + offsetY;
		view.layout(left, top, left + GOE_MAP_TILE_WIDTH, top + GOE_MAP_TILE_HEIGHT);
		Log.d(TAG, "layout tile image["+ i + "," + j + "] view at " + left + "," + top + ","
				+ (left + GOE_MAP_TILE_WIDTH) + ","
				+ (top + GOE_MAP_TILE_HEIGHT));
	}
	
	
	private TileImageView getView(TileImageView convertView, int i, int j) {
		
		if (convertView == null) {
			convertView = new TileImageView(mContext, i, j);
		}
		bindTileData(convertView, i, j);
		
		lastCenterX = getScrollX();
		lastCenterY = getScrollY();
		return convertView;
	}
	
	private void bindTileData(TileImageView view, int i, int j) {
		view.setX(i);
		view.setY(j);
		view.setImageResource(getRandomDrawable());
	}
	
	
	public int[] getCenter(){
		return getDataIJ(centerX, centerY);
	}
	
	private int[] getDataIJ(int x, int y) {
		
		int count = getChildCount();
		long start = System.currentTimeMillis();
		for (int i=0; i < count; i ++) {
			TileImageView view = (TileImageView) getChildAt(i);
			if (view.containsPosition(x, y)) {
				return view.getDataIJ();
			}
		}
		Log.d(TAG, " cost time == " + (System.currentTimeMillis() - start));
		return null;
	}
	
	private void onTileClicked() {
		int realPosX = getScrollX() + (int)mClickX;
		int realPosY = getScrollY() + (int)mClickY;
		
		Log.d(TAG, "------ onTileClicked ------ real position=(" + realPosX + "," + realPosY + ")");
		// TODO show dialog
		getDataIJ(realPosX, realPosY);
	}
	
	
	private int getRandomDrawable() {
		Random r = new Random();
		int next = r.nextInt(24);
		int drawable = R.drawable.city_1;
		switch(next) {
		case 0: drawable = R.drawable.empty_1; break;
		case 1: drawable = R.drawable.empty_2; break;
		case 2: drawable = R.drawable.empty_3; break;
		case 3: drawable = R.drawable.city_1; break;
		case 4: drawable = R.drawable.city_2; break;
		case 5: drawable = R.drawable.city_3; break;
		case 6: drawable = R.drawable.crop_1; break;
		case 7: drawable = R.drawable.crop_2; break;
		case 8: drawable = R.drawable.crop_3; break;
		case 9: drawable = R.drawable.wood_1; break;
		case 10: drawable = R.drawable.wood_2; break;
		case 11: drawable = R.drawable.wood_3; break;
		case 12: drawable = R.drawable.stone_1; break;
		case 13: drawable = R.drawable.stone_2; break;
		case 14: drawable = R.drawable.stone_3; break;
		case 15: drawable = R.drawable.tower_1; break;
		case 16: drawable = R.drawable.tower_2; break;
		case 17: drawable = R.drawable.tower_3; break;
		case 18: drawable = R.drawable.dragon_1; break;
		case 19: drawable = R.drawable.dragon_2; break;
		case 20: drawable = R.drawable.dragon_3; break;
		case 21: drawable = R.drawable.iron_1; break;
		case 22: drawable = R.drawable.iron_2; break;
		case 23: drawable = R.drawable.iron_3; break;
		}
		return drawable;
	}
	
}
