package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import org.asteriskjava.manager.action.SipPeersAction;
import org.asteriskjava.manager.event.ManagerEvent;

import ch.cern.atlas.apvs.client.AudioException;
import ch.cern.atlas.apvs.client.event.AsteriskStatusEvent;
import ch.cern.atlas.apvs.client.event.AudioSettingsChangedEvent;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedEvent;
import ch.cern.atlas.apvs.client.event.ServerSettingsChangedEvent;
import ch.cern.atlas.apvs.client.service.AudioService;
import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;



public class AudioServiceImpl extends ResponsePollService implements AudioService, ManagerEventListener {

	private ManagerConnection managerConnection;
	private AsteriskServer asteriskServer;
	private AudioSettings voipAccounts;
	private List<String> usersList;
	
	private ExecutorService executorService;
	private Future<?> connectFuture;
	
	// Account Details
	private static final String ASTERISK_URL = "pcatlaswpss02.cern.ch";
	private static final String AMI_ACCOUNT = "manager";
	private static final String PASSWORD = "password";
	
	
	private static final String CONTEXT = "internal";
	private static final int PRIORITY = 1;
	private static final int TIMEOUT = 20000;
	
	private RemoteEventBus eventBus;
	
	public AudioServiceImpl(){
		if(eventBus != null)
			return;
		System.out.println("Creating AudioService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
		executorService = Executors.newSingleThreadExecutor();
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		
		//if(audioHandler != null)
			//return;
		
		System.out.println("Starting Audio Service...");
		
		//audioHandler = new AudioHandler(eventBus);
	
		//Local List of the current Users 
		usersList = new ArrayList<String>(); //TODO This can migrate to listOnlineUsers()
		voipAccounts = new AudioSettings();
		
		
		//Asterisk Connection Manager 
		ManagerConnectionFactory factory = new ManagerConnectionFactory(ASTERISK_URL, AMI_ACCOUNT, PASSWORD);
		this.managerConnection = factory.createManagerConnection();
		
		// Eases the communication with asterisk server
		asteriskServer = new DefaultAsteriskServer(managerConnection);
	
		
		// Event handler
		managerConnection.addEventListener(this);
		
		connectFuture = executorService.submit(new Runnable() {
			
			@Override
			public void run() {
				System.err.println("Login in to Asterisk Server on " + ASTERISK_URL.toLowerCase() + " ...");
				try {
					login();
				} catch (AudioException e) {
					e.printStackTrace();
				}
				
			}
		});
		
		AudioSettingsChangedEvent.subscribe(eventBus, new AudioSettingsChangedEvent.Handler() {
			
			@Override
			public void onAudioSettingsChanged(AudioSettingsChangedEvent event) {
				voipAccounts = event.getAudioSettings();
			}
		});
		
	}
	
//*********************************************	
	// Constructor
	
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

//*********************************************	
	// RPC Methods	
	
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
	
	@Override
	public void usersList() throws AudioException {
		usersList.clear();
		try {
			managerConnection.sendAction(new SipPeersAction());
		} catch (IllegalArgumentException e) {
			throw new AudioException(e.getMessage());
		} catch (IllegalStateException e) {
			throw new AudioException(e.getMessage());
		} catch (IOException e) {
			throw new AudioException(e.getMessage());
		} catch (TimeoutException e) {
			throw new AudioException("Timeout: " + e.getMessage());
		}
	}
	
	
//*********************************************	
	// Event Handler
	
	@Override
	public void onManagerEvent(ManagerEvent event) {
		String[] eventContent = event.toString().split("\\[");
		System.out.println("************************Event " + eventContent[0] );
			
		// PeerEntryEvent
		if(eventContent[0].contains("PeerEntryEvent"))
    		listOnlineUsers(eventContent[1]);
		
		// NewChannelEvent    	
		if(eventContent[0].contains("NewChannelEvent")){
	    	newChannelEvent(eventContent[1]);
		}

	    // BridgeEvent
		if(eventContent[0].contains("BridgeEvent"))
	    	bridgeEvent(eventContent[1]);
	    	
		// PeerStatusEvent
		if(eventContent[0].contains("PeerStatusEvent"))
			peerStatusEvent(eventContent[1]);
		
		// HangupEvent
		if(eventContent[0].contains("HangupEvent"))
			hangupEvent(eventContent[1]);
		
		//((RemoteEventBus)eventBus).fireEvent(new AudioSettingsChangedEvent(voipAccounts));
	}
	
	public String contentValue(String content){
		return content.substring(content.indexOf("'",0)+1,content.indexOf("'",content.indexOf("'",0)+1));
	}
	
	
//*********************************************	
	// Event Methods

	
	//*********************************************
	//New Channel
	public void newChannelEvent(String channel){
		List<String> ptuIdList= new ArrayList<String>(voipAccounts.getPtuIds());
		String[] list = channel.replace(',','\n').split("\\n");
		for (int i=0 ; i<list.length; i++){
			if(list[i].contains("channel=")){
				channel=contentValue(list[i]);
				String[] aux = channel.split("-");
				for(int j=0; j<ptuIdList.size(); j++){
					if(voipAccounts.getNumber(ptuIdList.get(j)).equals(aux[0])){
						voipAccounts.setChannel(ptuIdList.get(j),channel);
						break;
					}
				}
				((RemoteEventBus) eventBus).fireEvent(new AudioSettingsChangedEvent(voipAccounts));
				break;
			}			
		}								
	}

	//*********************************************	
	// Users Register and Unregister
	public void peerStatusEvent(String evntContent) {
		String[] list = evntContent.replace(',','\n').split("\\n");
		boolean canRead= false;
		VoipAccount user = new VoipAccount();
		
		for(int i=0 ; i<list.length; i++){
			if(list[i].contains("peer=")){
				String[] number=contentValue(list[i]).split("/");
				user.setNumber(contentValue(list[i]));
				canRead = true;
			}else{ 
				if(canRead==true){
					//TODO If later is desired to support devices other than SIP should be added channel type
					/*
					 * if(list[i].contains("channeltype"))
					 * 		user.setType(contentValue(list[i]));
					 */
					
					if(list[i].contains("peerstatus")){
						if(contentValue(list[i]).equals("Registered")){			
							user.setStatus("Online");
							break;
						}
						if(contentValue(list[i]).equals("Unregistered")){
							user.setStatus("Offline");
							break;
						}else{
							user.setStatus("Unknown");
							break;
						}
					}
				}
			}
							
		}
		String ptuId = voipAccounts.getPtuId(voipAccounts, user.getNumber());
		System.out.println("***********************************PTUID " + ptuId);
		if(ptuId != null){
			voipAccounts.setStatus(ptuId, user.getStatus());
		}
		
		((RemoteEventBus) eventBus).fireEvent(new AudioSettingsChangedEvent(voipAccounts));
	}
	
	//*********************************************
	// List Users Number
	public void listOnlineUsers(String evntContent){
		String[] list = evntContent.replace(',','\n').split("\\n");
				
		for(int i=0; i<list.length; i++){
			if(list[i].contains("objectname")){
				usersList.add("SIP/" + contentValue(list[i]));
				System.out.println("SIP/" + contentValue(list[i]));
			}	
		}
		
		((RemoteEventBus) eventBus).fireEvent(new AsteriskStatusEvent(usersList));
		
	}
	
	//*********************************************
	// Bridge of Call Channels
	
	public void bridgeEvent(String channel) {
			//System.out.println(channel);
		String[] list = channel.replace(',','\n').split("\\n");
		ArrayList<String> usersBridged = new ArrayList<String>();
		List<String> ptuIdList = new ArrayList<String>(voipAccounts.getPtuIds());
		
		for (int i=0 ; i<list.length; i++){
				//System.out.println("ENTROU");
			if(list[i].contains("channel")){
				channel=contentValue(list[i]);
				String[] aux = channel.split("-");
				usersBridged.add(aux[0]);
			}	
		}
		
		//TODO Improve for loop with getIndexOfUsername fuction
		for (int u=0; u<ptuIdList.size(); u++){
			if(usersBridged.contains(voipAccounts.getNumber(ptuIdList.get(u)))){
				for (int b=0; b<usersBridged.size(); b++){
					if(usersBridged.get(b).equals(voipAccounts.getNumber(ptuIdList.get(u))))
						continue;
					else{
						if(voipAccounts.getDestUser(ptuIdList.get(u)).isEmpty()){
							voipAccounts.setDestUser(ptuIdList.get(u), usersBridged.get(b));
								//System.out.println("ENTROU");
						}else{
							voipAccounts.setDestUser(ptuIdList.get(u), voipAccounts.getDestUser(ptuIdList.get(u)) + "," + usersBridged.get(b));
								//System.out.println("ENTROU2");
						}
					}
				}	
			}
			
		}
			
			//System.out.println(getIndexOfUsername(aux[0]));
			//usersList.get(getIndexOfUsername(aux[0])).setActiveCallChannel(channel);
			//System.out.println(usersList.get(getIndexOfUsername(aux[0])).getActiveCallChannel());
		((RemoteEventBus)eventBus).fireEvent(new AudioSettingsChangedEvent(voipAccounts));
	}
	
	
	//*********************************************
	// Hangup Call Event
	public void hangupEvent(String channel){
		List<String> ptuIdList= new ArrayList<String>(voipAccounts.getPtuIds());
		String[] list = channel.replace(',','\n').split("\\n");
		for (int i=0 ; i<list.length; i++){
			if(list[i].contains("channel=")){
				channel=contentValue(list[i]);
				String[] aux = channel.split("-");
				for(int u=0; u<ptuIdList.size(); u++){
					if(voipAccounts.getNumber(ptuIdList.get(u)).equals(aux[0])){
						voipAccounts.setChannel(ptuIdList.get(u),"");
						voipAccounts.setDestUser(ptuIdList.get(u),"");
						break;	
					}
				}
			}			
		}
		((RemoteEventBus)eventBus).fireEvent(new AudioSettingsChangedEvent(voipAccounts));

	}
	
	
}
