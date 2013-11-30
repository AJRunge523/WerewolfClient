package edu.wm.werewolfclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	
	private static final String TAG = "LOGIN_ACTIVITY";
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mUserName;
	private String mPassword;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	
	private ProgressBar loginProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mUsernameView = (EditText) findViewById(R.id.login_username);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		loginProgressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
		SharedPreferences settings = getSharedPreferences("Stored_Data", 0);
		mUsernameView.setText(settings.getString("user", ""));
		mPasswordView.setText(settings.getString("pw", ""));
		attemptLogin();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	public void startRegister(View view) {
		Intent intent = new Intent(this, RegisterUserActivity.class);
		intent.putExtra("username", mUsernameView.getText().toString());
		startActivity(intent);
	}
	

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}
		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUserName = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		if(mUserName.equals("") && mPassword.equals(""))
			return;

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} /*else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}
*/
		// Check for a valid email address.
		if (TextUtils.isEmpty(mUserName)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		} 

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			loginProgressBar.setVisibility(View.VISIBLE);
			mAuthTask = new UserLoginTask(this);
			mAuthTask.execute();
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	private class UserLoginTask extends AsyncTask<String, String, String> {
		
		Activity currentActivity;
		
		public UserLoginTask(Activity a)
		{
			currentActivity = a;
		}
		
		@Override
		protected String doInBackground(String... params) {
	       
			DefaultHttpClient httpclient = new DefaultHttpClient();			
			String result;
			
			try {
				URI uri = new URI("http://secret-wildwood-3803.herokuapp.com/auth/verify");
				httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
				HttpGet httpGet = new HttpGet(uri); 
				//Authentication header
				httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(mUserName, mPassword), "UTF-8", false));
				
				//Make the request
				HttpResponse response = httpclient.execute(httpGet);
				
				//Check the response
				if(response!=null)
				{	
					Log.i(TAG, "" + response.getStatusLine().getStatusCode());
					int code = response.getStatusLine().getStatusCode();
					if(code == 200)
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
					    inputStream.close();
						return result;
					}
					else if(code == 401)
					{
						Log.v(TAG, "HTTP Error: " + response.getStatusLine().getStatusCode());
						return "invalid";
					}
					else
						return "unknown";
				}
				else
				{
					return "failure";
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "failure";
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return "failure";
			} catch (IOException e) {
				e.printStackTrace();
				return "failure";
			}
			catch (Exception e) {
				return "General failure, exception type: " + e.getClass().getName();
			}
		 }
		  @Override
		  protected void onPostExecute(String result) {
		   // execution of result of Long time consuming operation

		    	if(result.equals("failure"))
		    	{
			    	Toast toast = Toast.makeText(getApplicationContext(), 
			    			"Error attempting to contact web service. Please try again.", Toast.LENGTH_LONG);
			    	toast.show();
		    	}
		    	else if(result.equals("invalid"))
		    	{
		    		loginProgressBar.setVisibility(View.GONE);
		    		mUsernameView.setError("Invalid username and/or password");
		    	}
		    	else{
		    	//Parse the Json Object
			    	try {
			    		Log.v(TAG, result);
						JSONObject obj = new JSONObject(result);
						Log.v(TAG, "helping");
						String status = obj.getString("status");
						Log.v(TAG, status);
						if(!status.equals("success"))
						{
							Log.e(TAG, "HUGE ERROR");
						}
						else
						{
							Log.v(TAG, "help");
							String data = obj.getString("data");
							JSONObject dataObj = new JSONObject(data);
							SharedPreferences prefs = getSharedPreferences("Stored_Data", 0);
							SharedPreferences.Editor editor = prefs.edit();
							editor.putString("user", mUserName);
							editor.putString("pw", mPassword);
							editor.commit();
							Intent intent = new Intent(currentActivity, UserActivity.class);
							intent.putExtra("ROLE", dataObj.getString("ROLE"));
							intent.putExtra("TYPE", dataObj.getString("TYPE"));
							intent.putExtra("username", mUserName);
							intent.putExtra("password", mPassword);
							startActivity(intent);
							currentActivity.finish();
							
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}		 
		    	}
		    loginProgressBar.setVisibility(View.GONE);
		    mAuthTask = null;
		    	
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
