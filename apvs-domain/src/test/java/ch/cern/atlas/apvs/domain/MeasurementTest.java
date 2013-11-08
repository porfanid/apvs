package ch.cern.atlas.apvs.domain;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

public class MeasurementTest extends AbstractDomainTest {

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testBasicUsage() {
		Device d1 = new Device("PTU_88", InetAddress.getByName("localhost"), "ptu 88", null, "ptu_88.cern.ch", false);
		Device d2 = new Device("PTU_99", InetAddress.getByName("localhost"), "ptu 99", null, "ptu_99.cern.ch", false);
		
		Measurement m1 = new Measurement(d1, "Temperature", 22.4, 20.0,
				25.0, "Degrees", 60000, "OneShoot", new Date());
		Measurement m2 = new Measurement(d2, "Temperature", 22.8, 20.0, 25.0,
				"Degrees", 60000, "OneShoot", new Date());

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
		List result = session.createCriteria(Measurement.class).list();
		Assert.assertEquals(2, result.size());
//		System.err.println(result.get(0));
		Assert.assertEquals(m1, result.get(0));
//		System.err.println(result.get(1));
		Assert.assertEquals(m2, result.get(1));
		session.getTransaction().commit();
		session.close();
	}

}
