package edu.wm.werewolfclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	EditText firstNameText;
	EditText lastNameText;
	EditText userNameText;
	EditText passwordText;
	EditText verifyText;
	
	String firstName;
	String lastName;
	String userName;
	String password;
	String verify;
	
	TextView resultText;
	
	Button submitButton;

	private class AsyncPostTask extends AsyncTask<String, String, String> {
		
		@Override
		protected String doInBackground(String... params) {
	       
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://secret-wildwood-3803.herokuapp.com/newAccount"); 
			HashMap<String, String> postPars = new HashMap<String, String>();
			postPars.put("firstName", "andrew");
			postPars.put("lastName", "johnson");
			postPars.put("userName", "ImpeachThis");
			postPars.put("password", "d4dem");
			postPars.put("imageURL", "nope");
			postPars.put("role", "ROLE_USER");		
			JSONObject obj = new JSONObject(postPars);
			Log.i("tag", obj.toString());
			try {
				StringEntity se = new StringEntity(obj.toString());
				httpPost.setEntity(se);
				httpPost.setHeader("Content-Type", "application/json");
				HttpResponse response = httpclient.execute(httpPost);
				if(response!=null)
				{
					String temp = EntityUtils.toString(response.getEntity());
			        Log.i("tag", temp);
			        return temp;
				}
				else
				{
					return "error - null";
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "failure - unsupported encoding";
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return "failure - client error";
			} catch (IOException e) {
				e.printStackTrace();
				return "failure - io";
			}
			catch (Exception e) {
				return "General failure, exception type: " + e.getClass().getName();
			}
		 }
		
		@Override
		protected void onPreExecute()
		{
	    	firstName = firstNameText.getText().toString();
	        lastName = lastNameText.getText().toString();
	        userName = userNameText.getText().toString();
	        password = passwordText.getText().toString();
	        verify = verifyText.getText().toString();
	        
	        if(!verify.equals(password))
	        {
	        	verifyText.setBackgroundColor(Color.parseColor("#FF0000"));
	        	Toast toast = Toast.makeText(getApplicationContext(), "Passwords do not match.", Toast.LENGTH_SHORT);
	        	toast.show();
	        	return;
	        }
	        Log.i("MainActivity", "Boo!");
		}
		 /*
		   * (non-Javadoc)
		   * 
		   * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		   */
		  @Override
		  protected void onPostExecute(String result) {
		   // execution of result of Long time consuming operation
		    	Toast toast = Toast.makeText(getApplicationContext(), "Finishing an asynchronous task. Result: " + result, Toast.LENGTH_SHORT);
		    	toast.show();
		    	resultText.setText(result);
		    	
		  }

		  @Override
		  protected void onProgressUpdate(String... text) {
		    	Toast toast = Toast.makeText(getApplicationContext(), "Running an asynchronous task.", Toast.LENGTH_SHORT);
		    	toast.show();
		   // Things to be done while execution of long running operation is in
		   // progress. For example updating ProgessDialog
		  }
	}

	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState != null)
        {
        	firstName = savedInstanceState.getString("firstname");
        	lastName = savedInstanceState.getString("lastname");
        	userName = savedInstanceState.getString("username");
        	password = savedInstanceState.getString("password");
        	verify = savedInstanceState.getString("verify");
        }
        
        firstNameText = (EditText) findViewById(R.id.firstNameText);
        lastNameText = (EditText) findViewById(R.id.lastNameText);
        userNameText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        verifyText = (EditText) findViewById(R.id.verifyPasswordText);
        submitButton = (Button) findViewById(R.id.submitButton);
        
        resultText = (TextView) findViewById(R.id.resultView);
    }
    
    public void asyncTaskTest(View view)
    {
    	System.out.println("Got to this point");
    	AsyncPostTask task = new AsyncPostTask();
    	task.execute("1000");
    	Log.i("MainActivity", "Launched AsyncTask");
    }
    
    public void sendUserCreate(View view)
    {
		Intent intent = new Intent(this, UserActivity.class);
		startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
