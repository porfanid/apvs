package ch.cern.atlas.apvs.client.service;

import java.util.List;

public interface TableService<T> {
	int getRowCount() throws ServiceException;

	List<T> getTableData(int start, int length, SortOrder[] order)
			throws ServiceException;
}
