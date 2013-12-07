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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class WebRequestManager {
	
	public static final String TAG = "WEB_REQUEST_MANAGER";
	
	WebRequest GDRequest = null;
	WebRequest VLRequest = null;
	WebRequest KLRequest = null;
	WebRequest VRequest = null;
	WebRequest KRequest = null;
	WebRequest LRequest = null;
	WebRequest AKRequest = null;
	
	
	public WebRequestManager() { }
	
	public void makeWebRequest(UserActivity activity, String URI, String username,
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
			if(VLRequest == null)
			{
				Log.v(TAG, "Beginning a Voting List Request");
				VLRequest = new WebRequest(activity, URI, username, password, payload, type);
				VLRequest.execute();
			}
			break;
		case 2: //Kill List Request
			if(KLRequest == null)
			{
				Log.v(TAG, "Beginning a Kill List Request");
				KLRequest = new WebRequest(activity, URI, username, password, payload, type);
				KLRequest.execute();
			}
			break;
		case 3: //Vote Request
			if(VRequest == null)
			{
				Log.v(TAG, "Beginning a Voting Request");
				VRequest = new WebRequest(activity, URI, username, password, payload, type);
				VRequest.execute();
			}
			break;
		case 4: //Kill Request
			if(KRequest == null)
			{
				Log.v(TAG, "Beginning a Kill Request");
				KRequest = new WebRequest(activity, URI, username, password, payload, type);
				KRequest.execute();
			}
			break;
		case 5:
			if(LRequest == null)
			{
				Log.v(TAG, "Beginning a Location Update Request");
				LRequest = new WebRequest(activity, URI, username, password, payload, type);
				LRequest.execute();
			}
		case 6:
			if(AKRequest == null)
			{
				Log.v(TAG, "Beginning an All Kill Request");
				AKRequest = new WebRequest(activity, URI, username, password, payload, type);
				AKRequest.execute();
			}
		default:
			break;
		}
	}
	
	private class WebRequest extends AsyncTask<String, String, String>
	{
		UserActivity activity;
		String URI;
		String username;
		String password;
		HashMap<String, String> payload;
		int type;
		
		public WebRequest(UserActivity activity, String URI, String username, String password, Map<String, String> payload, int type) {
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
					if(type==6)
					{
						HashMap<String, Integer> newPayload = new HashMap<String, Integer>();
						newPayload.put("time", Integer.valueOf(payload.get("time")));
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
					if(type==6)
					{
						activity.loadKills(null);
					}
				}
				else
				{
					switch(type)
					{
					case 0:
						HashMap<String, String> data = new HashMap<String, String>();
						JSONObject dataObj = new JSONObject(obj.getString("data"));
						data.put("time", dataObj.getString("time"));
						data.put("ww", dataObj.getString("ww"));
						data.put("players", dataObj.getString("players"));
						data.put("left", dataObj.getString("left"));
						data.put("alive", dataObj.getString("alive"));
						data.put("created", dataObj.getString("created"));
						activity.onGDUpdate(data);
						break;
					
					case 1:
						JSONArray array = obj.getJSONArray("data");
						activity.loadVoteList(array);
						break;
					case 2:
						JSONArray playerArray = obj.getJSONArray("data");
						activity.loadNearbyPlayers(playerArray);
						break;
					case 3:
						activity.onVoteRequest();
						break;
					case 4:
						activity.onKillRequest();
						break;
					case 5:
						break;
					
					case 6:
						JSONArray killArray = obj.getJSONArray("data");
						activity.loadKills(killArray);
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
				 VLRequest = null;
				 break;
			 case 2:
				 KLRequest = null;
				 break;
			 case 3:
				 VRequest = null;
				 break;
			 case 4:
				 KRequest = null;
				 break;
			 case 5:
				 LRequest = null;
				 break;
			 case 6:
				 AKRequest = null;
				 break;
			 default: break;
			 }
		  }
	}
}
