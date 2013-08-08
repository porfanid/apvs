package ch.cern.atlas.apvs.client.service;

import java.io.Serializable;

import ch.cern.atlas.apvs.client.settings.Proxy;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsServer")
public interface ServerService extends RemoteService {
	
	public class User implements Serializable, IsSerializable {
		private static final long serialVersionUID = -563202736116953055L;

		private String fullName;
		private String email;
		private boolean supervisor;
		
		public User() {
			// default constructor to be serializable
		}
		
		public User(String fullName, String email, boolean supervisor) {
			this.fullName = fullName;
			this.email = email;
			this.supervisor = supervisor;
		}

		public String getFullName() {
			return fullName;
		}

		public String getEmail() {
			return email;
		}

		public boolean isSupervisor() {
			return supervisor;
		}
		
		public String toString() {
			return getFullName()+" <"+getEmail()+"> "+(isSupervisor() ? "Supervisor" : "Observer");
		}
	}
	
	public boolean isReady();
	
	public Proxy getProxy();

	public User login(String pwd);
	
	public User getUser();
		
	public void setPassword(String name, String password);
}
