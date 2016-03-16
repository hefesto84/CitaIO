package es.ubiqua.citaio.ui;

import es.ubiqua.citaio.BaseApplication;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GcmActivity extends Activity{
	
	private boolean mGcm = false;
	private String mUrl = "";
	private String mKind = "";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mGcm = getIntent().getExtras().getBoolean(BaseApplication.CITAIO_GCM);
		mUrl = getIntent().getExtras().getString(BaseApplication.CITAIO_URL);
		mKind = getIntent().getExtras().getString(BaseApplication.CITAIO_KIND);
		
		Intent intent = new Intent(this,SplashActivity.class);
		intent.putExtra(BaseApplication.CITAIO_KIND, mKind);
		intent.putExtra(BaseApplication.CITAIO_URL, mUrl);
		intent.putExtra(BaseApplication.CITAIO_GCM, mGcm);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
	
}
