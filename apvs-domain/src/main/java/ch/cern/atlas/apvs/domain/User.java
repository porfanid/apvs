package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
@Entity
@Table( name = "TBL_USERS" )
public class User implements Serializable, IsSerializable {

	private static final long serialVersionUID = -1423459851493629177L;

	private int id;
	
	@NotNull
	@Size(min = 1)
	private String firstName;
	
	@NotNull
	@Size(min = 4)
	private String lastName;
	
	private String cernId;
	
	public User() {
		// Serializable
	}

	public User(String firstName, String lastName, String cernId) {
		setFirstName(firstName);
		setLastName(lastName);
		setCernId(cernId);
	}
		
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name = "ID", length=3)
	public int getId() {
		return id;
	}
	
	@SuppressWarnings("unused")
	private void setId(int id) {
		this.id = id;
	}

	@Column(name = "FNAME", length=30, nullable=false)
	public String getFirstName() {
		return firstName;
	}
	
	private void setFirstName(String firstName) {
		this.firstName = firstName;		
	}

	@Column(name = "LNAME", length=30, nullable=false)
	public String getLastName() {
		return lastName;
	}
	
	private void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "CERN_ID", length=20)
	public String getCernId() {
		return cernId;
	}

	private void setCernId(String cernId) {
		this.cernId = cernId;
	}

	@Transient
	public String getDisplayName() {
		if (((firstName == null) || firstName.equals("")) && ((lastName == null) || lastName.equals(""))) {
			return "";
		}
		
		if ((firstName == null) || firstName.equals("")) {
			return lastName;
		}
		
		if ((lastName == null) || lastName.equals("")) {
			return firstName;
		}
		
		return firstName+" "+lastName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cernId == null) ? 0 : cernId.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName="
				+ lastName + ", cernId=" + cernId + "]";
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
		User other = (User) obj;
		if (cernId == null) {
			if (other.cernId != null) {
				return false;
			}
		} else if (!cernId.equals(other.cernId)) {
			return false;
		}
		if (firstName == null) {
			if (other.firstName != null) {
				return false;
			}
		} else if (!firstName.equals(other.firstName)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (lastName == null) {
			if (other.lastName != null) {
				return false;
			}
		} else if (!lastName.equals(other.lastName)) {
			return false;
		}
		return true;
	}
	
	
}
