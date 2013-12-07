package edu.wm.werewolfclient;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import edu.wm.werewolfclient.adapters_and_objects.AdminPlayerListAdapter;
import edu.wm.werewolfclient.adapters_and_objects.AdminUserListAdapter;
import edu.wm.werewolfclient.adapters_and_objects.ListPlayer;
import edu.wm.werewolfclient.adapters_and_objects.ListUser;

public class AdminActivity extends FragmentActivity {

	AdminRequestManager manager;
	
    private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 170;
	
	private ViewFlipper viewFlipper;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
	
	TextView userCount;
	TextView currentGameLength;
	TextView currentGamePlayerCount;
	TextView currentGameTime;
	TextView gameInfoLabel;
	TextView loadingInfoLabel;
	
	Spinner timeSpinner;
	int newTime;
	
	Button restartButton;
	Button removeUserButton;
	Button smitePlayerButton;
	
	String username;
	String password;
	
	ListView adminUserList;
	ListView adminPlayerList;
	
	ProgressBar adminLoadBar;
	
	Drawable townDrawable;
	Drawable wwDrawable;
	
	ListUser selectedUser = null;
	
	public static final String TAG = "ADMIN_ACTIVITY";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);
		
		manager = new AdminRequestManager();
		
		viewFlipper = (ViewFlipper)findViewById(R.id.admin_view_flipper);
		
		townDrawable = getResources().getDrawable(R.drawable.townsperson_trans);
		wwDrawable = getResources().getDrawable(R.drawable.werewolf_trans);
		
		//Set up gesture detection
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        
        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.in_from_left);
        slideLeftOut = AnimationUtils
                .loadAnimation(this, R.anim.out_to_left);
        slideRightIn = AnimationUtils
                .loadAnimation(this, R.anim.in_from_right);
        slideRightOut = AnimationUtils.loadAnimation(this,
                R.anim.out_to_right);
		
		
		
        username = this.getIntent().getExtras().getString("username");
        password = this.getIntent().getExtras().getString("password");
		
        userCount = (TextView) findViewById(R.id.adminUserCount);
        currentGameLength = (TextView) findViewById(R.id.adminCurrentGameLength);
        currentGamePlayerCount = (TextView) findViewById(R.id.adminCurrentGamePlayerCount);
        currentGameTime = (TextView) findViewById(R.id.adminCurrentGameTimeToSwitch);
        gameInfoLabel = (TextView) findViewById(R.id.adminCurrentGameLabel);
        loadingInfoLabel = (TextView) findViewById(R.id.adminLoadingLabel);
        
        
		adminLoadBar = (ProgressBar) findViewById(R.id.adminLoadBar);
        
		timeSpinner = (Spinner) findViewById(R.id.timeSpinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.TimesArray, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		timeSpinner.setAdapter(adapter);
		timeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				String item = (String) parent.getItemAtPosition(pos);
				if(item.equals("10 seconds"))
					newTime = 10000;
				else if(item.equals("5 minutes"))
					newTime = 60000 * 5;
				else if(item.equals("15 minutes"))
					newTime = 60000 * 15;
				else if(item.equals("30 minutes"))
					newTime = 60000 * 30;
				else if(item.equals("1 hour"))
					newTime = 60000 * 60;
				else if(item.equals("2 hours"))
					newTime = 60000 * 60 * 2;
				else if(item.equals("4 hours"))
					newTime = 60000 * 60 * 4;
				else if(item.equals("6 hours"))
					newTime = 60000 * 60 * 6;
				else if(item.equals("12 hours"))
					newTime = 60000 * 60 * 12;
				Toast toast = Toast.makeText(getApplicationContext(), "" + newTime, Toast.LENGTH_SHORT);
				toast.show();					
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				newTime = 0;
			}
			
			
		});
		
		findViewById(R.id.restartButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						restartGame();
					}
				});
		findViewById(R.id.smitePlayerButton).setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						showConfirmationDialog(0);
						
					}
				});
		
		findViewById(R.id.removeUserButton).setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						showConfirmationDialog(1);
						
					}
				});
		adminUserList = (ListView) findViewById(R.id.adminUserList);
		/*
        adminUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                int position, long id) {
      			if(selectedUser != null)
      				selectedUser.setChecked(false);
      			final ListUser item = (ListUser) parent.getItemAtPosition(position);
      			item.setChecked(true);
      			selectedUser = item;
      			Toast toast = Toast.makeText(getApplicationContext(), "Clicked item " + position, Toast.LENGTH_SHORT);
      			toast.show();
            }});
		*/
		
		adminPlayerList = (ListView) findViewById(R.id.adminPlayerList);
			
		
		
		
		loadData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admin, menu);
		return true;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		super.dispatchTouchEvent(event);
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public void onStop()
	{
		super.onStop();
		ConfirmationDialogFragment f = (ConfirmationDialogFragment) getSupportFragmentManager().findFragmentByTag("fragment_confirmation");
		if(f != null)
			f.getDialog().dismiss();
		//((ConfirmationDialogFragment) getSupportFragmentManager().findFragmentByTag("fragment_confirmation")).getDialog().dismiss();
	}
	
	private void showConfirmationDialog(int type) {
        FragmentManager fm = getSupportFragmentManager();
        ConfirmationDialogFragment confirm = new ConfirmationDialogFragment();
        confirm.setType(type);
        confirm.show(fm, "fragment_confirmation");
	}
	
	public void smitePlayers() {
		HashMap<String, String> payload = new HashMap<String, String>();
		for(int i = 0; i<adminPlayerList.getAdapter().getCount(); i++)
		{
			ListPlayer p = ((ListPlayer) adminPlayerList.getAdapter().getItem(i));
			if(p.isChecked())
			{
				Log.v(TAG, p.getUserName());
				payload.put("user"+i, p.getUserName());
			}
		}
		
		manager.makeWebRequest(this, "http://secret-wildwood-3803.herokuapp.com/auth/smitePlayer", username, password, payload, 2);
	}
	
	public void removeUsers() {
		HashMap<String, String> payload = new HashMap<String, String>();
		for(int i = 0; i<adminUserList.getAdapter().getCount(); i++)
		{
			ListUser u = ((ListUser) adminUserList.getAdapter().getItem(i));
			if(u.isChecked())
			{
				Log.v(TAG, "Removing " + u.getUserName());
				payload.put("user"+i, u.getUserName());
			}
		}
		manager.makeWebRequest(this, "http://secret-wildwood-3803.herokuapp.com/auth/removeUser", username, password, payload, 1);

		
		
	}
	
	@Override
	   public boolean onOptionsItemSelected(MenuItem item) {

	   switch(item.getItemId()){
	       case R.id.action_logout:
	           logout();
	           break;
	       case R.id.action_reload:
	    	   reload();
	    	   break;
	       default: break;
	   }
	   return true;
	   }
	
	private void loadData()
	{
		userCount.setVisibility(View.GONE);
		currentGameLength.setVisibility(View.GONE);
		currentGamePlayerCount.setVisibility(View.GONE);
		currentGameTime.setVisibility(View.GONE);
		gameInfoLabel.setVisibility(View.GONE);
		adminLoadBar.setVisibility(View.VISIBLE);
		loadingInfoLabel.setVisibility(View.VISIBLE);
        manager.makeWebRequest(this, "http://secret-wildwood-3803.herokuapp.com/auth/adminInfo", username, password, null, 0);
	}
	
	public void onGDUpdate(HashMap<String, String> data)
	{
		ArrayList<ListUser> users = new ArrayList<ListUser>();
		ArrayList<ListPlayer> players = new ArrayList<ListPlayer>();
		if(!data.containsKey("users"))
		{
			Toast toast = Toast.makeText(getApplicationContext(), "ERROR: NO USERS, PLEASE REFRESH", Toast.LENGTH_SHORT);
			toast.show();	
			return;
		}
		try {
			JSONArray array = new JSONArray(data.get("users"));
			for(int i = 0; i<array.length(); i++)
			{
				JSONObject obj = array.getJSONObject(i);
				Log.v(TAG, obj.toString());
				ListUser u = new ListUser(obj.getString("username"), obj.getBoolean("playing"));
				users.add(u);
			}
			adminUserList.setAdapter(new AdminUserListAdapter(this, users));
			adminUserList.invalidateViews();
			Log.v(TAG, "SHOULD HAVE SET THE ADAPTER");
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			JSONArray array = new JSONArray(data.get("players"));
			long currentMillis = System.currentTimeMillis();
			for(int i = 0; i<array.length(); i++)
			{
				JSONObject obj = array.getJSONObject(i);
				Log.v(TAG, obj.toString());
				long millis;
				try {
					millis = currentMillis - obj.getLong("lastUpdate");
				}
				catch (JSONException e)
				{
					millis = -1;
				}
				ListPlayer p = new ListPlayer(obj.getString("userID"), millis, obj.getBoolean("dead"), (obj.getBoolean("werewolf") ? wwDrawable: townDrawable));
				players.add(p);
			}
			adminPlayerList.setAdapter(new AdminPlayerListAdapter(this, players));
			adminPlayerList.invalidateViews();
			Log.v(TAG, "SHOULD HAVE SET THE ADAPTER FOR THE PLAYERS");
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		userCount.setText("Total users: " + users.size());
		userCount.setVisibility(View.VISIBLE);
		currentGameLength.setText("Game Started: " + data.get("created"));
		currentGameLength.setVisibility(View.VISIBLE);
		
		currentGamePlayerCount.setText(data.get("alive") + "/" + players.size()+ " players remain");
		currentGamePlayerCount.setVisibility(View.VISIBLE);
		currentGameTime.setVisibility(View.VISIBLE);
		
		final String time = data.get("time");
		new CountDownTimer(Long.valueOf(data.get("left")), 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

			 public void onTick(long millisUntilFinished) {
				 String hoursT = "", minT="", secT="";
				 long hours = millisUntilFinished / 1000 / 60 / 60;
				 if(hours < 10)
					 hoursT = "0" + hours;
				 else
					 hoursT = ""+hours;
				 long minutes = (millisUntilFinished - (hours * 3600 * 1000)) / 1000 / 60;
				 if(minutes < 10)
					 minT = "0" + minutes;
				 else
					 minT = "" + minutes;
				 long seconds = (millisUntilFinished - (hours * 3600 * 1000) - (minutes * 60 * 1000)) / 1000;
				 if(seconds<10)
					 secT = "0" + seconds;
				 else
					 secT = "" + seconds;
			     currentGameTime.setText("Time until " + (time.equals("day") ? "night: " : "day: ") + hoursT + ":" + minT + ":" + secT );
			 }

			 public void onFinish() {
				 reload();
			 }
			}
			.start();
			
		
		adminLoadBar.setVisibility(View.GONE);
		loadingInfoLabel.setVisibility(View.GONE);
		
	}
	
	private void logout()
	{
		SharedPreferences prefs = getSharedPreferences("Stored_Data", 0);
		Editor editor = prefs.edit();
		editor.remove("user");
		editor.remove("pw");
		editor.commit();
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		this.finish();
	}
	
	private void reload() 
	{
        loadData();
	}
	
	private void restartGame() {
		SharedPreferences prefs = getSharedPreferences("Stored_Data", 0);
		String username = prefs.getString("user", null);
		String password = prefs.getString("pw", null);
		if(username != null && password != null) {
			HashMap<String, String> payload = new HashMap<String, String>();
			payload.put("time", String.valueOf(newTime));
			manager.makeWebRequest(this,"http://secret-wildwood-3803.herokuapp.com/auth/restart", username, password, payload, 3);
		}
	}

	
	
	public void onRestartGame(String status) {
		loadData();		
	}
	
    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
    				viewFlipper.setInAnimation(slideRightIn);
    				viewFlipper.setOutAnimation(slideLeftOut);
                    viewFlipper.showNext();
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
    				viewFlipper.setInAnimation(slideLeftIn);
    				viewFlipper.setOutAnimation(slideRightOut);
                    viewFlipper.showPrevious();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
	
}
