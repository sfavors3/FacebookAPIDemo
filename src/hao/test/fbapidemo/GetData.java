package hao.test.fbapidemo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.LinkedList;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.FacebookError;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import hao.test.fbapidemo.FacebookUtility;

public class GetData extends Activity {
	
	// private variables
	private GetMethod [] mGets;
	private GetMethodAdapter ga;
	
	private ListView lv;
	private TextView tv;
	
	private Button get;
	private Button reset;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // set layout
        setContentView(R.layout.getdata);
        
        // get views
        lv = (ListView)findViewById(R.id.list_getdata);
        tv = (TextView)findViewById(R.id.resp_getdata);
        get = (Button)findViewById(R.id.get_getdata);
        reset = (Button)findViewById(R.id.reset_getdata);
        
        // set list
        loadGetMethod();
        ga = new GetMethodAdapter(this, R.layout.entry, mGets);
        lv.setAdapter(ga);
        
        // set buttons
        setButtonEvent ();
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
	
	private class GetMethod
    {
    	private String mGetMethod = "";
    	private Boolean mChecked = false;
    	public GetMethod (String get, Boolean checked){
    		mGetMethod = get;
    		mChecked = checked;
    	}
    	public String getMethod(){
    		return mGetMethod;
    	}
    	public Boolean getChecked(){
    		return mChecked;
    	}
    	public void setChecked(Boolean checked){
    		mChecked = checked;
    	}
    }
    
    private class GetMethodAdapter extends ArrayAdapter<GetMethod>
    {
        Context context; 
        int layoutResourceId;    
        
        public GetMethodAdapter(Context context, int layoutResourceId, GetMethod [] getMethods) {
            super(context, layoutResourceId, getMethods);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
        	final GetMethod mGet = this.getItem(position);
        	final CheckBox check;
        	final int pos = position;
        	
            if(convertView == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
                check = (CheckBox)convertView.findViewById(R.id.check);
            }
            else {    
                check = (CheckBox)convertView.findViewById(R.id.check);
            }
            check.setText(mGet.getMethod());
            check.setChecked(mGet.getChecked());
            
            check.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mGet.getChecked()){
						mGet.setChecked(false);
					}
					else{
						mGet.setChecked(true);
					}
					check.setChecked(mGet.getChecked());
				}
			});
            return convertView;
        }
    }
	
	private void loadGetMethod ()
    {
        LinkedList <String> raw = new LinkedList <String> ();
        try{
        	AssetManager am = this.getAssets();
        	InputStream fis = am.open("get.dat");
        	InputStreamReader isr = new InputStreamReader(fis);
        	BufferedReader br = new BufferedReader(isr);
        	String temp = br.readLine();
        	while (temp != null){
        		raw.add(temp);
        		temp = br.readLine();
        	}
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        mGets = new GetMethod [raw.size()];
        for (int k=0; k<raw.size(); k++)
        {
        	mGets[k]= new GetMethod (raw.get(k), false);
        }
    }
	
	private void setButtonEvent ()
	{
		get.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String action = "";
				for (int k=0; k<mGets.length; k++) {
					if (mGets[k].getChecked()) {
						action = mGets[k].getMethod();
						break;
					}
				}
				if (action == "") {
					Toast.makeText(GetData.this, "Error: No action selected.", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!checkFacebook()) {
					Toast.makeText(GetData.this, "Error: Please sign in first.", Toast.LENGTH_SHORT).show();
					return;
				}
				FacebookUtility.runner.request(action, new AsyncFacebookRunner.RequestListener(){
					@Override
					public void onComplete(String response, Object state) {
						final String display = processResponse(response);
						runOnUiThread(new Runnable() {
						    public void run() {
						    	tv.setText(display);
						    	Toast.makeText(GetData.this, "Request completed.", Toast.LENGTH_SHORT).show();
						    }
						});
					}
					@Override
					public void onIOException(IOException e, Object state) {
						
					}
					@Override
					public void onFileNotFoundException(FileNotFoundException e, Object state) {
						
					}
					@Override
					public void onMalformedURLException(MalformedURLException e, Object state) {
						
					}
					@Override
					public void onFacebookError(FacebookError e, Object state) {
						
					}});
			}
		});
		
		reset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int k=0; k<mGets.length; k++) {
					mGets[k].setChecked(false);
				}
				ga.notifyDataSetChanged();
				tv.setText("");
			}
		});
	}
	
	private String processResponse (String response)
	{
		String result = "";
		int counterQuote = 0;
		for (int k=0; k<response.length(); k++)
		{
			if (response.charAt(k) == '{')
			{
				if (k != 0)
				{
					result = result + "\n";
				}
				for (int n=0; n<counterQuote; n++)
				{
					result = result + "\t";
				}
				result = result + "{\n";
				counterQuote += 1;
				for (int n=0; n<counterQuote; n++)
				{
					result = result + "\t";
				}
			}
			else if (response.charAt(k) == '}')
			{
				result = result + "\n";
				counterQuote -= 1;
				for (int n=0; n<counterQuote; n++)
				{
					result = result + "\t";
				}
				result = result + "}";
				
			}
			else if (response.charAt(k) == ',')
			{
				result = result + ",\n";
				for (int n=0; n<counterQuote; n++)
				{
					result = result + "\t";
				}
			}
			else
			{
				result = result + response.charAt(k);
			}
		}
		return result;
	}
}