package ch.cern.atlas.apvs.client.service;

import ch.cern.atlas.apvs.domain.Ptu;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsPtu")
public interface PtuService extends RemoteService {
	public Ptu getPtu(int ptuId);
}
