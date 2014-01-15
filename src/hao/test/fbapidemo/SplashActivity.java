package hao.test.fbapidemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;


public class SplashActivity extends Activity {

	protected int splashTime = 2000;
    private Thread splashTread;
	private InternetAccess ia;
	private AlertDialog ad;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // set orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // set layout
        setContentView(R.layout.splash);

        // initialize InternetAccess object
        ia = new InternetAccess(this);
        
        // thread to sleep some time
        splashTread = new Thread() 
        {
            @Override
            public void run(){
                try{
                	sleep(splashTime);
                }
                catch(InterruptedException e){
                	e.printStackTrace();
                }
                finally{
                    if (ia.IsConnected){
                    	Intent intent = new Intent(SplashActivity.this, CenterActivity.class);
                    	startActivity(intent);
                    	SplashActivity.this.finish();
                    }
                    else
                    {
                    	runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            	ad = new AlertDialog.Builder(SplashActivity.this).create();
                            	ad.setTitle("Internet Error");
                            	ad.setMessage("There is no avaliable Internet connection. Please connect to the Internet first.");
                            	ad.setButton("OK", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										ad.dismiss();
										SplashActivity.this.finish();
									}
								});
                            	ad.show();
                            }
                        });
                    }
                }
            }
        };

        splashTread.start();
    }
    
    private class InternetAccess
    {
    	boolean IsConnected = false;
    	
    	InternetAccess(Context ctx)
    	{
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo [] ni = cm.getAllNetworkInfo();
            for (NetworkInfo this_ni : ni)
            {
            	if (this_ni.getState() == NetworkInfo.State.CONNECTED)
            	{
            		IsConnected = true;
            	}
            }
        }
    }
}

    