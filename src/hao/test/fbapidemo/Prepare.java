package hao.test.fbapidemo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;


public class Prepare extends Activity{

	// private view objects
	private WebView wv;
	private Button left;
	private Button right;
	private TextView tv;
	
	// private step counter
	private int counter;
	private final int counter_min = 0;
	private final int counter_max = 10;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // set orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // set layout
        setContentView(R.layout.prepare);
        
        // set View
        getViews();
        setButton();
        initialize();
    }
    
    private void getViews ()
    {
    	wv = (WebView)findViewById(R.id.prepare_webView);
    	left = (Button)findViewById(R.id.prepare_left);
    	right = (Button)findViewById(R.id.prepare_right);
    	tv = (TextView)findViewById(R.id.prepare_label);
    }
    
    private void setButton ()
    {
    	left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (decreaseCounter())
				{
					wv.loadUrl("file:///android_asset/step"+counter+".html");
					if (counter == counter_min) {
						tv.setText("Disclaimer");
					}
					else {
						tv.setText("Step " + counter);
					}
				}
			}
		});
    	
    	right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (increaseCounter())
				{
					wv.loadUrl("file:///android_asset/step"+counter+".html");
					tv.setText("Step " + counter);
				}
			}
		});
    }
    
    private void initialize ()
    {
    	counter = counter_min;
    	tv.setText("Disclaimer");
    	wv.loadUrl("file:///android_asset/step"+counter+".html");
    }
    
    private boolean increaseCounter ()
    {
    	if (counter+1>counter_max){
    		counter = counter_max;
    		return false;
    	}
    	else{
    		counter = counter + 1;
    		return true;
    	}
    }
    
    private boolean decreaseCounter ()
    {
    	if (counter-1<counter_min){
    		counter = counter_min;
    		return false;
    	}
    	else{
    		counter = counter - 1;
    		return true;
    	}
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
      savedInstanceState.putInt("counter", counter);
    }
}