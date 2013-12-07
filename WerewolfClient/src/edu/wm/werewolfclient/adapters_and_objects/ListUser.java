package edu.wm.werewolfclient.adapters_and_objects;

public class ListUser {
	private String userName;
	private boolean isPlaying;
	private boolean isChecked;
	
	public ListUser(String userName, boolean isPlaying) {
		super();
		this.userName = userName;
		this.isPlaying = isPlaying;
		this.isChecked = false;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}


	
	
}
