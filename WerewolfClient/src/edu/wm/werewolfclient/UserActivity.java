package edu.wm.werewolfclient;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;



public class UserActivity extends Activity {

    private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 170;
	
	private ViewFlipper viewFlipper;
	private float lastX;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		viewFlipper = (ViewFlipper)findViewById(R.id.view_flipper);
		
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
        
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user, menu);
		return true;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
	super.dispatchTouchEvent(event);
	return gestureDetector.onTouchEvent(event);
	}
	
	/*
	public boolean onTouchEvent(MotionEvent touchevent)
    {
		switch (touchevent.getAction())
		{
		// when user first touches the screen to swap
		case MotionEvent.ACTION_DOWN:
		{
			lastX = touchevent.getX();
			break;
		}
		case MotionEvent.ACTION_UP:
		{
			float currentX = touchevent.getX();

			// if left to right swipe on screen
			if (lastX < currentX)
			{
				// If no more View/Child to flip
				if (viewFlipper.getDisplayedChild() == 0)
					break;

				// set the required Animation type to ViewFlipper
				// The Next screen will come in form Left and current Screen will go OUT from Right
				viewFlipper.setInAnimation(this, R.anim.in_from_left);
				viewFlipper.setOutAnimation(this, R.anim.out_to_right);
				// Show the next Screen
				viewFlipper.showNext();
			}

			// if right to left swipe on screen
			if (lastX > currentX)
			{
				if (viewFlipper.getDisplayedChild() == 1)
					break;
				// set the required Animation type to ViewFlipper
				// The Next screen will come in form Right and current Screen will go OUT from Left
				viewFlipper.setInAnimation(this, R.anim.in_from_right);
				viewFlipper.setOutAnimation(this, R.anim.out_to_left);
				// Show The Previous Screen
				viewFlipper.showPrevious();
			}
			break;
		}
		}
		return false;
    }
	*/
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
    				if(viewFlipper.getDisplayedChild() == 0)
    				{
    					Toast toast = Toast.makeText(getApplicationContext(), "This is view 0!", Toast.LENGTH_SHORT);
    			    	toast.show();
    				}
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
