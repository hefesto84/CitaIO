package es.ubiqua.citaio.model;

public class User {
	
	private int id;
	private String auth_token;
	private int m;
	private boolean is_host;
	
	public User(){
		
	}

	public int getId() {
		return id;
	}

	public String getAuth_token() {
		return auth_token;
	}

	public int getM() {
		return m;
	}

	public boolean isIs_host() {
		return is_host;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAuth_token(String auth_token) {
		this.auth_token = auth_token;
	}

	public void setM(int m) {
		this.m = m;
	}

	public void setIs_host(boolean is_host) {
		this.is_host = is_host;
	}
}
