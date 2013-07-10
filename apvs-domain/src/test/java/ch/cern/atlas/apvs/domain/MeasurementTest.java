package ch.cern.atlas.apvs.domain;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MeasurementTest {
	private SessionFactory sessionFactory;

	@Before
	public void before() throws Exception {
		ServiceRegistry registry = new ServiceRegistryBuilder()
				.buildServiceRegistry();
		// configures settings from hibernate.cfg.xml
		Configuration configuration = new Configuration().configure();
		configuration.registerTypeOverride(new DoubleStringType());
		configuration.registerTypeOverride(new IntegerStringType());
		sessionFactory = configuration.buildSessionFactory();
	}

	@After
	public void after() throws Exception {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testBasicUsage() {
		Measurement m1 = new Measurement("PTU_88", "Temperature", 22.4, 20.0,
				25.0, "Degrees", 60000, new Date());
		Measurement m2 = new Measurement("PTU_99", "Temperature", 22.8, 20.0, 25.0,
				"Degrees", 60000, new Date());

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(m1);
		session.save(m2);
		session.getTransaction().commit();
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createQuery("from Measurement").list();
		Assert.assertEquals(2, result.size());
//		System.err.println(result.get(0));
		Assert.assertEquals(m1, result.get(0));
//		System.err.println(result.get(1));
		Assert.assertEquals(m2, result.get(1));
		session.getTransaction().commit();
		session.close();
	}

}
