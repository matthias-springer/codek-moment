package edu.ucsd.cse110.team27.placeits.data;

public class User {
	
	private static User instance;
	
	public static final String DatastoreURI = "http://team27placeits.appspot.com/user";
	public static final String PREFS = "team27.placeits.prefs";
	
	private String name;
	private String password;
	private String active = "";
	private String pulled = "";
	private String scheduled = "";
	private String categorized = "";
		
	public User(String n, String pw, String a, String p, String s, String c) {
		name = n;
		setPassword(pw);
		active = a;
		scheduled = s;
		categorized = c;
	}
	
	public User(String name, String pw) {
		this.name = name;
		setPassword(pw);
		active = "";
		pulled = "";
		scheduled = "";
		categorized = "";
	}
	
	public User () {
		name = "";
		setPassword("");
		active = "";
		pulled = "";
		scheduled = "";
		categorized ="";
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getPulled() {
		return pulled;
	}

	public void setPulled(String pulled) {
		this.pulled = pulled;
	}

	public String getScheduled() {
		return scheduled;
	}

	public void setScheduled(String scheduled) {
		this.scheduled = scheduled;
	}

	public String getCategorized() {
		return categorized;
	}

	public void setCategorized(String categorized) {
		this.categorized = categorized;
	}

	/*
	 * Get Singleton instance of the current user.
	 */
	public static User getCurrentUser() {
		if (instance == null) {
			instance = new User();
		}

		return instance;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void clear() {
		this.name = "";
		this.password = "";
		this.active = "";
		this.pulled = "";
		this.scheduled = "";
		this.categorized = "";
		
	}

}
