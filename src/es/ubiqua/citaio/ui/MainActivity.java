package es.ubiqua.citaio.ui;

import es.ubiqua.citaio.BaseApplication;
import es.ubiqua.citaio.R;
import es.ubiqua.citaio.listeners.OnTokenReceivedListener;
import es.ubiqua.citaio.listeners.OnUserReceivedListener;
import es.ubiqua.citaio.model.UserData;
import es.ubiqua.citaio.tasks.GetUserAsyncTask;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity implements DialogInterface.OnClickListener, OnTokenReceivedListener, OnUserReceivedListener{

	private WebView webview;
	private WebChromeClient chrome_client;
	private AlertDialog dialog;
	private CookieManager cookieManager;
	private ProgressDialog progressDialog;
	private boolean mFromGCM = false;
	private String mUrl = null;
	private String mToken = null;
	private String mKind = null;
	private String mWebViewContent = "";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		createProgressDialog();
		
		try{
			mUrl = getIntent().getExtras().getString(BaseApplication.CITAIO_URL);
			mToken = getIntent().getExtras().getString(BaseApplication.CITAIO_TOKEN);
			mKind = getIntent().getExtras().getString(BaseApplication.CITAIO_KIND);
			
			if(mUrl!=null & mToken!=null){
				mFromGCM = true;
			}else{
				mFromGCM = false;
			}
			
		}catch(Exception e){
			mFromGCM = false;
		}
		
		if(mFromGCM && !mToken.equals("0")){
			onTokenReceived(mToken);
		}else{
			initialize();
		}
	}

	/* Listeners de la Activity */
	
	public void onClick(DialogInterface dialog, int which) {
		if(which==-1){ finish(); }
	}

	public void onTokenReceived(String token) {
		GetUserAsyncTask userAsyncTask = new GetUserAsyncTask(this);
		final String url = getResources().getString(R.string.cita_io_url_debug) +getResources().getString(R.string.cita_io_api_path)+token;
		userAsyncTask.execute(url);
	}

	public void onUserReceived(UserData userData) {
		if(userData!=null){
			BaseApplication.mUser = userData;
			Intent intent = new Intent(this,WebCamActivity.class);
			startActivity(intent);
		}else{
			Toast.makeText(this, getString(R.string.cita_io_error_token), Toast.LENGTH_SHORT).show();
			initialize();
		}
		
		progressDialog.dismiss();
	}
	
	@Override
	public void onBackPressed() {
		createExitDialog();
		dialog.show();
	}
	
	/* Métodos privados */
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initialize(){
		
		/* Objects instantiation */
		chrome_client = new WebChromeClient();
		
		webview = (WebView)findViewById(R.id.webview);
		
		/* Settings configuration */
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSaveFormData(true);
	
		/* WebView clients configuration */
		webview.setWebChromeClient(chrome_client);
		webview.setWebViewClient(new WebViewClient() {
			
			@Override
		    public void onPageFinished(WebView view, String url) {
		        super.onPageFinished(view, url);
		        Log.d("es.ubiqua.citaio","URL recibida: "+url);
		        progressDialog.dismiss();
			}
			
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url){
				 //if( url.startsWith("http:") || url.startsWith("https:") ) {
				 //      return false;
				 //}
				 if(url.contains("citaio")){
				    	onTokenReceived(extractToken(url));
						return true;
				 }
				 return false;
				 /*
				    // Otherwise allow the OS to handle it
				    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				    startActivity( intent );
				    return true;
				    
				if(url.contains("citaio")){
					onTokenReceived(extractToken(url));
					return true;
				}
				return false;
				*/
			}
			
		});
		
		/* Load Cookies */
		enableCookies();
		
		/* Load URL */
		try{
			if(mToken.equals("0") && mFromGCM){
				webview.loadUrl(mUrl);
			}else{
				webview.loadUrl(getResources().getString(R.string.cita_io_url_debug));
			}
		}catch(Exception e){
			webview.loadUrl(getResources().getString(R.string.cita_io_url_debug));
		}
	}
	
	private void createExitDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);	
		builder.setTitle(getResources().getString(R.string.exit_dialog_title));
		builder.setMessage(getResources().getString(R.string.exit_dialog_subtitle));
		builder.setPositiveButton(getResources().getString(R.string.exit_dialog_option_yes), this);
		builder.setNegativeButton(getResources().getString(R.string.exit_dialog_option_no), this);
		dialog = builder.create();
	}

	private String extractToken(String url){
		String token = "";
		token = url.substring(url.indexOf("=")+1);
		return token;
	}
	
	private void enableCookies(){
		SharedPreferences sp = getSharedPreferences(SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String regid = sp.getString(SplashActivity.PROPERTY_REG_ID, "null");
		cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.setCookie(getString(R.string.cita_io_url_debug), "x-citaio-device-id="+regid);
		Log.i(BaseApplication.TAG,"Registered with: "+cookieManager.getCookie(getString(R.string.cita_io_url_debug)));
	}
	
	private void createProgressDialog(){
		progressDialog = ProgressDialog.show(this,getString(R.string.cita_io_loading_session_caption),getString(R.string.cita_io_loading_session),true);

	}
	
}
