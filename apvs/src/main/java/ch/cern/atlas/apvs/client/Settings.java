package ch.cern.atlas.apvs.client;

import java.util.Map;


public interface Settings {

	public void setMap(Map<String, Map<String, String>> map);

	public Map<String, Map<String, String>> getMap();
	
	public void put(int id, String name, String value);
	
	public String get(int id, String name);
	
	public int size();
	
	public String debugString();
}
