package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.service.ServerService;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class ServerServiceImpl extends ResponsePollService implements
		ServerService {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private RemoteEventBus eventBus;
	private ServerSettingsStorage serverSettingsStorage;
	private User user = null;
	private Map<String, String> headers = new HashMap<String, String>();

	public ServerServiceImpl() {
		log.info("Creating ServerService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
		serverSettingsStorage = ServerSettingsStorage.getInstance(eventBus);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		log.info("Starting ServerService...");
	}

	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	protected String readContent(HttpServletRequest request)
			throws ServletException, IOException {
		headers.clear();
		for (@SuppressWarnings("unchecked")
		Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements(); ) {
			String key = e.nextElement();
			headers.put(key, request.getHeader(key));
		}
				
		return super.readContent(request);
	}
	
	@Override
	public boolean isReady() {
		return true;
	}
	
	@Override
	public boolean isSecure() {
		return getHeader("HTTPS", "").equalsIgnoreCase("on");
	}
		
	@Override
	public User getUser(String supervisorPassword) {
		if (user != null) {
			return user;
		}
		
		if (isSecure()) {
						
			String fullName = getHeader("ADFS_FULLNAME", "Unknown Person");
			String email = getHeader("REMOTE_USER", "");
			boolean isSupervisor = false;
			try {
				isSupervisor = EgroupCheck.check("atlas-upgrade-web-atwss-supervisors", email);
			} catch (Exception e) {
				log.warn("Could not read e-group", e);
			} 
		
			user = new User(fullName, email, isSupervisor);
		} else {
			// not secure, we use simple plain password to become supervisor
			String pwd = getEnv("APVSpwd", null);
			boolean isSupervisor = false;
			if (pwd == null) {
				log.warn("NO Supervisor Password set!!! Set enviroment variable 'APVSpwd'");
			} else {
				System.err.println("***** "+supervisorPassword+" "+pwd);
				isSupervisor = (supervisorPassword != null) && supervisorPassword.equals(pwd);
			}
			user = new User("Unknown Person", "", isSupervisor);
		}
		System.err.println("User: "+user);			
		return user;
	}
		
	@Override
	public void setPassword(String name, String password) {
		serverSettingsStorage.setPassword(name, password);
	}
	
	private String getEnv(String env, String defaultValue) {
		String value = System.getenv(env);
		return value != null ? value : defaultValue;
	}
	
	private String getHeader(String env, String defaultValue) {
		String value = headers.get(env);
		return value != null ? value : defaultValue;
	}
}
