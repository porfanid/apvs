package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
@Entity
@Table( name = "TBL_INSPECTIONS" )
public class Intervention implements Serializable, IsSerializable {

	private static final long serialVersionUID = 2578285814293336298L;

	@NotNull
	private User user;
	@NotNull
	private Device device;
	
	private int id;
	
	private Date startTime;
	private Date endTime;
	private String impactNumber;
	private Double recStatus;
	private String description;
	private Boolean test;

	protected Intervention() {
	}
	
	public Intervention(User user, Device device, Date startTime,
			String impactNumber, Double recStatus, String description, Boolean test) {
		setUser(user);
		setDevice(device);
		setStartTime(startTime);
		setEndTime(null);
		setImpactNumber(impactNumber);
		setRecStatus(recStatus);
		setDescription(description);
		setTest(test);
	}
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name = "ID", length=15)
	public int getId() {
		return id;
	}
	
	@SuppressWarnings("unused")
	private void setId(int id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="USER_ID", nullable=false)
	@Cascade({CascadeType.SAVE_UPDATE})
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@ManyToOne
	@JoinColumn(name="DEVICE_ID", nullable=false)
	@Cascade({CascadeType.SAVE_UPDATE})
	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
	
	@Transient
	public String getName() {		
		return user.getDisplayName();
	}

	@Transient
	public String getPtuId() {
		return device.getName();
	}
	
	@Column(name = "IMPACT_NUM", length=50)
	public String getImpactNumber() {
		return impactNumber;
	}
	
	public void setImpactNumber(String impactNumber) {
		this.impactNumber = impactNumber;
	}
	
	// FIXME, check if this is correct
	@Column(name = "REC_STATUS")
	public Double getRecStatus() {
		return recStatus;
	}
	
	public void setRecStatus(Double recStatus) {
		this.recStatus = recStatus;
	}

	@Column(name = "DSCR", length=500)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "STARTTIME", nullable=false)
	public Date getStartTime() {
		return startTime;
	}
	
	private void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ENDTIME")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public Boolean isTest() {
		return test;
	}
	
	@Type(type="yes_no")
	@Column(name = "TEST", length=1)
	public void setTest(Boolean test) {
		this.test = test;
	}

	@Override
	public String toString() {
		return "Intervention [user=" + user + ", device=" + device + ", id="
				+ id + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", impactNumber=" + impactNumber + ", recStatus=" + recStatus
				+ ", description=" + description + ", test=" + test + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((device == null) ? 0 : device.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((impactNumber == null) ? 0 : impactNumber.hashCode());
		result = prime * result
				+ ((recStatus == null) ? 0 : recStatus.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((test == null) ? 0 : test.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Intervention other = (Intervention) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (device == null) {
			if (other.device != null) {
				return false;
			}
		} else if (!device.equals(other.device)) {
			return false;
		}
		if (endTime == null) {
			if (other.endTime != null) {
				return false;
			}
		} else if (!endTime.equals(other.endTime)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (impactNumber == null) {
			if (other.impactNumber != null) {
				return false;
			}
		} else if (!impactNumber.equals(other.impactNumber)) {
			return false;
		}
		if (recStatus == null) {
			if (other.recStatus != null) {
				return false;
			}
		} else if (!recStatus.equals(other.recStatus)) {
			return false;
		}
		if (startTime == null) {
			if (other.startTime != null) {
				return false;
			}
		} else if (!startTime.equals(other.startTime)) {
			return false;
		}
		if (test == null) {
			if (other.test != null) {
				return false;
			}
		} else if (!test.equals(other.test)) {
			return false;
		}
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.user)) {
			return false;
		}
		return true;
	}
	

}
