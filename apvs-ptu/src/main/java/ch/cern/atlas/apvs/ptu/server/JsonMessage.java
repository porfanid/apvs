package ch.cern.atlas.apvs.ptu.server;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.Error;
import ch.cern.atlas.apvs.domain.Event;
import ch.cern.atlas.apvs.domain.GeneralConfiguration;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Message;
import ch.cern.atlas.apvs.domain.Order;
import ch.cern.atlas.apvs.domain.Report;
import ch.cern.atlas.apvs.domain.SensorOrder;

import com.cedarsoftware.util.io.JsonObject;

public class JsonMessage {

	private static Logger log = LoggerFactory.getLogger(JsonMessage.class
			.getName());

	protected JsonObject<String, Object> msg;

	public JsonMessage(JsonObject<String, Object> msg) {
		this.msg = msg;
	}

	// FIXME add proper conversions (Date, Int, Double)
	public JsonMessage(Message message) {
		msg = new JsonObject<String, Object>();
		if (message instanceof Measurement) {
			Measurement m = (Measurement) message;
			msg.put("Type", m.getType());
			msg.put("Sensor", m.getSensor());
			msg.put("Unit", m.getUnit());
			msg.put("Value", m.getValue().toString());
			msg.put("Time", m.getDate());
			msg.put("SamplingRate", m.getSamplingRate());
			msg.put("HighLimit", m.getHighLimit());
			msg.put("LowLimit", m.getLowLimit());
		} else if (message instanceof Event) {
			Event m = (Event) message;
			msg.put("Type", m.getType());
			msg.put("Value", m.getValue());
			msg.put("Threshold", m.getThreshold());
			msg.put("Unit", m.getUnit());
			msg.put("Time", m.getDate());
		} else if (message instanceof Error) {
			Error m = (Error) message;
			msg.put("Type", m.getType());
			msg.put("Description", m.getDescription());
			msg.put("Criticality", m.getCriticality());
			msg.put("Time", m.getDate());
		} else if (message instanceof GeneralConfiguration) {
			GeneralConfiguration m = (GeneralConfiguration) message;
			msg.put("Type", m.getType());
			msg.put("DosimeterId", m.getDosimeterId());
		} else if (message instanceof SensorOrder) {
			SensorOrder m = (SensorOrder) message;
			msg.put("Type", m.getType());
			msg.put("Name", m.getName());
			msg.put("Parameter", m.getParameter());
			msg.put("Value", m.getValue());
		} else if (message instanceof Order) {
			Order m = (Order) message;
			msg.put("Type", m.getType());
			msg.put("Parameter", m.getParameter());
			msg.put("Value", m.getValue());
		} else if (message instanceof Report) {
			Report m = (Report) message;
			msg.put("Type", m.getType());
			msg.put("BatteryLevel", m.getBatteryLevel());
			msg.put("CameraHandheld", m.getCameraHandheld());
			msg.put("CameraHelmet", m.getCameraHelmet());
			msg.put("Audio", m.getAudio());
			msg.put("Time", m.getDate());
		} else {
			throw new RuntimeException("Cannot find JsonMessage for '"
					+ message + "'");
		}
	}

	public Message toMessage(Device device) {
		String type = (String) msg.get("Type");
		if (type.equals("Measurement")) {
			String sensor = getString("Sensor");
			String unit = getString("Unit");
			Double value = getDouble("Value");
			Date time = getDate("Time");
			Integer samplingRate = getInteger("SamplingRate");

			// fix for #486 and #490
			if ((sensor == null) || (value == null) || (unit == null)
					|| (time == null)) {
				log.warn("PTU "
						+ device.getName()
						+ ": Measurement contains <null> sensor, value, samplingrate, unit or time ("
						+ sensor + ", " + value + ", " + unit + ", "
						+ samplingRate + ", " + time + ")");
				return null;
			}

			return new Measurement(device, sensor, value,
					getDouble("LowLimit"), getDouble("HighLimit"), unit,
					samplingRate, time);
		} else if (type.equals("Event")) {
			return new Event(device, getString("Sensor"),
					getString("EventType"), getDouble("Value"),
					getDouble("Threshold"), getString("Unit"), getDate("Time"));
		} else if (type.equals("Error")) {
			return new Error(device, getInteger("ErrNo"),
					getString("Description"), getString("Criticality"),
					getDate("Time"));
		} else if (type.equals("GeneralConfiguration")) {
			return new GeneralConfiguration(device, getString("DosimeterId"));
		} else if (type.equals("SensorOrder")) {
			return new SensorOrder(device, getString("Name"),
					getString("Parameter"), getString("Value"));
		} else if (type.equals("Order")) {
			return new Order(device, getString("Parameter"), getString("Value"));
		} else if (type.equals("Report")) {
			return new Report(device, getDouble("BatteryLevel"),
					getBoolean("CameraHandheld"), getBoolean("CameraHelmet"),
					getBoolean("Audio"), getDate("Time"));
		}

		return null;
	}

	private String getString(String key) {
		return msg.get(key).toString();
	}

	private Double getDouble(String key) {
		return toDouble(msg.get(key));
	}

	private Integer getInteger(String key) {
		return toInteger(msg.get(key));
	}

	private Boolean getBoolean(String key) {
		return toBoolean(msg.get(key));
	}

	private Date getDate(String key) {
		return toDate(msg.get(key));
	}

	public static Double toDouble(Object number) {
		if ((number == null) || !(number instanceof String)) {
			return null;
		}
		try {
			return Double.parseDouble((String) number);
		} catch (NumberFormatException e) {
			log.warn("NumberFormatException " + number + " " + e);
			return null;
		}
	}

	public static Integer toInteger(Object number) {
		if ((number == null) || !(number instanceof String)) {
			return null;
		}
		try {
			return Integer.parseInt((String) number);
		} catch (NumberFormatException e) {
			log.warn("NumberFormatException " + number + " " + e);
			return null;
		}
	}

	public static boolean toBoolean(Object state) {
		if ((state == null) || !(state instanceof String)) {
			return false;
		}
		try {
			String b = (String) state;
			b = b.equals("1") ? "true" : b.equals("0") ? "false" : b;
			return Boolean.parseBoolean((String) state);
		} catch (NumberFormatException e) {
			log.warn("NumberFormatException " + state + " " + e);
			return false;
		}
	}

	public static Date toDate(Object date) {
		try {
			return PtuServerConstants.dateFormat.parse((String) date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public String toString() {
		return msg.toString();
	}

	@SuppressWarnings("unused")
	private String limit(String value, int length) {
		StringBuilder buf = new StringBuilder(value);
		if (buf.length() > length) {
			buf.setLength(length);
		}

		return buf.toString();
	}
}
