package ch.cern.atlas.apvs.domain;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

public class EventTest extends AbstractDomainTest {

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testBasicUsage() {
		Device d1 = new Device("PTU_88", InetAddress.getByName("localhost"), "ptu 88", null, "ptu_88.cern.ch", false);
		Device d2 = new Device("PTU_99", InetAddress.getByName("localhost"), "ptu 99", null, "ptu_99.cern.ch", false);
		
		Event e1 = new Event(d1, "Temperature", "UpValue", 20.5, 18.5, "&deg;", new Date());
		Event e2 = new Event(d2, "Temperature", "LowValue", 17.5, 18.0, "&deg;", new Date());

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(d1);
		session.save(d2);
		session.save(e1);
		session.save(e2);
		session.getTransaction().commit();
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createCriteria(Event.class).list();
		Assert.assertEquals(2, result.size());
//		System.err.println(result.get(0));
		Assert.assertEquals(e1, result.get(0));
//		System.err.println(result.get(1));
		Assert.assertEquals(e2, result.get(1));
		session.getTransaction().commit();
		session.close();
	}

}
