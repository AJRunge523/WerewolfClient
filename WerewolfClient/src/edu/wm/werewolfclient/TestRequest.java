package edu.wm.werewolfclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class TestRequest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_request);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_request, menu);
		return true;
	}
	
	public void sendRequest(View view)
	{
                      //ALERT MESSAGE
                      Toast.makeText(getBaseContext(),"Please wait, connecting to server.",Toast.LENGTH_LONG).show();

             try{

                  // URLEncode user defined data

                    String loginValue    = URLEncoder.encode("andrew", "UTF-8");
                    String fnameValue  = URLEncoder.encode("andrew", "UTF-8");
                    String emailValue   = URLEncoder.encode("ajrunge@email.wm.edu", "UTF-8");
                    String passValue    = URLEncoder.encode("butts", "UTF-8");

                 // Create http client object to send request to server

                    HttpClient Client = new DefaultHttpClient();
                 
                 // Create URL string

                  String URL = "http://androidexample.com/media/webservice/httpget.php?user="+loginValue+"&name="+fnameValue+"&email="+emailValue+"&pass="+passValue;

                 Log.i("httpget", URL);

                try
                 {
                               String SetServerString = "";
                     
                             // Create Request to server and get response
                     
                               HttpGet httpget = new HttpGet(URL);
                              ResponseHandler<String> responseHandler = new BasicResponseHandler();
                              SetServerString = Client.execute(httpget, responseHandler);
                    
                               // Show response on activity 
                              Log.i("TestRequest", SetServerString);
                  }
                catch(Exception ex)
                   {
                          //content.setText("Fail!");
                	Log.i("TestRequest", "Unknown Exception");
                		Log.i("TestRequest", "Woops");
                		Log.i("TestRequest", ex.getClass().getName());
                		
                	
                    }
             }
           catch(UnsupportedEncodingException ex)
            {
                   // content.setText("Fail");
        	   Log.i("TestRequest", "Woops");
        	   Log.i("TestRequest", "BadEncoding");
             }  
         }
}
