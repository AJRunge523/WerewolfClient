package edu.wm.werewolfclient.adapters_and_objects;

import android.graphics.drawable.Drawable;

public class VoteListPlayer {

	private String userID;
	private Drawable playerImage;
	private boolean isChecked;
	public VoteListPlayer(String userID, Drawable playerImage) {
		super();
		this.userID = userID;
		this.playerImage = playerImage;
		this.setChecked(false);
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public Drawable getPlayerImage() {
		return playerImage;
	}
	public void setPlayerImage(Drawable playerImage) {
		this.playerImage = playerImage;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	
	
}
