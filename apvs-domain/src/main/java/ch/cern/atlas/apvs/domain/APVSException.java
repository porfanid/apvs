package ch.cern.atlas.apvs.domain;

import java.io.IOException;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class APVSException extends IOException implements IsSerializable {

	private static final long serialVersionUID = 3520019113105842483L;

	protected APVSException() {
	}

	public APVSException(String msg) {
		super(msg);
	}

	public APVSException(Throwable t) {
		super(t);
	}
	
	public APVSException(String msg, Throwable t) {
		super(msg, t);
	}
}
