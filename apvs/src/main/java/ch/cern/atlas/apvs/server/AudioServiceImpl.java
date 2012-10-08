package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;


import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.DefaultAsteriskServer;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.ManagerEventListener; 
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.HangupAction;
import org.asteriskjava.manager.event.ManagerEvent;

import ch.cern.atlas.apvs.client.AudioException;
import ch.cern.atlas.apvs.client.event.AudioSettingsChangedEvent;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedEvent;
import ch.cern.atlas.apvs.client.service.AudioService;
import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;



public class AudioServiceImpl extends ResponsePollService implements AudioService, ManagerEventListener {

	private ManagerConnection managerConnection;
	private AsteriskServer asteriskServer;
	private AudioSettings voipAccounts;
	
	private static final String CONTEXT = "internal";
	private static final int PRIORITY = 1;
	private static final int TIMEOUT = 20000;
	
	private RemoteEventBus eventBus;
	
	// Account Details
	private static final String ASTERISK_SERVER = "pcatlaswpss02.cern.ch";
	private static final String AMI_ACCOUNT = "manager";
	private static final String PASSWORD = "password";
	
	public AudioServiceImpl(){
		System.out.println("Creating AudioService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		
		System.err.println("Starting Audio Service...");
		
		//Local List of the current Users
		voipAccounts = new AudioSettings();
		
		//Asterisk Connection Manager 
		ManagerConnectionFactory factory = new ManagerConnectionFactory(ASTERISK_SERVER, AMI_ACCOUNT, PASSWORD);
		this.managerConnection = factory.createManagerConnection();
		
		// Eases the communication with asterisk server
		asteriskServer = new DefaultAsteriskServer(managerConnection);
		
		// Event handler
		managerConnection.addEventListener(this);
		
		System.err.println("Login in to Asterisk Server on " + ASTERISK_SERVER.toLowerCase() + " ...");
		try {
			this.login();
		} catch (AudioException e) {
			e.printStackTrace();
		}
		
		voipAccounts.add("Alexandre1");
		voipAccounts.add("Mark1");
		
	}
	
	public void login() throws AudioException{
		try{
			managerConnection.login();
		}catch (IllegalStateException e){
			throw new AudioException(e.getMessage());
		}catch (IOException e){
			throw new AudioException(e.getMessage());
		}catch (AuthenticationFailedException e) {
			throw new AudioException("Failed login to Asterisk Manager: " + e.getMessage());
		}catch (TimeoutException e) {
			throw new AudioException("Login to Asterisk Timeout: " + e.getMessage());
		}
	}
	
	@Override
	public void call(String callerOriginater, String callerDestination) {
		asteriskServer.originateToExtension(callerOriginater, CONTEXT, callerDestination, PRIORITY, TIMEOUT);
	}

	@Override
	public void hangup(String channel) throws AudioException {
		HangupAction hangupCall = new HangupAction(channel);
		try{
			managerConnection.sendAction(hangupCall);
		}catch (IllegalArgumentException e){
			throw new AudioException(e.getMessage());
		}catch (IllegalStateException e){
			throw new AudioException(e.getMessage());
		}catch (IOException e){
			throw new AudioException(e.getMessage());
		}catch (TimeoutException e){
			throw new AudioException("Timeout: " + e.getMessage());
		}
	}
	
	
	// Event Handler
	@Override
	public void onManagerEvent(ManagerEvent event) {
		String[] eventContent = event.toString().split("\\[");
		System.err.println("Event " + eventContent[0] );
				
		// NewChannelEvent    	
		if(eventContent[0].contains("NewChannelEvent")){
	    	newChannelEvent(eventContent[1]);
		}

	    	
	    // BridgeEvent
		if(eventContent[0].contains("BridgeEvent"))
	    	;//bridgeEvent(eventContent[1]);
	    	
		// PeerStatusEvent
		if(eventContent[0].contains("PeerStatusEvent"))
			;//peerStatusEvent(eventContent[1]);
		
		// HangupEvent
		if(eventContent[0].contains("HangupEvent"))
			;//hangupEvent(eventContent[1]);
		
		((RemoteEventBus)eventBus).fireEvent(new AudioSettingsChangedEvent(voipAccounts));
	}
	
	public String contentValue(String content){
		return content.substring(content.indexOf("'",0)+1,content.indexOf("'",content.indexOf("'",0)+1));
	}
	
	//New Channel Event
	public void newChannelEvent(String channel){
		//System.err.println(channel);
		String[] list = channel.replace(',','\n').split("\\n");
		for (int i=0 ; i<list.length; i++){
			//System.err.println("ENTROU");
			if(list[i].contains("channel=")){
				channel=contentValue(list[i]);
				String[] aux = channel.split("-");
				voipAccounts.setChannel("Alexandre1", aux[1]);
				voipAccounts.setChannel("Mark1", aux[0]);
				//TODO
				/*
				 * 
				 * Update channel to voipAccount list
				 * 
				 */
				//usersList.get(getIndexOfUsername(aux[0])).setActiveCallChannel(channel);
				//System.out.println(usersList.get(getIndexOfUsername(aux[0])).getActiveCallChannel());
				break;
			}			
		}								
	}

	
	
}
