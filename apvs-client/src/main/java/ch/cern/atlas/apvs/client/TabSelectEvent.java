package ch.cern.atlas.apvs.client;

import java.io.Serializable;

/**
 * 
 * @author Mark Donszelmann
 */
public class TabSelectEvent implements Serializable {

	private static final long serialVersionUID = 8644054323858503049L;
	private int tabNo;
	
	public TabSelectEvent() {
	}
	
	public TabSelectEvent(int tabNo) {
		this.tabNo = tabNo;
	}
	
	public int getTabNo() {
		return tabNo;
	}
}

