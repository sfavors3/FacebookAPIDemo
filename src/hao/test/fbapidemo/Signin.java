package hao.test.fbapidemo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.LinkedList;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import hao.test.fbapidemo.FacebookUtility;


public class Signin extends Activity {

	
	// private parameters
	private Permission [] perm;
	private PermissionAdapter pa;
	private String [] selectedPerm;
	
	private String APP_ID;
	
	private ListView lv;
	private TextView tv;
	private ScrollView sv;
	
	private Button signin;
	private Button signout;
	private Button renew;
	private Button reset;
	
	private String access_token = null;
	private long expires = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // set orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // set layout
        setContentView(R.layout.signin);
        
        // get Views 
        tv = (TextView)findViewById(R.id.resp_signin);
        lv = (ListView)findViewById(R.id.list_signin);
        sv = (ScrollView)findViewById(R.id.scroll_signin);
        
        signin = (Button)findViewById(R.id.signin_signin);
        signout = (Button)findViewById(R.id.signout_signin);
        renew = (Button)findViewById(R.id.renew_signin);
        reset= (Button)findViewById(R.id.reset_signin);
        
        // load permissions
        loadPermissions();
        
        // set listview
        pa = new PermissionAdapter(this, R.layout.entry, perm);
        lv.setAdapter(pa);
        
        // initialize facebook
        initializeFacebook();
        
        // set button event
        setButtonEvent();
    }
    
    private class Permission
    {
    	private String mPermission = "";
    	private Boolean mChecked = false;
    	public Permission (String permission, Boolean checked){
    		mPermission = permission;
    		mChecked = checked;
    	}
    	public String getPermission(){
    		return mPermission;
    	}
    	public Boolean getChecked(){
    		return mChecked;
    	}
    	public void setChecked(Boolean checked){
    		mChecked = checked;
    	}
    }
    
    private class PermissionAdapter extends ArrayAdapter<Permission>
    {
        Context context; 
        int layoutResourceId;    
        
        public PermissionAdapter(Context context, int layoutResourceId, Permission [] perms) {
            super(context, layoutResourceId, perms);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
        	final Permission mPerm = this.getItem(position);
        	final CheckBox check;
        	
            if(convertView == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
                check = (CheckBox)convertView.findViewById(R.id.check);
            }
            else {    
                check = (CheckBox)convertView.findViewById(R.id.check);
            }
            check.setText(mPerm.getPermission());
            check.setChecked(mPerm.getChecked());
            
            check.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mPerm.getChecked()){
						mPerm.setChecked(false);
						tv.append("[Permission]: " + mPerm.getPermission() + " unselected." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					else{
						mPerm.setChecked(true);
						tv.append("[Permission]: " + mPerm.getPermission() + " selected." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					check.setChecked(mPerm.getChecked());
				}
			});
            
            return convertView;
        }
    }
    
    private void loadPermissions ()
    {
        LinkedList <String> raw = new LinkedList <String> ();
        try{
        	AssetManager am = this.getAssets();
        	InputStream fis = am.open("permission.dat");
        	InputStreamReader isr = new InputStreamReader(fis);
        	BufferedReader br = new BufferedReader(isr);
        	String temp = br.readLine();
        	while (temp != null){
        		raw.add(temp);
        		temp = br.readLine();
        	}
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }

        perm = new Permission [raw.size()];
        for (int k=0; k<raw.size(); k++)
        {
        	perm[k]= new Permission (raw.get(k), false);
        }
    }
    
    private void initializeFacebook ()
    {
    	APP_ID = getResources().getString(R.string.APP_ID);
    	if (FacebookUtility.fb == null)
    	{
    		FacebookUtility.fb = new Facebook(APP_ID);
    		FacebookUtility.runner = new AsyncFacebookRunner(FacebookUtility.fb);
    	}
    }
    
    private void setButtonEvent ()
    {
    	signin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LinkedList <String> temp = new LinkedList <String> ();
				for (int k=0; k<perm.length; k++) {
					if (perm[k].getChecked()){
						temp.add(perm[k].getPermission());
					}
				}
				selectedPerm = new String [temp.size()];
				for (int k=0; k<temp.size(); k++) {
					selectedPerm[k] = temp.get(k);
				}
				
				// go to facebook authorization
				FacebookUtility.fb.authorize(Signin.this, selectedPerm, new DialogListener(){
					@Override
					public void onComplete(Bundle values) {
						access_token = FacebookUtility.fb.getAccessToken();
						expires = FacebookUtility.fb.getAccessExpires();
						tv.append("[Sign in]: access_token = " + access_token + ".\n");
						sv.fullScroll(View.FOCUS_DOWN);
						tv.append("[Sign in]: access_token expires = " + Long.toString(expires) + ".\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					@Override
					public void onFacebookError(FacebookError e) {
						tv.append("[Sign in]: Facebook error." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					@Override
					public void onError(DialogError e) {
						tv.append("[Sign in]: Dialog error." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
					@Override
					public void onCancel() {
						tv.append("[Sign in]: Sign in cancelled." + "\n");
						sv.fullScroll(View.FOCUS_DOWN);
					}
				});
			}
		});
    	
    	signout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!FacebookUtility.fb.isSessionValid()) {
					tv.append("[Sign out]: no valid session" + ".\n");
					sv.fullScroll(View.FOCUS_DOWN);
					return;
				}	
				FacebookUtility.runner.logout(v.getContext(), new RequestListener() {
					@Override
					public void onMalformedURLException(MalformedURLException e, Object state) {
						runOnUiThread(new Runnable() {
						    public void run() {
						    	tv.append("[Sign out]: MalformedURLException." + "\n");
								sv.fullScroll(View.FOCUS_DOWN);
						    }
						});
					}
					@Override
					public void onIOException(IOException e, Object state) {
						runOnUiThread(new Runnable() {
						    public void run() {
						    	tv.append("[Sign out]: IOException." + "\n");
								sv.fullScroll(View.FOCUS_DOWN);
						    }
						});
					}
					@Override
					public void onFileNotFoundException(FileNotFoundException e, Object state) {
						runOnUiThread(new Runnable() {
						    public void run() {
						    	tv.append("[Sign out]: FileNotFoundException." + "\n");
								sv.fullScroll(View.FOCUS_DOWN);
						    }
						});
					}
					@Override
					public void onFacebookError(FacebookError e, Object state) {
						runOnUiThread(new Runnable() {
						    public void run() {
						    	tv.append("[Sign out]: Facebook error." + "\n");
								sv.fullScroll(View.FOCUS_DOWN);
						    }
						});
					}
					@Override
					public void onComplete(String response, Object state) {
						final String temp = response;
						runOnUiThread(new Runnable() {
						    public void run() {
						    	tv.append("[Sign out]: " + temp + ".\n");
								sv.fullScroll(View.FOCUS_DOWN);
						    }
						});
					}
				});
			}
		});
    	
    	renew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FacebookUtility.fb.extendAccessToken(v.getContext(), null);
				access_token = FacebookUtility.fb.getAccessToken();
				expires = FacebookUtility.fb.getAccessExpires();
				tv.append("[Renew]: access_token = " + access_token + ".\n");
				sv.fullScroll(View.FOCUS_DOWN);
				tv.append("[Renew]: access_token expires = " + Long.toString(expires) + ".\n");
				sv.fullScroll(View.FOCUS_DOWN);
			}
		});
    	
    	reset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tv.setText("");
				for (int k=0; k<perm.length; k++) {
					perm[k].setChecked(false);
					pa.notifyDataSetChanged();
				}
			}
		});
    }
    
    public void onResume()
    {    
        super.onResume();
        if (FacebookUtility.fb != null)
        {
        	FacebookUtility.fb.extendAccessTokenIfNeeded(this, null);
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookUtility.fb.authorizeCallback(requestCode, resultCode, data);
    }
}