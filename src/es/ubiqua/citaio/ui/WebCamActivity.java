package es.ubiqua.citaio.ui;

import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.PublisherKit.PublisherListener;
import com.opentok.android.Session.SessionListener;
import com.opentok.android.SubscriberKit;
import com.opentok.android.SubscriberKit.SubscriberListener;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit.VideoListener;

import es.ubiqua.citaio.BaseApplication;
import es.ubiqua.citaio.R;
import es.ubiqua.citaio.listeners.OnSessionRecordListener;
import es.ubiqua.citaio.model.UserData;
import es.ubiqua.citaio.tasks.CloseSessionAsyncTask;
import es.ubiqua.citaio.tasks.RecordSessionAsyncTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class WebCamActivity extends Activity implements SessionListener, PublisherListener, SubscriberListener, VideoListener, OnSessionRecordListener, OnCancelListener{
	
	private UserData mUser;
	private Session mSession;
	private Subscriber mSubscriber;
	private Publisher mPublisher;
	private FrameLayout mVideo;
	private FrameLayout mVideoPublisher;
	private boolean isRecording = false;
	private String token = "";
	private String sessionid = "";
	private String apikey = "";
	private RecordSessionAsyncTask mRecordSessionAsyncTask;
	private CloseSessionAsyncTask mCloseSessionAsyncTask;
	private ProgressDialog progressDialog;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webcam_activity_2);
		
		mVideo = (FrameLayout)findViewById(R.id.video);
		mVideoPublisher = (FrameLayout)findViewById(R.id.videoPublisher);
		
		createProgressDialog();
		
		ConfigureUserData();
		ConfigureSession();
		OpenSession();
	}

	private void ConfigureUserData(){
		mUser = BaseApplication.mUser;
	}
	
	private void ConfigureSession(){
		token = mUser.getRoom().getTokbox_token();
		sessionid = mUser.getRoom().getTokbox_session_id();
		apikey = mUser.getRoom().getTokbox_api_key();
	}
	
	private void OpenSession(){
		mSession = new Session(this,apikey,sessionid);
		mSession.setSessionListener(this);
		mSession.connect(token);
	}
	
	private void PublishToStream(){
		mPublisher = new Publisher(this,"");
		mPublisher.setPublisherListener(this);
		mSession.publish(mPublisher);
	}
	
	private void SubscribeToStream(Stream stream){
		mSubscriber = new Subscriber(this, stream);
		mSubscriber.setVideoListener(this);
		mSession.subscribe(mSubscriber);
		PublishToStream();
	}
	
	/*****************************/
	/* Listeners SessionListener */
	/*****************************/
	
	@Override
	public void onConnected(Session session) {
		
	}

	@Override
	public void onDisconnected(Session arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onError(Session arg0, OpentokError arg1) {
		Toast.makeText(this, getString(R.string.cita_io_error_token), Toast.LENGTH_SHORT).show();
		progressDialog.dismiss();
		onSessionClosed();
	}

	/*******************************/
	/* Listeners PublisherListener */
	/*******************************/
	
	@Override
	public void onStreamDropped(Session arg0, Stream arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStreamReceived(Session session, Stream stream) {
		SubscribeToStream(stream);
	}

	@Override
	public void onError(PublisherKit arg0, OpentokError arg1) {
		Toast.makeText(this, getString(R.string.cita_io_error_connection_publisher), Toast.LENGTH_SHORT).show();
		progressDialog.dismiss();
		onSessionClosed();
	}

	@Override
	public void onStreamCreated(PublisherKit arg0, Stream arg1) {
		mVideoPublisher.addView(mPublisher.getView());
	}

	@Override
	public void onStreamDestroyed(PublisherKit arg0, Stream arg1) {
		// TODO Auto-generated method stub
	}
	
	/********************************/
	/* Listeners SubscriberListener */
	/********************************/
	
	@Override
	public void onConnected(SubscriberKit kit) {
		
	}

	@Override
	public void onDisconnected(SubscriberKit kit) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onError(SubscriberKit arg0, OpentokError arg1) {
		Toast.makeText(this, getString(R.string.cita_io_error_connection_subscriber), Toast.LENGTH_SHORT).show();
		progressDialog.dismiss();
		onSessionClosed();
	}

	/***************************/
	/* Listeners VideoListener */
	/***************************/
	
	@Override
	public void onVideoDataReceived(SubscriberKit kit) {
		//RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);

		mVideo.addView(mSubscriber.getView(), layoutParams);
		progressDialog.dismiss();
	}

	@Override
	public void onVideoDisableWarning(SubscriberKit arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onVideoDisableWarningLifted(SubscriberKit arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onVideoDisabled(SubscriberKit arg0, String arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onVideoEnabled(SubscriberKit arg0, String arg1) {
		// TODO Auto-generated method stub
	}
	

	@Override
	public void onStartRecord() {
		((Button)this.findViewById(R.id.btnRecord)).setText(R.string.cita_io_stop_record);
	}

	@Override
	public void onStopRecord() {
		((Button)this.findViewById(R.id.btnRecord)).setText(R.string.cita_io_start_record);
	}
	
	@Override
	public void onSessionClosed(){
		mPublisher.destroy();
		mSubscriber.destroy();
		mSession.disconnect();
		backToMain();
	}
	
	public void recordSession(View v){
		if(isRecording){
			mRecordSessionAsyncTask = new RecordSessionAsyncTask(this, mUser, false);
			mRecordSessionAsyncTask.execute("");
			isRecording = false;
		}else{
			mRecordSessionAsyncTask = new RecordSessionAsyncTask(this, mUser, true);
			mRecordSessionAsyncTask.execute("");
			isRecording = true;
		}
	}
	
	public void closeSession(View v){
		mCloseSessionAsyncTask = new CloseSessionAsyncTask(this, mUser);
		mCloseSessionAsyncTask.execute("");
	}
	
	private void createProgressDialog(){
		progressDialog = ProgressDialog.show(this,getString(R.string.cita_io_loading_session_caption),getString(R.string.cita_io_loading_session_video),true);
		progressDialog.setOnCancelListener(this);
		progressDialog.setCanceledOnTouchOutside(true);
	}
	
	private void backToMain(){
		Intent i = null;
		i = new Intent(getApplicationContext(),SplashActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
		this.finish();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		backToMain();
	}
}
