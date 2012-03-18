package com.jimin.test.mapscrollview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.jimin.test.mapscrollview.R;
import com.jimin.test.mapscrollview.view.MapScrollView;

public class TestActivity extends Activity {
	
	
	private int left;
	private int top;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        
        final MapScrollView myView = (MapScrollView) findViewById(R.id.myView);
        
        myView.setLayoutParams(new FrameLayout.LayoutParams(10000, 10000));
        
        final Button btn = (Button) findViewById(R.id.locate);
        btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myView.scrollTo(0, 0);
			}
		});
        
        Button report = (Button) findViewById(R.id.report);
        report.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				myView.test();
			}
		});
    }
}