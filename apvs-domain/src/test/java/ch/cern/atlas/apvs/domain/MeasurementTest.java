package ch.cern.atlas.apvs.domain;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.cern.atlas.apvs.hibernate.types.DoubleStringType;
import ch.cern.atlas.apvs.hibernate.types.InetAddressType;
import ch.cern.atlas.apvs.hibernate.types.IntegerStringType;
import ch.cern.atlas.apvs.hibernate.types.MacAddressType;

public class MeasurementTest {
	private SessionFactory sessionFactory;

	@SuppressWarnings("deprecation")
	@Before
	public void before() throws Exception {
		// configures settings from hibernate.cfg.xml
		Configuration configuration = new Configuration().configure();
		configuration.registerTypeOverride(new DoubleStringType());
		configuration.registerTypeOverride(new IntegerStringType());
		configuration.registerTypeOverride(new MacAddressType());
		configuration.registerTypeOverride(new InetAddressType());
		sessionFactory = configuration.buildSessionFactory();
	}

	@After
	public void after() throws Exception {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testBasicUsage() {
		Device d1 = new Device("PTU_88", InetAddress.getByName("ptu_88"), "ptu 88", null, "ptu_88.cern.ch");
		Device d2 = new Device("PTU_99", InetAddress.getByName("ptu_99"), "ptu 99", null, "ptu_99.cern.ch");
		
		Measurement m1 = new Measurement(d1, "Temperature", 22.4, 20.0,
				25.0, "Degrees", 60000, new Date());
		Measurement m2 = new Measurement(d2, "Temperature", 22.8, 20.0, 25.0,
				"Degrees", 60000, new Date());

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(d1);
		session.save(d2);
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
