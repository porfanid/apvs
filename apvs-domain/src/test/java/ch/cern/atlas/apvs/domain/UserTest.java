package ch.cern.atlas.apvs.domain;

import java.util.List;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

public class UserTest extends AbstractDomainTest {
	
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testBasicUsage() {
		User u1 = new User("Mark", "Donszelmann", "yyyyy");
		User u2 = new User("Olga", "Beltramello", "xxxxx");

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(u1);
		session.save(u2);
		session.getTransaction().commit();
		session.close();

		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createQuery("from User").list();
		Assert.assertEquals(2, result.size());
		System.err.println(result.get(0));
		Assert.assertEquals(u1, result.get(0));
		System.err.println(result.get(1));
		Assert.assertEquals(u2, result.get(1));
		session.getTransaction().commit();
		session.close();
	}

}
