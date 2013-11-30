package edu.wm.werewolfclient;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;



public class UserActivity extends Activity {

	public static final String TAG = "USER_ACTIVITY";
	
    private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 170;
	
	private ViewFlipper viewFlipper;
	private float lastX;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private boolean isNight;
	
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    
    //View 0
    ProgressBar playerWerewolfBar;
    ProgressBar gameOverviewLoadingBar;
    TextView gameOverviewText;
    TextView playerWerewolfText;
    TextView playerTownsText;
    
    TransitionDrawable background;
    
    
    //View 1
    TextView gameInfoTextView;
    TextView playerInfoTextView;
    TextView gameInfoLabel;
    TextView playerInfoLabel;
    
    ListView playerVoteList;
    ListView playerKillList;
    
    WebRequestManager manager;
    String username;
    String password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
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
        //View 0
        playerWerewolfBar = (ProgressBar) findViewById(R.id.playerWerewolfBar);
        gameOverviewLoadingBar = (ProgressBar) findViewById(R.id.gameOverviewLoadingBar);
        gameOverviewText = (TextView) findViewById(R.id.overviewGameSum);
        playerWerewolfText = (TextView) findViewById(R.id.playerWerewolfCountText);
        playerTownsText = (TextView) findViewById(R.id.playerTownsCountText);       
        
                
        //View 1
        gameInfoTextView = (TextView) findViewById(R.id.gameInfoText);
        playerInfoTextView = (TextView) findViewById(R.id.playerInfoText);
        gameInfoLabel = (TextView) findViewById(R.id.gameInfoLabelText);
        playerInfoLabel = (TextView) findViewById(R.id.playerInfoLabelText);
        
        
        //Load in credentials and role information
        String role = this.getIntent().getExtras().getString("ROLE");
        String type = this.getIntent().getExtras().getString("TYPE");
        username = this.getIntent().getExtras().getString("username");
        password = this.getIntent().getExtras().getString("password");
        
        
        manager = new WebRequestManager();
        constructViewFlipper(role, type);
        
        playerVoteList = (ListView) findViewById(R.id.player_vote_list);
        playerKillList = (ListView) findViewById(R.id.player_kill_list);
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
            "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
            "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
            "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
            "Android", "iPhone", "WindowsMobile" };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
          list.add(values[i]);
        }

        playerVoteList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list));

        playerVoteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> parent, final View view,
              int position, long id) {
            final String item = (String) parent.getItemAtPosition(position);
            list.remove(item);
            playerVoteList.invalidateViews();
          }});

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
			playerWerewolfBar.setProgress((int)(100-((float)wwCount/pCount * 100)));
			playerWerewolfBar.setVisibility(View.VISIBLE);
			if(data.get("time").equals("night") && !isNight || data.get("time").equals("day") && isNight)
			{
				isNight = !isNight;
				flipDayNight();
			}
				
			gameOverviewText.setText("Current time: " +data.get("time"));
			gameOverviewText.setVisibility(View.VISIBLE);
			playerWerewolfText.setText("" + wwCount + (wwCount > 1 ? " werewolves" : " werewolf"));
			playerWerewolfText.setVisibility(View.VISIBLE);
			playerTownsText.setText("" + (pCount - wwCount) + ((pCount - wwCount) > 1 ? " villages" : " villager"));
			playerTownsText.setVisibility(View.VISIBLE);
		}
		else
		{
			gameOverviewText.setText("Error retrieving data, please try refreshing.");
			gameOverviewText.setVisibility(View.VISIBLE);
		}
	}
	
	public void flipDayNight()
	{
		for(int n = 0; n<viewFlipper.getChildCount(); n++)
		{
			background = (TransitionDrawable) (viewFlipper.getChildAt(n).getBackground());
			if(background == null)
				Log.i(TAG, "" + viewFlipper.getCurrentView().getClass());
			else if(isNight)
				background.startTransition(700);
			else
				background.reverseTransition(700);
		}
		if(isNight)
		{
			TransitionDrawable listBG = (TransitionDrawable) playerVoteList.getBackground();
			listBG.startTransition(700);
			playerVoteList.setCacheColorHint(Color.BLACK);
			listBG = (TransitionDrawable) playerKillList.getBackground();
			listBG.startTransition(700);
			playerKillList.setCacheColorHint(Color.BLACK);
		}
		else
		{
			TransitionDrawable listBG = (TransitionDrawable) playerVoteList.getBackground();
			listBG.reverseTransition(700);
			playerVoteList.setCacheColorHint(Color.WHITE);
			listBG = (TransitionDrawable) playerKillList.getBackground();
			listBG.reverseTransition(700);
			playerKillList.setCacheColorHint(Color.WHITE);
		}
		if(!isNight)
		{
			gameOverviewText.setTextColor(Color.BLACK);
		    playerWerewolfText.setTextColor(Color.BLACK);
		    playerTownsText.setTextColor(Color.BLACK);
		    gameInfoTextView.setTextColor(Color.BLACK);
		    playerInfoTextView.setTextColor(Color.BLACK);
	        gameInfoLabel.setTextColor(Color.BLACK);
	        playerInfoLabel.setTextColor(Color.BLACK);
		}
		else
		{
			gameOverviewText.setTextColor(Color.RED);
		    playerWerewolfText.setTextColor(Color.WHITE);
		    playerTownsText.setTextColor(Color.WHITE);
		    gameInfoTextView.setTextColor(Color.WHITE);
		    playerInfoTextView.setTextColor(Color.WHITE);
	        gameInfoLabel.setTextColor(Color.WHITE);
	        playerInfoLabel.setTextColor(Color.WHITE);
		}
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
		editor.commit();
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		this.finish();
	}
	
	private void reload()
	{
        manager.makeWebRequest(this, "http://secret-wildwood-3803.herokuapp.com/auth/game", username, password, null, 0);
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
