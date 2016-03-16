package es.ubiqua.citaio;

import es.ubiqua.citaio.model.UserData;
import android.app.Application;

public class BaseApplication extends Application{
	
	public static UserData mUser;
	public static final String TAG = "es.ubiqua.citaio";
	public static final String CITAIO_URL = "url";
	public static final String CITAIO_TOKEN = "q";
	public static final String CITAIO_KIND = "kind";
	public static final String CITAIO_GCM = "gcm";
	
	public static String mFromPattern = "";
	
	public void OnCreate(){
		super.onCreate();
	}
}
