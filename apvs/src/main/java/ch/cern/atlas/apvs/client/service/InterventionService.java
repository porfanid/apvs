package ch.cern.atlas.apvs.client.service;

import ch.cern.atlas.apvs.client.ui.Intervention;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsIntervention")
public interface InterventionService extends TableService<Intervention>, RemoteService {

	void addDevice(String ptuId);

	void addUser(String fname, String lname);

	void addIntervention(int i, int j, String string);

}
