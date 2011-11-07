package org.atmosphere.samples.client;

import java.io.Serializable;

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

