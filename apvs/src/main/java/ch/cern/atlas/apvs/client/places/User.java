package ch.cern.atlas.apvs.client.places;


public class User extends RemotePlace {

	private static final long serialVersionUID = -3380084091303584375L;
	private String user;
	
	public User() {
	}
	
	public User(int remoteId, String user) {
		super(remoteId);
		this.user = user;
	}
	
	public String getUser() {
		return user;
	}
	
	public String toString() {
		return super.toString() + " User: "+getUser();
	}
}
