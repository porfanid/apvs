package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class Order implements Message, Serializable, IsSerializable {

	private static final long serialVersionUID = 2917315876156737998L;
	
	private String ptuId;
	private String parameter;
	private String value;
	private String type = "Order";

	public Order() {
	}
	
	public Order(String ptuId, String parameter, String value) {
		this.ptuId = ptuId;
		this.parameter = parameter;
		this.value = value;
	}

    @Override
	public String getPtuId() {
		return ptuId;
	}

	public String getParameter() {
		return parameter;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return getType()+" "+getPtuId()+" "+getParameter()+" "+getValue();
	}
}
