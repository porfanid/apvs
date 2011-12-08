package ch.cern.atlas.apvs.client;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author duns
 *
 */
public interface Settings {

	public void setList(List<Map<String, String>> list);

	public List<Map<String, String>> getList();
	
	public void put(int index, String name, String value);
	
	public String get(int index, String name);
	
	public void remove(int id);

	public int size();
	
	public String debugString();
}
