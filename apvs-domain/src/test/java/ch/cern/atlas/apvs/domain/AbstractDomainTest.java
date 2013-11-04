package ch.cern.atlas.apvs.domain;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractDomainTest {
	protected SessionFactory sessionFactory;

	@Before
	public void before() throws Exception {
		sessionFactory = new TestDatabase().getSessionFactory();
	}

	@After
	public void after() throws Exception {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}
}
