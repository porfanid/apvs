package ch.cern.atlas.apvs.client.service;

import java.util.List;

import ch.cern.atlas.apvs.client.AudioException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("apvsAudio")
public interface AudioService extends RemoteService {
	void call(String callerOriginater, String callerDestination);

	void hangup(String channel) throws AudioException;
	
	void hangupMultiple(List<String> channels) throws AudioException;

	void newConference(List<String> participants);

	void addToConference(String callerOriginater, String conferenceRoom);

	void usersList() throws AudioException;
}
