package es.ubiqua.citaio.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;

import es.ubiqua.citaio.listeners.OnUserReceivedListener;
import es.ubiqua.citaio.model.UserData;
import android.os.AsyncTask;

public class GetUserAsyncTask extends AsyncTask<String, Float, Integer>{

	private UserData m_User;
	private boolean m_Success;
	private OnUserReceivedListener m_Listener;
	
	public GetUserAsyncTask(OnUserReceivedListener listener) {
		m_Listener = listener;
	}
	
	@Override
	protected Integer doInBackground(String... arg0) {
		getUser(arg0[0]);
		return null;
	}
	
	protected void onPostExecute(Integer bytes){
		if(m_Success){
			m_Listener.onUserReceived(m_User);
		}else{
			m_Listener.onUserReceived(null);
		}
	}
	
	private void getUser(String url){
		try{
			URL _url = new URL(url);
			HttpsURLConnection urlConnection = (HttpsURLConnection) _url.openConnection();
		    urlConnection.setRequestMethod("GET");
		    urlConnection.setDoOutput(false);
		    urlConnection.connect();
		    InputStream inputStream = urlConnection.getInputStream();
		    Reader reader = new InputStreamReader(inputStream);
		    Gson gson = new Gson();
		    m_User = gson.fromJson(reader, UserData.class);
		    reader.close();
		    inputStream.close();
		    m_Success = true;
		}catch (MalformedURLException e) {
			m_Success = false;
		}catch (IOException e) {
			m_Success = false;
		}
	}

}
