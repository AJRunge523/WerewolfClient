package edu.wm.werewolfclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterUserActivity extends Activity {

	private static String TAG = "USER_REGISTRATION_ACTIVITY";
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
	
	ProgressBar requestProgress;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_register_user);
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
        
        requestProgress = (ProgressBar) findViewById(R.id.playerWerewolfBar);
        
        userNameText.setText(this.getIntent().getStringExtra("username"));
    }
    
    public void asyncTaskTest(View view)
    {
    	//Start by ensuring there is a network connection
    	firstNameText.setError(null);
    	lastNameText.setError(null);
    	userNameText.setError(null);
    	passwordText.setError(null);
    	verifyText.setError(null);
    	ConnectivityManager connMgr = (ConnectivityManager) 
    			getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	
    	
    	if(networkInfo != null && networkInfo.isConnected())
    	{
	    	firstName = firstNameText.getText().toString();
	        lastName = lastNameText.getText().toString();
	        userName = userNameText.getText().toString();
	        password = passwordText.getText().toString();
	        verify = verifyText.getText().toString();
	        //Make sure all fields are filled in
	        View focusView = null;
	        if(!verify.equals(password))
	        {
	        	verifyText.setError(getString(R.string.password_mismatch));
	        	verifyText.requestFocus();
	        	return;
	        }
	        if(password.length() == 0)
	        {
	        	passwordText.setError(getString(R.string.required_field));
	        	focusView = passwordText;
	        }
	        if(userName.length() == 0)
	        {
	        	userNameText.setError(getString(R.string.required_field));
	        	focusView = userNameText;
	        }
	        if(lastName.length() == 0)
	        {
	        	lastNameText.setError(getString(R.string.required_field));
	        	focusView = lastNameText;
	        }
	        if(firstName.length() == 0)
	        {
	        	firstNameText.setError(getString(R.string.required_field));
	        	focusView = firstNameText;
	        }
	        
	        if(focusView == null)
	        {
	        //If no problems with the form data, start the Async Task
	        	requestProgress.setVisibility(View.VISIBLE);
	        	AsyncPostTask task = new AsyncPostTask(this);
	        	task.execute();
	        	Log.i(TAG, "Launched AsyncTask");
	        }
	        else
	        	focusView.requestFocus();
    	}
    	else
    	{
    		Toast toast = Toast.makeText(getApplicationContext(), "No network connection!", Toast.LENGTH_SHORT);
	    	toast.show();
    	}
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
    
    
private class AsyncPostTask extends AsyncTask<String, String, String> {
		
		Activity currentActivity;
		
		public AsyncPostTask(Activity a)
		{
			currentActivity = a;
		}
	
		@Override
		protected String doInBackground(String... params) {
	       
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://secret-wildwood-3803.herokuapp.com/newAccount"); 
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);

			String result;
			//Load up a map that we'll convert to a Json object.
			HashMap<String, String> postPars = new HashMap<String, String>();
			postPars.put("firstName", firstName);
			postPars.put("lastName", lastName);
			postPars.put("userName", userName);
			postPars.put("password", password);
			postPars.put("imageURL", "nope");
			postPars.put("role", "ROLE_USER");	
			
			//Convert to a Json object.
			JSONObject obj = new JSONObject(postPars);
			Log.i(TAG, obj.toString());
			
			try {
				//Make the post
				StringEntity se = new StringEntity(obj.toString());
				httpPost.setEntity(se);
				httpPost.setHeader("Content-Type", "application/json");
				HttpResponse response = httpclient.execute(httpPost);
				
				//Check the response
				if(response!=null)
				{	
					Log.i(TAG, "" + response.getStatusLine().getStatusCode());
					if(response.getStatusLine().getStatusCode() == 200)
					{
						InputStream inputStream = response.getEntity().getContent();
					    // json is UTF-8 by default
					    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
					    StringBuilder sb = new StringBuilder();

					    String line = null;
					    while ((line = reader.readLine()) != null)
					    {
					        sb.append(line);
					    }
					    result = sb.toString();
						//String temp = EntityUtils.toString(response.getEntity());
						//Log.i(TAG, temp);
					    inputStream.close();
						return result;
					}
					else
					{
						return "HTTP Error: " + response.getStatusLine().getStatusCode();
					}
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
			
	        Log.i("MainActivity", "Boo!");
		}
		  @Override
		  protected void onPostExecute(String result) {
		   // execution of result of Long time consuming operation
		    	resultText.setText(result);
		    	resultText.setVisibility(View.VISIBLE);
		    	
		    	//Parse the Json Object
		    	try {
					JSONObject obj = new JSONObject(result);
					String status = obj.getString("status");
					if(!status.equals("success"))
					{
						String data = obj.getString("data");
						Log.v(TAG, data);
						String[] data_split = data.split(" ");
						if(data_split[1].equals("username:"))
						{
							userNameText.setError("Username already in use");
							userNameText.requestFocus();
						}
					}
					else
					{
						Toast toast = Toast.makeText(getApplicationContext(), "Successfully created account!", Toast.LENGTH_SHORT);
						toast.show();
						currentActivity.finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		    	
		    requestProgress.setVisibility(View.GONE);
		    	
		  }

		  @Override
		  protected void onProgressUpdate(String... text) {
		    	Toast toast = Toast.makeText(getApplicationContext(), "Running an asynchronous task.", Toast.LENGTH_SHORT);
		    	toast.show();
		   // Things to be done while execution of long running operation is in
		   // progress. For example updating ProgessDialog
		  }
	}
    
    
}
