package ch.cern.atlas.apvs.client.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.cern.atlas.apvs.client.AudioException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("apvsAudio")
public interface AudioService extends RemoteService{	
	void call(String callerOriginater, String callerDestination);
	void hangup(String channel) throws AudioException;
	void usersList() throws AudioException;
}
