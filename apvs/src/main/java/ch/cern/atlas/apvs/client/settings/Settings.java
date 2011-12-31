package ch.cern.atlas.apvs.client.settings;

public interface Settings {
	
	public final static String DEFAULT_SUPERVISOR = "duns";
	
	public void setSupervisorName(String name);

	public String getSupervisorName();
}
