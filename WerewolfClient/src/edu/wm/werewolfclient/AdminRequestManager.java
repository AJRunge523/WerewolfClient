	package edu.wm.werewolfclient;

	import java.io.BufferedReader;
	import java.io.InputStream;
	import java.io.InputStreamReader;
	import java.net.URI;
	import java.util.HashMap;
	import java.util.Map;

	import org.apache.http.HttpResponse;
	import org.apache.http.auth.UsernamePasswordCredentials;
	import org.apache.http.client.methods.HttpGet;
	import org.apache.http.client.methods.HttpPost;
	import org.apache.http.entity.StringEntity;
	import org.apache.http.impl.auth.BasicScheme;
	import org.apache.http.impl.client.DefaultHttpClient;
	import org.apache.http.params.CoreConnectionPNames;
	import org.json.JSONException;
	import org.json.JSONObject;

	import android.app.Activity;
	import android.os.AsyncTask;
	import android.util.Base64;
	import android.util.Log;
	import android.widget.Toast;
	
public class AdminRequestManager {

		public static final String TAG = "ADMIN_REQUEST_MANAGER";
		
		WebRequest GDRequest = null;
		WebRequest URRequest = null;
		WebRequest PKRequest = null;
		WebRequest RGRequest = null;
		
		
		public AdminRequestManager() { }
		
		public void makeWebRequest(AdminActivity activity, String URI, String username,
				String password, Map<String, String> payload, int type)
		{
			Log.v(TAG, ""+type);
			switch(type)
			{
			case 0: //Game Data Request
				if(GDRequest == null)
				{
					Log.v(TAG, "Beginning a Game Data Request");
					GDRequest = new WebRequest(activity, URI, username, password, payload, type);
					GDRequest.execute();
				}
				break;
			case 1: //Vote List Request
				if(URRequest == null)
				{
					Log.v(TAG, "Beginning a User Removal Request");
					URRequest = new WebRequest(activity, URI, username, password, payload, type);
					URRequest.execute();
				}
				break;
			case 2: //Kill List Request
				if(PKRequest == null)
				{
					Log.v(TAG, "Beginning a Player Kill Request");
					PKRequest = new WebRequest(activity, URI, username, password, payload, type);
					PKRequest.execute();
				}
				break;
			case 3:
				if(RGRequest == null)
				{
					Log.v(TAG, "Beginning a Restart Game Request");
					RGRequest = new WebRequest(activity, URI, username, password, payload, type);
					RGRequest.execute();
				}
			default:
				break;
			}
		}
		
		private class WebRequest extends AsyncTask<String, String, String>
		{
			AdminActivity activity;
			String URI;
			String username;
			String password;
			HashMap<String, String> payload;
			int type;
			
			public WebRequest(AdminActivity activity, String URI, String username, String password, Map<String, String> payload, int type) {
				this.activity = activity;
				this.URI = URI;
				this.username = username;
				this.password = password;
				if(payload==null)
					this.payload = null;
				else
					this.payload = (HashMap<String, String>) payload;
				this.type = type;
			}
			
			protected String doInBackground(String... params) {
			       
				DefaultHttpClient httpclient = new DefaultHttpClient();		
				HttpResponse response;
				String result;
				
				try {
					Log.v(TAG, "Got into the async task");
					URI uri = new URI(URI);
					httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
					if(payload == null)
					{
						HttpGet httpGet = new HttpGet(uri); 
						//Authentication header
						httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username, password), "UTF-8", false));
					
						//Make the request
						response = httpclient.execute(httpGet);
					}
					else
					{
						HttpPost httpPost = new HttpPost(uri);
						JSONObject obj;
						if(type==3)
						{
							HashMap<String, Integer> newPayload = new HashMap<String, Integer>();
							newPayload.put("time", Integer.valueOf(payload.get("time")));
							Log.v(TAG, "" + Integer.valueOf(payload.get("time")));
							obj = new JSONObject(newPayload);
						}
						else
							obj = new JSONObject(payload);
						Log.i(TAG, obj.toString());
						//Make the post
						StringEntity se = new StringEntity(obj.toString());
						httpPost.setEntity(se);
						httpPost.setHeader( "Content-Type", "application/json");
						//Have to use the NO_WRAP mode because otherwise dumb shit happens
						String authorizationString = "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP); 
						httpPost.setHeader("Authorization", authorizationString);
						response = httpclient.execute(httpPost);
					}
					
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
				}
				catch (Exception e) {
					return "General failure, exception type: " + e.getClass().getName();
				}
			 }
			 @Override
			 protected void onPostExecute(String result) {
				 // execution of result of Long time consuming operation
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
						Toast toast;
						switch(type)
						{
						case 0:
							HashMap<String, String> data = new HashMap<String, String>();
							JSONObject dataObj = new JSONObject(obj.getString("data"));
							data.put("time", dataObj.getString("time"));
							data.put("left", dataObj.getString("left"));
							data.put("alive", dataObj.getString("alive"));
							data.put("created", dataObj.getString("created"));
							data.put("users", dataObj.getString("users"));
							data.put("players", dataObj.getString("players"));
							activity.onGDUpdate(data);
							break;		
						case 1:
							toast = Toast.makeText(activity, "Removed the Users!", Toast.LENGTH_SHORT);
		    			    toast.show();
		    			    activity.onRestartGame(status);
							break;
						case 2:
							toast = Toast.makeText(activity, "Killed the Players!", Toast.LENGTH_SHORT);
		    			    toast.show();
		    			    activity.onRestartGame(status);
							break;
						case 3:
		    				toast = Toast.makeText(activity, "Restarted the Game!", Toast.LENGTH_SHORT);
		    			    toast.show();
		    			    activity.onRestartGame(status);
							break;
						}
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}		 
	    	
				 //Set appropriate task to null here
				 switch(type)
				 {
				 case 0:
					 GDRequest = null;
					 break;
				 case 1:
					 URRequest = null;
					 break;
				 case 2:
					 PKRequest = null;
					 break;
				 case 3:
					 RGRequest = null;
					 break;

				 default: break;
				 }
			  }
		}
	}

