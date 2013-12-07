package edu.wm.werewolfclient.adapters_and_objects;

import android.graphics.drawable.Drawable;



public class Kill {
	private String victimID;
	private Drawable killImage;
	private long date;
	private Drawable victimImage;
	private int color;
	
	public Kill(String victimID, Drawable killImage, long date, Drawable victimImage, int color) {
		super();
		this.victimID = victimID;
		this.killImage = killImage;
		this.setDate(date);
		this.setVictimImage(victimImage);
		this.color = color;
	}

	public String getVictimID() {
		return victimID;
	}

	public void setVictimID(String victimID) {
		this.victimID = victimID;
	}

	public Drawable getKillImage() {
		return killImage;
	}

	public void setKillImage(Drawable killImage) {
		this.killImage = killImage;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public Drawable getVictimImage() {
		return victimImage;
	}

	public void setVictimImage(Drawable victimImage) {
		this.victimImage = victimImage;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	
}
