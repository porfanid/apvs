package ch.cern.atlas.apvs.client.ui;

import java.io.Serializable;
import java.util.Date;

public class Intervention implements Serializable {

	private static final long serialVersionUID = 2578285814293336298L;

	private int id;
	private String fname;
	private String lname;
	private String ptuId;
	private Date startTime;
	private Date endTime;
	private String description;

	public Intervention() {
	}
	
	public Intervention(int id, String fname, String lname, String ptuId, Date startTime,
			Date endTime, String description) {
		this.id = id;
		this.fname = fname;
		this.lname = lname;
		this.ptuId = ptuId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
	}

	public String getName() {
		return fname+" "+lname;
	}

	public String getPtuId() {
		return ptuId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Intervention) {
			return getId() == ((Intervention)obj).getId();
		}
		return super.equals(obj);
	}
}
