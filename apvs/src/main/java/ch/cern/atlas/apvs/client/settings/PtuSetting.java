package ch.cern.atlas.apvs.client.settings;

import java.io.Serializable;

public class PtuSetting implements Serializable, Comparable<PtuSetting> {

	private static final long serialVersionUID = 8780484883060796134L;

	private int ptuId;
	private String name;
	private int dosimeterSerialNumber;
	private boolean enabled;

	private String helmetUrl;

	private String handUrl;
	
	public PtuSetting(int ptuId) {
		this.ptuId = ptuId;
		this.name = "";
		this.dosimeterSerialNumber = 0;
		this.enabled = true;
		this.helmetUrl = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
		this.handUrl = "http://quicktime.tc.columbia.edu/users/lrf10/movies/sixties.mov";
	}

	public int getPtuId() {
		return ptuId;
	}

	public String getName() {
		return name;
	}

	public Integer getDosimeterSerialNumber() {
		return dosimeterSerialNumber;
	}

	public Boolean isEnabled() {
		return enabled;
	}

	public String getHelmetUrl() {
		return helmetUrl;
	}

	public String getHandUrl() {
		return handUrl;
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
