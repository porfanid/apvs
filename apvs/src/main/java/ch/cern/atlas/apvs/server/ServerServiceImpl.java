package ch.cern.atlas.apvs.server;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.service.ServerService;
import ch.cern.atlas.apvs.client.settings.Proxy;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class ServerServiceImpl extends ResponsePollService implements
		ServerService {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	public ServerServiceImpl() {
		log.info("Creating ServerService...");
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
	public boolean isReady() {
		return true;
	}
	
	@Override
	public Proxy getProxy() { 
		boolean secure = getHeader("HTTPS", "").equalsIgnoreCase("on");
		
		String proxyFile = "httpd-proxy.conf";
		Proxy proxy = new Proxy(secure, "https://atwss.cern.ch");
		try {
			proxy = ProxyConf.load(new FileInputStream(proxyFile), proxy);
			log.info(proxy.toString());
		} catch (IOException e) {
			log.warn("Cannot read '"+proxyFile+"'");
		}
		
		return proxy;
	}
		
	@Override
	public User login(String supervisorPassword) {
		HttpSession session = getThreadLocalRequest().getSession(true);
		boolean secure = getHeader("HTTPS", "").equalsIgnoreCase("on");
		
		User user;
		Boolean isSupervisor = false;
		if (secure) {
						
			String fullName = getHeader("ADFS_FULLNAME", "Unknown Person");
			String email = getHeader("REMOTE_USER", "");
			try {
				isSupervisor = EgroupCheck.check("atlas-upgrade-web-atwss-supervisors", email);
			} catch (Exception e) {
				log.warn("Could not read e-group", e);
			} 
		
			user = new User(fullName, email, isSupervisor);
		} else {
			// not secure, we use simple plain password to become supervisor
			String pwd = getEnv("APVSpwd", null);
			if (pwd == null) {
				log.warn("NO Supervisor Password set!!! Set enviroment variable 'APVSpwd'");
			} else {
				isSupervisor = (supervisorPassword != null) && supervisorPassword.equals(pwd);
			}
			user = new User("Unknown Person", "", isSupervisor);
		}
		log.info("User: "+user);
		
		session.setAttribute("USER", user);
		session.setAttribute("SUPERVISOR", isSupervisor);
		
		return user;
	}
	
	@Override
	public User getUser() {
		HttpSession session = getThreadLocalRequest().getSession(true);
		User user = (User)session.getAttribute("USER");
		if (user == null) {
			user = login(null);
		}
		
		return user;
	}
			
	private String getEnv(String env, String defaultValue) {
		String value = System.getenv(env);
		return value != null ? value : defaultValue;
	}
	
	private String getHeader(String key, String defaultValue) {
		String value = getThreadLocalRequest().getHeader(key);
		return value != null ? value : "";
	}
}
