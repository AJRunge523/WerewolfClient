package edu.wm.werewolfclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import edu.wm.werewolfclient.adapters_and_objects.Kill;
import edu.wm.werewolfclient.adapters_and_objects.KillListAdapter;
import edu.wm.werewolfclient.adapters_and_objects.KillTargetListAdapter;
import edu.wm.werewolfclient.adapters_and_objects.PlayerTarget;
import edu.wm.werewolfclient.adapters_and_objects.VoteListAdapter;
import edu.wm.werewolfclient.adapters_and_objects.VoteListPlayer;



public class UserActivity extends Activity {

	public static final String TAG = "USER_ACTIVITY";
	
    private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 170;
	
	private ViewFlipper viewFlipper;
	private float lastX;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private boolean isNight = false;
	private boolean firstLoad;
	
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    
    String role, type, status;
    
    //View 0
    ProgressBar playerWerewolfBar;
    ProgressBar gameOverviewLoadingBar;
    TextView gameOverviewText;
    TextView playerWerewolfText;
    TextView playerTownsText;
    TextView timerText;
    TextView playerStatusText;
    
    TextView killListLabel;
    
    TransitionDrawable background;
    
    int score;
    
    //List Views
    
    ListView playerVoteList;
    ListView allKillsList;
    ListView werewolfKillList;
    
    
    View targetView;
    View voteView;
    View gameOver;
    
    WebRequestManager manager;
    String username;
    String password;
    
    Drawable townsPerson;
    Drawable lightning;
    Drawable werewolf;
    Drawable noose;
    
    Drawable werewolf_night;
    Drawable townsPerson_night;
    
    Drawable gameOverWolves;
    Drawable gameOverTowns;
    
    List<VoteListPlayer> voteListPlayers;
    List<Kill> listKills;
    List<PlayerTarget> playerTargetList;
    
    View killListLabels;
    
    //Kill list labels
    TextView killedByLabel;
    TextView victimLabel;
    TextView victimRoleLabel;
    TextView timeOfDeathLabel;
    
    PlayerTarget selectedTarget;
    CheckBox selectedTargetCB;
    VoteListPlayer selectedVote;
    CheckBox selectedVoteCB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		
		firstLoad = true;
		
        username = this.getIntent().getExtras().getString("username");
        password = this.getIntent().getExtras().getString("password");
        role = this.getIntent().getExtras().getString("ROLE");
        type = this.getIntent().getExtras().getString("TYPE");
        status = this.getIntent().getExtras().getString("STATUS");
        
        SharedPreferences prefs = getSharedPreferences("Stored_Data", 0);
        Editor editor = prefs.edit();
        editor.putString(username, status);
        editor.commit();
        
        score = prefs.getInt(username+"_score", 0);
		
	    WakefulIntentService.scheduleAlarms(new AppListener(),
                this, false);
		viewFlipper = (ViewFlipper)findViewById(R.id.view_flipper);
		
		
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
        
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        

    	//View targetView = (View) findViewById(R.id.targetView);
    	targetView=(View)inflater.inflate(R.layout.player_target_list, null);
    	gameOver = (View)inflater.inflate(R.layout.game_over,  null);
    	voteView = (View)inflater.inflate(R.layout.player_vote_list, null);  
        if(status.equals("ALIVE"))
        {
        	viewFlipper.addView(voteView, 1);
        }
        
        //View 0
        playerWerewolfBar = (ProgressBar) findViewById(R.id.playerWerewolfBar);
        gameOverviewLoadingBar = (ProgressBar) findViewById(R.id.gameOverviewLoadingBar);
        gameOverviewText = (TextView) findViewById(R.id.overviewGameSum);
        playerWerewolfText = (TextView) findViewById(R.id.playerWerewolfCountText);
        playerTownsText = (TextView) findViewById(R.id.playerTownsCountText);  
        timerText = (TextView) findViewById(R.id.timerText);   
        playerStatusText = (TextView) findViewById(R.id.playerStatusText);
        
        killListLabels = (View) findViewById(R.id.killListLabels);
        killedByLabel = (TextView) findViewById(R.id.killedByLabel);
        victimLabel = (TextView) findViewById(R.id.victimLabel);
        victimRoleLabel = (TextView) findViewById(R.id.victimRoleLabel);
        timeOfDeathLabel = (TextView) findViewById(R.id.timeOfDeathLabel);
        
        
        
        
        //Load in credentials and role information
        
        
        playerVoteList = (ListView) voteView.findViewById(R.id.player_vote_list);
        werewolfKillList = (ListView) targetView.findViewById(R.id.player_kill_list);
        
        //OnClick listeners for the button
        
        targetView.findViewById(R.id.killButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						sendKill();
					}
				});
        
        voteView.findViewById(R.id.voteButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						sendVote();
					}
				});
        
        
        allKillsList = (ListView) findViewById(R.id.player_kill_list);
        
        townsPerson = getResources().getDrawable(R.drawable.townsperson_trans);
        lightning = getResources().getDrawable(R.drawable.trans_lightning);
        werewolf = getResources().getDrawable(R.drawable.werewolf_trans);
        noose = getResources().getDrawable(R.drawable.trans_noose);
        
        werewolf_night = getResources().getDrawable(R.drawable.werewolf_black);
        townsPerson_night = getResources().getDrawable(R.drawable.townsperson_black);
        
        gameOverWolves = getResources().getDrawable(R.drawable.werewolf);
        gameOverTowns = getResources().getDrawable(R.drawable.townspeople);
        
		//Set up everything assuming that it's day.
		viewFlipper.removeView(targetView);
        
        manager = new WebRequestManager();
        constructViewFlipper(role, type);
          
        }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user, menu);
		return true;
	}
	
	public void onGDUpdate(HashMap<String, String> data) {
		gameOverviewLoadingBar.setVisibility(View.GONE);
		if(data!=null)
		{
			int wwCount = Integer.valueOf(data.get("ww"));
			int pCount = Integer.valueOf(data.get("alive"));
			if(wwCount == 0 || pCount == wwCount)
			{
				if(viewFlipper.getChildCount() > 2)
					viewFlipper.removeViewAt(1);
				viewFlipper.removeViewAt(0);
				viewFlipper.addView(gameOver, 0);
				ImageView v = ((ImageView)gameOver.findViewById(R.id.gameOverImage));
				v.setImageDrawable(wwCount == 0 ? gameOverTowns : gameOverWolves);
				TextView gameOverWinner = ((TextView)gameOver.findViewById(R.id.gameOverWinner));
				TextView playerScore = ((TextView)gameOver.findViewById(R.id.playerFinalScore));
				if(wwCount == 0)
				{
					gameOverWinner.setText("TOWNS PEOPLE WIN!!!");
					gameOverWinner.setTextColor(Color.BLACK);
					int newScore = score;
					if(type.equals("TOWN"))
						newScore+=500;
					playerScore.setText("Your score: " + newScore);
					playerScore.setTextColor(Color.BLACK);
					gameOver.setBackgroundColor(Color.WHITE);
					allKillsList.setBackgroundColor(Color.WHITE);
					
			        killListLabels.setBackgroundColor(Color.WHITE);
			        killedByLabel.setTextColor(Color.BLACK);
			        victimLabel.setTextColor(Color.BLACK);
			        victimRoleLabel.setTextColor(Color.BLACK);
			        timeOfDeathLabel.setTextColor(Color.BLACK);
				}
				else
				{
					gameOverWinner.setText("WEREWOLVES WIN!!!");
					gameOverWinner.setTextColor(Color.RED);
					int newScore = score;
					if(type.equals("WOLF"))
						newScore+=500;
					playerScore.setText("Your score: " + newScore);
					playerScore.setTextColor(Color.RED);
					gameOver.setBackgroundColor(Color.BLACK);
					
					allKillsList.setBackgroundColor(Color.BLACK);
					killListLabels.setBackgroundColor(Color.BLACK);
			        killedByLabel.setTextColor(Color.WHITE);
			        victimLabel.setTextColor(Color.WHITE);
			        victimRoleLabel.setTextColor(Color.WHITE);
			        timeOfDeathLabel.setTextColor(Color.WHITE);
				}
		        manager.makeWebRequest(this, "http://secret-wildwood-3803.herokuapp.com/auth/kills", username, password, null, 6);
				viewFlipper.setDisplayedChild(0);
				
				return;
			}
			playerWerewolfBar.setProgress((int)(100-((float)wwCount/pCount * 100)));
			playerWerewolfBar.setVisibility(View.VISIBLE);
			if(data.get("time").equals("night") && !isNight || data.get("time").equals("day") && isNight)
			{
				isNight = !isNight;
				flipDayNight();
			}
	        else
	        {
	        	if(data.get("time").equals("day"))
	        	{
	        		if(status.equals("ALIVE"))
	    	        	manager.makeWebRequest(this, "http://secret-wildwood-3803.herokuapp.com/auth/players", username, password, null, 1);
	        	}
	        	else
	        	{
	                if(status.equals("ALIVE") && type.equals("WOLF"))
	    	        	manager.makeWebRequest(this, "http://secret-wildwood-3803.herokuapp.com/auth/players/nearbyPlayers", username, password, null, 2);
	        	}
	        }
			//Load the kills list...hopefully
	        manager.makeWebRequest(this, "http://secret-wildwood-3803.herokuapp.com/auth/kills", username, password, null, 6);

			
			gameOverviewText.setText("Current time: " +data.get("time"));
			Log.v(TAG, data.get("time"));
			Log.v(TAG, data.get("left"));
			final String time = data.get("time");
			gameOverviewText.setVisibility(View.VISIBLE);
			playerWerewolfText.setText("" + wwCount + (wwCount > 1 ? " werewolves" : " werewolf"));
			playerWerewolfText.setVisibility(View.VISIBLE);
			playerTownsText.setText("" + (pCount - wwCount) + ((pCount - wwCount) > 1 ? " villagers" : " villager"));
			playerTownsText.setVisibility(View.VISIBLE);
			if(status.equals("ALIVE"))
				if(type.equals("TOWN"))
					playerStatusText.setText("YOU ARE A TOWNS PERSON");
				else
					playerStatusText.setText("YOU ARE A WEREWOLF");
			else
				playerStatusText.setText("YOU ARE DEAD!");
			
			new CountDownTimer((Long.valueOf(data.get("left")) + 1000), 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

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
					 timerText.setText("Time until " + (time.equals("day") ? "night: " : "day: ") + hoursT + ":" + minT + ":" + secT );
				 }

				 public void onFinish() {
					 constructViewFlipper(role, type);
				 }
				}
				.start();
			timerText.setVisibility(View.VISIBLE);
		}
		else
		{
			gameOverviewText.setText("Error retrieving data, please try refreshing.");
			gameOverviewText.setVisibility(View.VISIBLE);
		}
	}
	
	public void flipDayNight()
	{
		//for(int n = 0; n<viewFlipper.getChildCount(); n++)
		//{
			background = (TransitionDrawable) (viewFlipper.getChildAt(0).getBackground());
			if(background == null)
				Log.i(TAG, "" + viewFlipper.getCurrentView().getClass());
			else if(isNight)
				background.startTransition(700);
			else
				background.reverseTransition(700);
			/*
			background = (TransitionDrawable) (viewFlipper.getChildAt(2).getBackground());
			if(background == null)
				Log.i(TAG, "" + viewFlipper.getCurrentView().getClass());
			else if(isNight)
				background.startTransition(700);
			else
				background.reverseTransition(700);
			*/
		//}
		if(isNight) //Add in the KillView, if they are a werewolf, otherwise simply remove the vote view.
		{	
			
			TransitionDrawable listBG = (TransitionDrawable) allKillsList.getBackground();
			listBG.startTransition(700);
			allKillsList.setCacheColorHint(Color.BLACK);
			
			if(status.equals("ALIVE"))
			{
				viewFlipper.removeView(voteView);
				if(!type.equals("TOWN"))
					viewFlipper.addView(targetView, 1);
				if(viewFlipper.getDisplayedChild() == 1)
					viewFlipper.setDisplayedChild(0);
			}
		}
		else
		{
			
			TransitionDrawable listBG = (TransitionDrawable) allKillsList.getBackground();
			listBG.reverseTransition(700);
			allKillsList.setCacheColorHint(Color.WHITE);
			
			if(status.equals("ALIVE"))
			{
				if(!type.equals("TOWN"))
					viewFlipper.removeView(targetView);
			viewFlipper.addView(voteView, 1);
			if(viewFlipper.getDisplayedChild() == 1)
				viewFlipper.setDisplayedChild(0);
			}
			
		}
		if(!isNight)
		{
			gameOverviewText.setTextColor(Color.BLACK);
		    playerWerewolfText.setTextColor(Color.BLACK);
		    playerTownsText.setTextColor(Color.BLACK);
	        timerText.setTextColor(Color.BLACK);
	        playerStatusText.setTextColor(Color.BLACK);
	        ((TransitionDrawable) killListLabels.getBackground()).reverseTransition(700);
	        killedByLabel.setTextColor(Color.BLACK);
	        victimLabel.setTextColor(Color.BLACK);
	        victimRoleLabel.setTextColor(Color.BLACK);
	        timeOfDeathLabel.setTextColor(Color.BLACK);
	        if(status.equals("ALIVE"))
	        	manager.makeWebRequest(this, "http://secret-wildwood-3803.herokuapp.com/auth/players", username, password, null, 1);
		}
		else
		{
			gameOverviewText.setTextColor(Color.RED);
		    playerWerewolfText.setTextColor(Color.WHITE);
		    playerTownsText.setTextColor(Color.WHITE);
	        timerText.setTextColor(Color.RED);
	        playerStatusText.setTextColor(Color.RED);
	        ((TransitionDrawable) killListLabels.getBackground()).startTransition(700);
	        killedByLabel.setTextColor(Color.WHITE);
	        victimLabel.setTextColor(Color.WHITE);
	        victimRoleLabel.setTextColor(Color.WHITE);
	        timeOfDeathLabel.setTextColor(Color.WHITE);
	        if(status.equals("ALIVE") && !type.equals("TOWN"))
	        	manager.makeWebRequest(this, "http://secret-wildwood-3803.herokuapp.com/auth/players/nearbyPlayers", username, password, null, 2);

		}
	}
	
	public void loadVoteList(JSONArray array)
	{
		voteListPlayers = new ArrayList<VoteListPlayer>();
		for(int i = 0; i<array.length(); i++)
		{
			JSONObject obj;
			try {
				obj = array.getJSONObject(i);
				Log.v(TAG, obj.toString());
				VoteListPlayer p = new VoteListPlayer(obj.getString("userID"), townsPerson);
				voteListPlayers.add(p);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		playerVoteList.setAdapter(new VoteListAdapter(this, voteListPlayers));
		playerVoteList.invalidateViews();
		
	}
	
	public void loadKills(JSONArray array) {
		
		if(array == null)
		{
			return;
		}
		Log.v(TAG, "Got here!");
		listKills = new ArrayList<Kill>();
		for(int i = 0; i<array.length(); i++)
		{
			JSONObject obj;
			try {
				obj = array.getJSONObject(i);
				Log.v(TAG, obj.toString());
				Drawable d, e;
				if(obj.getInt("type") == 0)
				{
					if(isNight)
						d = werewolf_night;
					else
						d = werewolf;
				}
				else if(obj.getInt("type") == 1)
					d = noose;
				else
					d = lightning;
				
				if(obj.getBoolean("werewolf"))
				{
					if(isNight)
						e = werewolf_night;
					else
						e = werewolf;
				}
				else
				{
					if(isNight)
						e = townsPerson_night;
					else
						e = townsPerson;
				}
				Kill k = new Kill(obj.getString("victimID"), d, (System.currentTimeMillis() - obj.getLong("timestamp")), e, (isNight ? Color.WHITE : Color.BLACK));
				int slot = 0;
				for(Kill kill: listKills)
				{
					if(kill.getDate() > k.getDate())
						break;
					slot++;
				}	
				listKills.add(slot, k);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		allKillsList.setAdapter(new KillListAdapter(this, listKills));
		allKillsList.invalidateViews();
	}

	public void loadNearbyPlayers(JSONArray array)
	{
		playerTargetList = new ArrayList<PlayerTarget>();
		for(int i = 0; i<array.length(); i++)
		{
			JSONObject obj;
			try {
				obj = array.getJSONObject(i);
				Log.v(TAG, obj.toString());
				if(!obj.getBoolean("dead"))
				{
					PlayerTarget p = new PlayerTarget(obj.getString("userID"), townsPerson, obj.getInt("distance"));
					playerTargetList.add(p);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		werewolfKillList.setAdapter(new KillTargetListAdapter(this, playerTargetList));
		werewolfKillList.invalidateViews();
	}
	
	public void sendVote()
	{
		HashMap<String, String> payload = new HashMap<String, String>();
		payload.put("type", "vote");
        manager.makeWebRequest(this, "http://secret-wildwood-3803.herokuapp.com/auth/players/" + selectedVote.getUserID(), username, password, payload, 3);
	}
	
	public void sendKill()
	{
		HashMap<String, String> payload = new HashMap<String, String>();
		payload.put("type", "kill");
        manager.makeWebRequest(this, "http://secret-wildwood-3803.herokuapp.com/auth/players/" + selectedTarget.getPlayerName(), username, password, payload, 4);
	}
	
	public void onKillRequest()
	{
		Toast toast = Toast.makeText(getApplicationContext(), "Successfully killed player " + selectedTarget.getPlayerName(), Toast.LENGTH_SHORT);
		toast.show();
		viewFlipper.removeView(targetView);
		score+=100;
	}
	
	public void onVoteRequest()
	{
		Toast toast = Toast.makeText(getApplicationContext(), "Successfully voted for player " + selectedVote.getUserID(), Toast.LENGTH_SHORT);
		toast.show();
		score+=50;
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
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		super.dispatchTouchEvent(event);
		return gestureDetector.onTouchEvent(event);
	}

	public void constructViewFlipper(String role, String type)
	{
        playerWerewolfBar.setVisibility(View.GONE);
        gameOverviewText.setVisibility(View.GONE);
        playerWerewolfText.setVisibility(View.GONE);
        playerTownsText.setVisibility(View.GONE);
        timerText.setVisibility(View.GONE);
        gameOverviewLoadingBar.setVisibility(View.VISIBLE);
        Log.v(TAG, "About to make request");
        manager.makeWebRequest(this, "http://secret-wildwood-3803.herokuapp.com/auth/game", username, password, null, 0);
        
	}
	
	private void logout()
	{
		SharedPreferences prefs = getSharedPreferences("Stored_Data", 0);
		Editor editor = prefs.edit();
		editor.remove("user");
		editor.remove("pw");
		editor.putString(username, status);
		editor.putInt(username+"_score", score);
		editor.commit();
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		this.finish();
	}
	
	private void reload()
	{
        constructViewFlipper(role, type);
	}
	
	public void setSelectedTarget(PlayerTarget playerName, CheckBox selected) {
		if(selectedTarget !=null)
		{
			selectedTargetCB.setChecked(false);
			selectedTarget.setChecked(false);
		}
		selectedTarget = playerName;
		playerName.setChecked(true);
		selectedTargetCB = selected;
		
	}
	
	public void setSelectedVote(VoteListPlayer playerName, CheckBox selected) {
		if(selectedVote !=null)
		{
			selectedVoteCB.setChecked(false);
			selectedVote.setChecked(false);
		}
		selectedVote = playerName;
		selectedVoteCB = selected;
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
    				Toast toast = Toast.makeText(getApplicationContext(), "This is view" + viewFlipper.getDisplayedChild(), Toast.LENGTH_SHORT);
    			    toast.show();
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
