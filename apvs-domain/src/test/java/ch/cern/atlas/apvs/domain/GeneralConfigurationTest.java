package ch.cern.atlas.apvs.domain;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

public class GeneralConfigurationTest extends AbstractDomainTest {

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testBasicUsage() {
		Device d1 = new Device("PTU_88", InetAddress.getByName("localhost"), "ptu 88", null, "ptu_88.cern.ch");
		Device d2 = new Device("PTU_99", InetAddress.getByName("localhost"), "ptu 99", null, "ptu_99.cern.ch");
		
		GeneralConfiguration gc1 = new GeneralConfiguration(d1, "12345", "00:00:00:00:00:00", new Date());
		GeneralConfiguration gc2 = new GeneralConfiguration(d1, "54321", "01:02:03:04:05:06", new Date());
 
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(d1);
		session.save(d2);
		session.save(gc1);
		session.save(gc2);
		session.getTransaction().commit();
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createCriteria(GeneralConfiguration.class).list();
		Assert.assertEquals(2, result.size());
//		System.err.println(result.get(0));
		Assert.assertEquals(gc1, result.get(0));
//		System.err.println(result.get(1));
		Assert.assertEquals(gc2, result.get(1));
		session.getTransaction().commit();
		session.close();
	}

}
