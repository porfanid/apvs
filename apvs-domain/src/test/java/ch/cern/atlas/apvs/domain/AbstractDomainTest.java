package ch.cern.atlas.apvs.domain;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;

import ch.cern.atlas.apvs.hibernate.types.DoubleStringType;
import ch.cern.atlas.apvs.hibernate.types.InetAddressType;
import ch.cern.atlas.apvs.hibernate.types.IntegerStringType;
import ch.cern.atlas.apvs.hibernate.types.MacAddressType;

public abstract class AbstractDomainTest {
	protected ServiceRegistry serviceRegistry;
	protected SessionFactory sessionFactory;
	
	@Before
	public void before() throws Exception {
		// configures settings from hibernate.cfg.xml
		Configuration configuration = new Configuration().configure();
		
		configuration.registerTypeOverride(new DoubleStringType());
		configuration.registerTypeOverride(new IntegerStringType());
		configuration.registerTypeOverride(new MacAddressType());
		configuration.registerTypeOverride(new InetAddressType());
		
		serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}

	@After
	public void after() throws Exception {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

}
