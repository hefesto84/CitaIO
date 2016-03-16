package es.ubiqua.citaio.ui;

import java.io.IOException;
import java.net.URI;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import es.ubiqua.citaio.BaseApplication;
import es.ubiqua.citaio.R;
import es.ubiqua.citaio.listeners.OnGCMRegistrationListener;
import es.ubiqua.citaio.listeners.OnSplashFinishedListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashActivity extends Activity implements OnSplashFinishedListener, OnGCMRegistrationListener{

	private final int SPLASH_TIME = 2000;
	private Context context;
	private GoogleCloudMessaging gcm;
	private String regid;
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private String mToken = "";
	private String mUrl = "";
	private String mKind = "";
	private boolean mFromGCM = false;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getApplicationContext();
		
		setContentView(R.layout.splash_activity);
		
		mFromGCM = weCameFromGCMorMail(getIntent().getExtras());
		
		if(mFromGCM){
			Log.d(BaseApplication.TAG,"Venimos de GCM");
		}else{
			Log.d(BaseApplication.TAG,"No venimos de GCM");
		}
		
		initialize();
	}


	private boolean weCameFromGCMorMail(Bundle bundle){	
		
		try{
			if(bundle!=null){
				if(bundle.containsKey("gcm")){
					mUrl = bundle.getString(BaseApplication.CITAIO_URL);
					mKind = bundle.getString(BaseApplication.CITAIO_KIND);
					mToken = "0";
				}else{
					Uri r = getIntent().getData();
					mUrl = r.getQueryParameter(BaseApplication.CITAIO_URL);
					mToken = r.getQueryParameter(BaseApplication.CITAIO_TOKEN);
					mKind = "0";
				}
				return true;
			}
		}catch(Exception e){
			return false;
		}
		return false;
	}
	
	private void initialize(){
		gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(this.getApplicationContext());
        registerInBackground();
	}

	public void onSplashFinished() {
		Intent i = null;
		i = new Intent(this,MainActivity.class);
		if(mFromGCM){
			i.putExtra(BaseApplication.CITAIO_KIND, mKind);
			i.putExtra(BaseApplication.CITAIO_URL, mUrl);
			i.putExtra(BaseApplication.CITAIO_TOKEN, mToken);
		}
		startActivity(i);
		finish();
	}
	
	private void SimulateSplash(){
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				onSplashFinished();
			}
			
		}, SPLASH_TIME);
	}
	
	private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.equals("")) {
            Log.i(BaseApplication.TAG, "Registration not found.");
            return "";
        }
        
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(BaseApplication.TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

	private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

	private SharedPreferences getGcmPreferences(Context context) {
        return getSharedPreferences(SplashActivity.class.getSimpleName(),Context.MODE_PRIVATE);
    }

	private void registerInBackground() {
		
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(getString(R.string.cita_io_project_number));
                    msg = "Device registered, registration ID=" + regid;
                    
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            	if(msg.contains("Error")){
            		onRegistered(false);
            	}else{
            		onRegistered(true);
            	}
            }
        }.execute(null, null, null);
    }
		
	private void storeRegistrationId(Context context, String regId) {
		Log.d(BaseApplication.TAG, "Registrando: "+regId);
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(BaseApplication.TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

	@Override
	public void onRegistered(boolean result) {
		if(result){
			storeRegistrationId(context, regid);
		}else{
			storeRegistrationId(context, "null");
		}
		
		SimulateSplash();
	}

}
