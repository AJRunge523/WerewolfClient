package edu.wm.werewolfclient.adapters_and_objects;

import android.graphics.drawable.Drawable;

public class ListPlayer {

	private String userName;
	private long lastUpdate;
	private boolean isChecked;
	private boolean isAlive;
	private Drawable playerImage;
	
	
	public ListPlayer(String userName, long lastUpdate, boolean isAlive, Drawable playerImage) {
		super();
		this.userName = userName;
		this.lastUpdate = lastUpdate;
		this.setAlive(isAlive);
		this.isChecked=false;
		this.playerImage = playerImage;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public long getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	public Drawable getPlayerImage() {
		return playerImage;
	}
	public void setPlayerImage(Drawable playerImage) {
		this.playerImage = playerImage;
	}
}
