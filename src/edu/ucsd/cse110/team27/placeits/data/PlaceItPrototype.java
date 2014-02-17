package edu.ucsd.cse110.team27.placeits.data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.google.android.gms.maps.model.LatLng;

public class PlaceItPrototype extends PlaceIt {

	private int week;

	private int dayOfWeek;

	private int minutes;

	private RepeatMode repeatMode;

	public static final int DAY_WEEK = 0;
	public static final int MINUTES = 1;

	public static enum RepeatMode {
		DAY_WEEK, MINUTES;
	}

	private Date nextScheduledTime;

	public PlaceItPrototype(String title, String description, LatLng location) {
		super(title, description, location);
	}

	public PlaceItPrototype(String title, String description, LatLng latLng,
			int week, int dayOfWeek, int minutes, RepeatMode repeatMode) {
		super(title, description, latLng);

		this.week = week;
		this.dayOfWeek = dayOfWeek;
		this.minutes = minutes;
		this.repeatMode = repeatMode;
		updateNextScheduledTime();
	}

	public PlaceIt load(String line) {
		super.load(line);

		String[] placeitData = line.split(DELIM);
		this.week = Integer.parseInt(placeitData[4]);
		this.dayOfWeek = Integer.parseInt(placeitData[5]);
		this.minutes = Integer.parseInt(placeitData[6]);
		this.repeatMode = RepeatMode.values()[Integer.parseInt(placeitData[7])];
		updateNextScheduledTime();

		return this;
	}

	public static Date nextScheduledTime(Date now, int nextWeek, int nextDay,
			int nextMinutes, RepeatMode repeatMode) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(now);

		if (repeatMode == RepeatMode.DAY_WEEK) {
			calendar.add(Calendar.DATE,
					(nextDay - (calendar.get(Calendar.DAY_OF_WEEK) - 1)) % 7);
			calendar.add(Calendar.DATE, (nextWeek - 1) * 7);		// sunday is 1
		} else if (repeatMode == RepeatMode.MINUTES) {
			calendar.add(Calendar.MINUTE, nextMinutes);
		}

		return calendar.getTime();
	}

	private void updateNextScheduledTime() {
		setNextScheduledTime(nextScheduledTime(new Date(), this.week,
				this.dayOfWeek, this.minutes, this.repeatMode));
	}

	public String toFileString() {
		return super.toFileString() + DELIM + week + DELIM + dayOfWeek + DELIM
				+ minutes + DELIM + (repeatMode == RepeatMode.MINUTES ? MINUTES : DAY_WEEK);
	}

	public PlaceItPrototype() {
		super();
	}

	public void createNew() {
		ActivePlaceIts.getInstance().add(
				new PlaceIt(getTitle(), getDescription(), getLatLng()));
		updateNextScheduledTime();
	}

	public int getWeek() {
		return this.week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public int getDayOfWeek() {
		return this.dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public int getMinutes() {
		return this.minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public RepeatMode getRepeatMode() {
		return this.repeatMode;
	}

	public void setRepeatMode(RepeatMode repeatMode) {
		this.repeatMode = repeatMode;
	}

	public boolean isDue() {
		return getNextScheduledTime().before(new Date());
	}

	public Date getNextScheduledTime() {
		return nextScheduledTime;
	}

	public void setNextScheduledTime(Date nextScheduledTime) {
		this.nextScheduledTime = nextScheduledTime;
	}

}
