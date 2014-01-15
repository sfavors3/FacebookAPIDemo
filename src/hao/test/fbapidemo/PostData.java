package hao.test.fbapidemo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.*;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class PostData extends Activity {
	
	// private variables
	private EditText edit;
	private EditText id;
	private ScrollView sv;
	private TextView tv;
	private Button my_wall;
	private Button friend_wall;
	private Button request;
	private Button reset;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // set orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // set layout
        setContentView(R.layout.postdata);
        
        // remove focus of edittext
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        
        // get views
        edit = (EditText)findViewById(R.id.edit_postdata);
        id = (EditText)findViewById(R.id.id_postdata);
        sv = (ScrollView)findViewById(R.id.scroll_postdata);
        tv = (TextView)findViewById(R.id.resp_postdata);
        
        my_wall = (Button)findViewById(R.id.user_wall_postdata);
        friend_wall = (Button)findViewById(R.id.friend_wall_postdata);
        request = (Button)findViewById(R.id.request_postdata);
        reset = (Button)findViewById(R.id.reset_postdata);
        
        // set button event
        setButtonEvent();
	}
	
	private boolean checkFacebook ()
	{
		if (FacebookUtility.fb == null) {
			return false;
		}
		else if (!FacebookUtility.fb.isSessionValid()) {
			return false;
		}
		else {
			return true;
		}
	}
	
	private void setButtonEvent ()
	{
		my_wall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!checkFacebook()) {
					Toast.makeText(PostData.this, "Error: Please sign in first.", Toast.LENGTH_SHORT).show();
					return;
				}
				FacebookUtility.fb.dialog(v.getContext(), "feed", new DialogListener() {
					@Override
					public void onFacebookError(FacebookError e) {
						tv.append("[My wall]: Facebook error." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					@Override
					public void onError(DialogError e) {
						tv.append("[My wall]: Dialog error." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					@Override
					public void onComplete(Bundle values) {
						tv.append("[My wall]: Post complete." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					@Override
					public void onCancel() {
						tv.append("[My wall]: Post cancelled." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
				});
			}
		});
		
		friend_wall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!checkFacebook()) {
					Toast.makeText(PostData.this, "Error: Please sign in first.", Toast.LENGTH_SHORT).show();
					return;
				}
				Bundle params = new Bundle();
				String ID = id.getText().toString();
				if (ID.equals("")) {
					Toast.makeText(PostData.this, "Error: no target friend's ID.", Toast.LENGTH_SHORT).show();
					return;
				}
				params.putString("to", ID);
				FacebookUtility.fb.dialog(v.getContext(), "feed", params, new DialogListener() {
					@Override
					public void onFacebookError(FacebookError e) {
						tv.append("[Friend's wall]: Facebook error." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					@Override
					public void onError(DialogError e) {
						tv.append("[Friend's wall]: Dialog error." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					@Override
					public void onComplete(Bundle values) {
						tv.append("[Friend's wall]: Post complete." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					@Override
					public void onCancel() {
						tv.append("[Friend's wall]: Post cancelled." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
				});
			}
		});
		
		request.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!checkFacebook()) {
					Toast.makeText(PostData.this, "Error: Please sign in first.", Toast.LENGTH_SHORT).show();
					return;
				}
				Bundle params = new Bundle();
				String mesa = edit.getText().toString();
				if (!mesa.equals("")) {
					params.putString("message", mesa);
				}
				else {
					Toast.makeText(PostData.this, "Hi, just input some message.", Toast.LENGTH_SHORT).show();
					return;
				}
				params.putString("caption", "API Demo");
				params.putString("link", "market://details?id=hao.test.fbapidemo");
				FacebookUtility.fb.dialog(v.getContext(), "apprequests", params, new DialogListener() {
					@Override
					public void onFacebookError(FacebookError e) {
						tv.append("[Request]: Facebook error." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					@Override
					public void onError(DialogError e) {
						tv.append("[Request]: Dialog error." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					@Override
					public void onComplete(Bundle values) {
						tv.append("[Request]: Post complete." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					@Override
					public void onCancel() {
						tv.append("[Request]: Post cancelled." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
				});
			}
		});
		
		reset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				edit.setText("");
				id.setText("");
				tv.setText("");
			}
		});
	}
}