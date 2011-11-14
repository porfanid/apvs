package ch.cern.atlas.apvs.client.places;


public class User extends RemotePlace {

	private static final long serialVersionUID = -3380084091303584375L;
	private int id;
	private String user;
	
	public User() {
	}
	
	public User(int id, String user) {
		this.id = id;
		this.user = user;
	}
	
	public String getUser() {
		return user;
	}
	
	public String toString() {
		return id + " User: "+getUser();
	}
}
