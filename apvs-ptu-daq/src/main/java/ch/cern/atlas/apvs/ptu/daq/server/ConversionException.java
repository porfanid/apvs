package ch.cern.atlas.apvs.ptu.daq.server;

public class ConversionException extends Exception {

	private static final long serialVersionUID = 7983962855788119297L;

	public ConversionException(String msg) {
		super("PTU Cannot convert: "+msg);
	}
}
