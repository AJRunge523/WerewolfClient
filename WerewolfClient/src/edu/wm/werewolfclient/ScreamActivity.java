package edu.wm.werewolfclient;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.Menu;

public class ScreamActivity extends Activity {

	String role, type, status, username, password;
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scream);
		role = this.getIntent().getExtras().getString("ROLE");
		type = this.getIntent().getExtras().getString("TYPE");
		status = this.getIntent().getExtras().getString("STATUS");
		username = this.getIntent().getExtras().getString("username");
		password = this.getIntent().getExtras().getString("password");
				
				
		intent = new Intent(this, UserActivity.class);
		intent.putExtra("ROLE", role);
		intent.putExtra("TYPE", type);
		intent.putExtra("STATUS", status);
		intent.putExtra("username", username);
		intent.putExtra("password", password);
		
		MediaPlayer mp = MediaPlayer.create(this, R.raw.scream);
		mp.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {
				moveOn();
			}
			
		});
		mp.start();
	}

	public void moveOn()
	{
		startActivity(intent);
		this.finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scream, menu);
		return true;
	}

}
