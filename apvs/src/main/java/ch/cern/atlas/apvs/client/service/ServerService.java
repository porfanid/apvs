package ch.cern.atlas.apvs.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsServer")
public interface ServerService extends RemoteService {
	public void isAlive();
	public boolean isReady(String supervisorPassword);
	public void setPassword(String name, String password);
}
