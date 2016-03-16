package es.ubiqua.citaio.model;

public class UserData {
	
	private Room room;
	private User user;
	
	public UserData(){
		
	}

	public Room getRoom() {
		return room;
	}

	public User getUser() {
		return user;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
