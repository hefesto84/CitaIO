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

public class CloseSessionAsyncTask extends AsyncTask<String, Float, Integer>{

	private boolean mSuccess;
	private OnSessionRecordListener mListener;
	private UserData mUser;
	
	public CloseSessionAsyncTask(OnSessionRecordListener listener, UserData user) {
		mListener = listener;
		mUser = user;
	}
	
	@Override
	protected Integer doInBackground(String... arg0) {
		closeSession();
		return null;
	}
	
	protected void onPostExecute(Integer bytes){
		if(mSuccess){
			mListener.onSessionClosed();
		}
	}
	
	private void closeSession(){
		
		mSuccess = true;
		
		try{
			URL _url = new URL("https://staging.cita.io/rooms/"+mUser.getRoom().getPermalink()+"/close_stream");
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
