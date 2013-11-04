package ch.cern.atlas.apvs.domain;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

public class MeasurementConfigurationTest extends AbstractDomainTest {

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testBasicUsage() {
		Device d1 = new Device("PTU_88", InetAddress.getByName("localhost"), "ptu 88", null, "ptu_88.cern.ch");
		Device d2 = new Device("PTU_99", InetAddress.getByName("localhost"), "ptu 99", null, "ptu_99.cern.ch");
		
		
		MeasurementConfiguration mc1 = new MeasurementConfiguration(d1, "Temperature", 20.0, 25.0, "&deg;C", 60000, 1.0, 0.2, new Date());
		MeasurementConfiguration mc2 = new MeasurementConfiguration(d1, "Humidity", 40.0, 60.0, "%", 10000, 0.95, -0.1, new Date());

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(d1);
		session.save(d2);
		session.save(mc1);
		session.save(mc2);
		session.getTransaction().commit();
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createCriteria(MeasurementConfiguration.class).list();
		Assert.assertEquals(2, result.size());
//		System.err.println(result.get(0));
		Assert.assertEquals(mc1, result.get(0));
//		System.err.println(result.get(1));
		Assert.assertEquals(mc2, result.get(1));
		session.getTransaction().commit();
		session.close();
	}

}
