
package linkup.linkup.Utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import linkup.linkup.model.SingletonUser;

public class HttpClient {

	private static final String TAG = "HttpClient";
	private static String BASE_URL = "http://dev-link-up-g1.herokuapp.com";

	public String getUsers() throws  UnknownHostException {
		HttpURLConnection con = null ;
		InputStream is = null;

		try {
			con = (HttpURLConnection) ( new URL(BASE_URL + "/users")).openConnection();
			con.setRequestProperty ("token", 			SingletonUser.getToken());
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.connect();
			
			// Let's read the response
			StringBuffer buffer = new StringBuffer();
			int status = con.getResponseCode();
			is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while (  (line = br.readLine()) != null )
				buffer.append(line + "\r\n");
			
			is.close();
			con.disconnect();
			return buffer.toString();
	    }
		catch(UnknownHostException t) {

			throw t;
		}
		catch(Throwable t) {

			t.printStackTrace();
		}
		finally {
			try { is.close(); } catch(Throwable t) {}
			try { con.disconnect(); } catch(Throwable t) {}
		}

		return null;
				
	}
	public String deleteUser() throws  UnknownHostException {
		HttpURLConnection con = null ;
		InputStream is = null;

		try {
			con = (HttpURLConnection) ( new URL(BASE_URL + "/users/"+SingletonUser.getUser().Uid)).openConnection();
			con.setRequestProperty ("token", 			SingletonUser.getToken());
			con.setRequestMethod("DELETE");
			con.setDoInput(true);
			con.connect();

			// Let's read the response
			StringBuffer buffer = new StringBuffer();
			int status = con.getResponseCode();
			is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while (  (line = br.readLine()) != null )
				buffer.append(line + "\r\n");

			is.close();
			con.disconnect();
			return buffer.toString();
		}
		catch(UnknownHostException t) {

			throw t;
		}
		catch(Throwable t) {

			t.printStackTrace();
		}
		finally {
			try { is.close(); } catch(Throwable t) {}
			try { con.disconnect(); } catch(Throwable t) {}
		}

		return null;

	}
}
