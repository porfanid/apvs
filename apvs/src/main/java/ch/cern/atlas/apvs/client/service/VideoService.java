package ch.cern.atlas.apvs.client.service;

import ch.cern.atlas.apvs.domain.Intervention;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsVideo")
public interface VideoService extends RemoteService {

	public void startVideo(Intervention intervention);

	public void stopVideo(Intervention intervention);
}
