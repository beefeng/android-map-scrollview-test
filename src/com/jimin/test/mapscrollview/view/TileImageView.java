package com.jimin.test.mapscrollview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class TileImageView extends ImageView{

	private static final String TAG = "TileImageView";
	private int posX, posY;
	
	public TileImageView(Context context, int x, int y) {
		super(context);
		posX = x;
		posY = y;
		
		Point p = new Point(x, y);
		this.setTag(p);
	}
	
	public void setX(int x) {
		posX = x;
	}
	
	public void setY(int y) {
		posY = y;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Paint paint = new Paint();
		paint.setTextSize(21);
		
		if (posX == 0 && posY == 0)
			paint.setColor(Color.RED);
		else
			paint.setColor(Color.WHITE);
		
		int width = getWidth();
		int height = getHeight();
		
		canvas.drawText("(" + posX + "," + posY + ")", width / 2 - 20, height / 2, paint);
	}

	public boolean containsPosition(int x, int y) {
		int offsetX = x - getLeft();
		int offsetY = y - getTop() + 10;
		
		if (offsetX > MapScrollView.GOE_MAP_TILE_WIDTH_HALF / 2 && 
				offsetX < MapScrollView.GOE_MAP_TILE_WIDTH_HALF * 3 / 2 &&
				offsetY > MapScrollView.GOE_MAP_TILE_HEIGHT_HALF / 2 && 
				offsetY < MapScrollView.GOE_MAP_TILE_HEIGHT_HALF * 3 / 2){
			Log.d(TAG, "in may area, ==============> (" + posX + "," + posY + ")");
			return true;
		}
		return false;
	}
	
	public int[] getDataIJ() {
		return new int[]{posX, posY};
	}
}
