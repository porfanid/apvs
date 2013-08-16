package ch.cern.atlas.apvs.client.service;

import java.util.List;

import ch.cern.atlas.apvs.domain.SortOrder;

public interface TableService<T> {
	long getRowCount() throws ServiceException;

	List<T> getTableData(int start, int length, SortOrder[] order)
			throws ServiceException;
}
