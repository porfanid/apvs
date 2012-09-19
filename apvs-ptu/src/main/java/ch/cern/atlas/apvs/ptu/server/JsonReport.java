package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Message;
import ch.cern.atlas.apvs.domain.Report;

public class JsonReport implements JsonMessage {
	
	String type;
	String batteryLevel;
	Date time;
	String cameraHandheld;
	String cameraHelmet;
	String audio;
	
	public JsonReport(Message message) {
		Report report = (Report)message;
		type = report.getType();
		time = report.getDate();
		batteryLevel = report.getBatteryLevel().toString();
		cameraHandheld = report.getCameraHandheld() ? "1" : "0";
		cameraHelmet = report.getCameraHelmet() ? "1" : "0";
		audio = report.getAudio() ? "1" : "0";
	}
}
