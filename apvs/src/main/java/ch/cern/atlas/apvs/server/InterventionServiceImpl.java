package ch.cern.atlas.apvs.server;

import java.sql.SQLException;
import java.util.List;

import ch.cern.atlas.apvs.client.service.InterventionService;
import ch.cern.atlas.apvs.client.service.ServiceException;
import ch.cern.atlas.apvs.client.service.SortOrder;
import ch.cern.atlas.apvs.client.ui.Intervention;

import com.google.gwt.view.client.Range;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class InterventionServiceImpl extends DbServiceImpl implements
		InterventionService {

	@Override
	public int getRowCount() throws ServiceException {
		try {
			return dbHandler.getInterventionCount();
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Intervention> getTableData(Range range, SortOrder[] order)
			throws ServiceException {
		try {
			return dbHandler.getInterventions(range, order);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}
}
