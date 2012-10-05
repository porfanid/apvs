package ch.cern.atlas.apvs.client.service;

import java.util.List;

import ch.cern.atlas.apvs.client.ui.Intervention;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.view.client.Range;

/**
 * @author Mark Donszelmann
 */
@RemoteServiceRelativePath("apvsDb")
public interface DbService extends RemoteService {
	List<Intervention> getInterventions(Range range, SortOrder[] order) throws ServiceException;
}
