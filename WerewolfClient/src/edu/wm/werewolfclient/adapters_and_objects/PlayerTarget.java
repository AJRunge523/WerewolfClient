package edu.wm.werewolfclient.adapters_and_objects;

import android.graphics.drawable.Drawable;

public class PlayerTarget {
	private String playerName;
	private boolean isSelected;
	private Drawable playerImage;
	private int distance;
	
	public PlayerTarget(String playerName, Drawable playerImage, int distance) {
		super();
		this.playerName = playerName;
		this.playerImage = playerImage;
		this.isSelected = false;
		this.setDistance(distance);
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public boolean isChecked() {
		return isSelected;
	}
	public void setChecked(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public Drawable getPlayerImage() {
		return playerImage;
	}
	public void setPlayerImage(Drawable playerImage) {
		this.playerImage = playerImage;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
}
