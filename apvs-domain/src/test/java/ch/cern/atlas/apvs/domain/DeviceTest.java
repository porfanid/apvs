package ch.cern.atlas.apvs.domain;

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

public class DeviceTest {
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
		Device d1 = new Device("PTU01", InetAddress.getByAddress(new byte[] {
				127, 0, 0, 1 }), "Description", new MacAddress(new byte[] {
				0x23, 0x45, 0x67, (byte)0x89, (byte)0xAB, (byte)0xCD }), "ptu01.cern.ch");
		Device d2 = new Device("PTU02", InetAddress.getByName("localhost"),
				"Some Desc", new MacAddress("23:45:67:89:AB:CD"),
				"ptu02.cern.ch");

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(d1);
		session.save(d2);
		session.getTransaction().commit();
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createQuery("from Device").list();
		Assert.assertEquals(2, result.size());
		System.err.println(result.get(0));
		Assert.assertEquals(d1, result.get(0));
		System.err.println(result.get(1));
		Assert.assertEquals(d2, result.get(1));
		session.getTransaction().commit();
		session.close();
	}

}
