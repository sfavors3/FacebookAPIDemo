package hao.test.fbapidemo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class CenterActivity extends Activity {
    
	// buttons
	private Button prepare;
	private Button signin;
	private Button getdata;
	private Button postdata;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // set orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // set layout
        setContentView(R.layout.center);
        
        // set Buttons
        getButton();
        setButtonEvent();
    }
	
	private void getButton ()
	{
		prepare = (Button)findViewById(R.id.button_preparation);
		signin = (Button)findViewById(R.id.button_signin);
		getdata = (Button)findViewById(R.id.button_getdata);
		postdata = (Button)findViewById(R.id.button_postdata);
	}
	
	private void setButtonEvent ()
	{
		prepare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(CenterActivity.this, Prepare.class);
				startActivity(it);
			}
		});
		
		signin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(CenterActivity.this, Signin.class);
				startActivity(it);
			}
		});
		
		getdata.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(CenterActivity.this, GetData.class);
				startActivity(it);
			}
		});
		
		postdata.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(CenterActivity.this, PostData.class);
				startActivity(it);
			}
		});
	}
}