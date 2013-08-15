package ch.cern.atlas.apvs.domain;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

public class InterventionTest extends AbstractDomainTest {

	@Test
	public void testBasicUsage() {
		User u1 = new User("Mark", "Donszelmann", "yyyyy");
		User u2 = new User("Olga", "Beltramello", "xxxxx");
		
		Device d1 = new Device("PTU_88", InetAddress.getByName("localhost"), "ptu 88", null, "ptu_88.cern.ch");
		Device d2 = new Device("PTU_99", InetAddress.getByName("localhost"), "ptu 99", null, "ptu_99.cern.ch");
		
		Intervention i1 = new Intervention(u1, d1, new Date(1234567), null, "12345", null, "Test Intervention 1");
		Intervention i2 = new Intervention(u2, d2, new Date(1234567), null, "12345", null, "Test Intervention 2");
		
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(u1);
		session.save(u2);
		session.save(d1);
		session.save(d2);
		session.save(i1);
		session.save(i2);
		session.getTransaction().commit();		
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<Intervention> result = session.createQuery("from Intervention intervention where intervention.endTime is null").list();
		Assert.assertEquals(2, result.size());
//		System.err.println(result.get(0));
		Assert.assertEquals(i1, result.get(0));
		Assert.assertEquals(u1, result.get(0).getUser());
		Assert.assertEquals(d1, result.get(0).getDevice());
//		System.err.println(result.get(1));
		Assert.assertEquals(i2, result.get(1));
		Assert.assertEquals(u2, result.get(1).getUser());
		Assert.assertEquals(d2, result.get(1).getDevice());
		session.getTransaction().commit();
		session.close();
		
		// close second intervention and check
		session = sessionFactory.openSession();
		session.beginTransaction();
		i2.setEndTime(new Date());
		session.update(i2);
		session.getTransaction().commit();	
		
		@SuppressWarnings("unchecked")
		List<Intervention> result2 = session.createQuery("from Intervention").list();
		Assert.assertEquals(2, result2.size());
		Assert.assertEquals(i1, result2.get(0));
		Assert.assertEquals(i2, result2.get(1));		
		session.close();
	}

}
