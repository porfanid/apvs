package ch.cern.atlas.apvs.client.service;

import java.util.List;

import com.google.gwt.view.client.Range;

public interface TableService<T> {
	int getRowCount() throws ServiceException;

	List<T> getTableData(Range range, SortOrder[] order)
			throws ServiceException;
}
