package ch.cern.atlas.apvs.client.service;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("audioService")
public interface AudioService extends RemoteService{
	
	void login();
	void logoff();
	void call(String callerOriginater, String callerDestination);
	void hangup(String channel);
}
