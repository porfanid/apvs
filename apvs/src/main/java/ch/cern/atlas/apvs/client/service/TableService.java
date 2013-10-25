package ch.cern.atlas.apvs.client.service;

import java.util.List;

import ch.cern.atlas.apvs.domain.SortOrder;

public interface TableService<T> {
	long getRowCount() throws ServiceException;

	List<T> getTableData(Integer start, Integer length, List<SortOrder> order)
			throws ServiceException;
}
