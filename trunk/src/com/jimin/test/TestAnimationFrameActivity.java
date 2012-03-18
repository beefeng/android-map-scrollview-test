package com.jimin.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestAnimationFrameActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = LayoutInflater.from(this).inflate(R.layout.animation, null);
		builder.setView(view);
		
//		setContentView(R.layout.animation);
		
		View myView = view.findViewById(R.id.myView);
		
		final AnimationDrawable drawable = new AnimationDrawable();
		for (int i = 1; i <= 15; i++) {
			String str = String.format("lotteryani_%02d", i);
			Log.d("TestAnimationActivity", str);
			int id = getResources().getIdentifier(str, "drawable",
					getPackageName());
			Drawable mBitAniamtion = getResources().getDrawable(id);
			drawable.addFrame(mBitAniamtion, 50);
		}
		drawable.setOneShot(false);
		myView.setBackgroundDrawable(drawable);
		Button play = (Button) view.findViewById(R.id.play);
		play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawable.start();
			}
		});
		
		builder.show();
	}
	
}
