package ch.cern.atlas.apvs.client.settings;

import java.io.Serializable;

public class PtuSetting implements Serializable, Comparable<PtuSetting> {

	private static final long serialVersionUID = 8780484883060796134L;

	private int ptuId;
	private String name;
	
	public int getPtuId() {
		return ptuId;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(PtuSetting o) {
		if (this == o) {
			return 0;
		}

		return (o != null) ? getPtuId() < o.getPtuId() ? -1
				: getPtuId() == o.getPtuId() ? 0 : 1 : 1;
	}

}
