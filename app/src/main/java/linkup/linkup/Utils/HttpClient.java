
package linkup.linkup.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class HttpClient {

	private static String BASE_URL = "https://dev-link-up-g1.herokuapp.com/users";

	public String getUsers() throws  UnknownHostException {
		HttpURLConnection con = null ;
		InputStream is = null;

		try {
			con = (HttpURLConnection) ( new URL(BASE_URL + "")).openConnection();

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
	
}
