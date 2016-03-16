package es.ubiqua.citaio.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import es.ubiqua.citaio.listeners.OnSessionRecordListener;
import es.ubiqua.citaio.model.UserData;
import android.os.AsyncTask;

public class RecordSessionAsyncTask extends AsyncTask<String, Float, Integer>{

	private boolean mSuccess;
	private OnSessionRecordListener mListener;
	private boolean mRecord;
	private UserData mUser;
	
	public RecordSessionAsyncTask(OnSessionRecordListener listener, UserData user, boolean record) {
		mListener = listener;
		mRecord = record;
		mUser = user;
	}
	
	@Override
	protected Integer doInBackground(String... arg0) {
		recordSession();
		return null;
	}
	
	protected void onPostExecute(Integer bytes){
		if(mSuccess){
			if(mRecord){
				mListener.onStartRecord();
			}else{
				mListener.onStopRecord();
			}
		}
	}
	
	private void recordSession(){
		
		String action = "";
		mSuccess = true;
		
		if(mRecord){ action = "start"; }else{ action = "stop"; }
		
		try{
			URL _url = new URL("https://staging.cita.io/api/v1/rooms/"+mUser.getRoom().getId()+"/"+action+"-recording?auth_token="+mUser.getUser().getAuth_token()+"&m=null");
			HttpsURLConnection urlConnection = (HttpsURLConnection) _url.openConnection();
		    urlConnection.setRequestMethod("GET");
		    urlConnection.setDoOutput(false);
		    urlConnection.connect();
		    InputStream inputStream = urlConnection.getInputStream();
		    Reader reader = new InputStreamReader(inputStream);
		    reader.close();
		    inputStream.close();
		    mSuccess = true;
		}catch (MalformedURLException e) {
			mSuccess = false;
		}catch (IOException e) {
			mSuccess = false;
		}
		
	}

}
