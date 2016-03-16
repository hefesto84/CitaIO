package es.ubiqua.citaio.model;

import java.util.List;

public class Room {
	
	private int id;
	private String permalink;
	private String tokbox_api_key;
	private String tokbox_token;
	private String tokbox_session_id;
	private List<Attendees> attendees;
	
	public Room(){
		
	}

	public int getId() {
		return id;
	}

	public String getPermalink() {
		return permalink;
	}

	public String getTokbox_api_key() {
		return tokbox_api_key;
	}

	public String getTokbox_token() {
		return tokbox_token;
	}

	public String getTokbox_session_id() {
		return tokbox_session_id;
	}

	public List<Attendees> getAttendees() {
		return attendees;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public void setTokbox_api_key(String tokbox_api_key) {
		this.tokbox_api_key = tokbox_api_key;
	}

	public void setTokbox_token(String tokbox_token) {
		this.tokbox_token = tokbox_token;
	}

	public void setTokbox_session_id(String tokbox_session_id) {
		this.tokbox_session_id = tokbox_session_id;
	}

	public void setAttendees(List<Attendees> attendees) {
		this.attendees = attendees;
	}
}
