package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import oracle.net.aso.e;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RPCRequest;

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

	public ServerServiceImpl() {
		log.info("Creating ServerService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
		serverSettingsStorage = ServerSettingsStorage.getInstance(eventBus);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		log.info("Starting ServerService...");

		// FIXME, move this back to AudioSettings as ServerSettingsStorage is now created in ServerFactory
		//AudioUsersSettingsStorage.getInstance(eventBus);
		//AudioSupervisorSettingsStorage.getInstance(eventBus);
	}

	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	public boolean isReady() {
		return true;
	}
	
	@Override
	public boolean isSecure() {
		return true;
	}
	
	@Override
	protected String readContent(HttpServletRequest request)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.err.println(request.getAuthType()+" "+request.getRequestURI()+" "+request.getContextPath()+" "+request.getRemoteUser());
		
		System.err.println("Headers");
		for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements(); ) {
			String key = e.nextElement();
			System.err.println("   "+key+" "+request.getHeader(key));
		}
		
		System.err.println("Attributes");
		for (Enumeration<String> e = request.getAttributeNames(); e.hasMoreElements(); ) {
			String key = e.nextElement();
			System.err.println("   "+key+" "+request.getAttribute(key));
		}
		
		return super.readContent(request);
	}
	
	@Override
	public User getUser(String supervisorPassword) {
		if (user != null) {
			return user;
		}
		
		if (isSecure()) {
			
			for (Iterator<Entry<String, String>> i = System.getenv().entrySet().iterator(); i.hasNext(); ) {
				Entry<String, String> entry = i.next();
				System.err.println("'"+entry.getKey()+"'='"+entry.getValue()+"'");
			}
			
			String fullName = getEnv("ADFS_FULLNAME", "Unknown Person");
			String email = getEnv("REMOTE_USER", "");
			boolean isSupervisor = false;
			try {
				isSupervisor = EgroupCheck.check("atlas-upgrade-web-atwss-supervisors", email);
			} catch (Exception e) {
				log.warn("Could not read e-group", e);
			} 
		
			user = new User(fullName, email, isSupervisor);
		} else {
			// not secure, we use simple plain password to become supervisor
			String pwd = System.getenv("APVSpwd");
			boolean isSupervisor = false;
			if (pwd == null) {
				log.warn("NO Supervisor Password set!!! Set enviroment variable 'APVSpwd'");
			} else {
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
}
