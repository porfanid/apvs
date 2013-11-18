package ch.cern.atlas.apvs.converter;

import java.util.Arrays;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.db.Database;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.SortOrder;

public class DatabaseConverter {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	private Database database;

	public DatabaseConverter() {
		database = Database.getInstance();
	}

	public void run() {
		log.info("START run()");
		long count = database.getCount(Measurement.class);
		log.info("Found: " + count + " records");

		Session session = null;
		Transaction tx = null;
		try {
			session = database.getSessionFactory().openSession();
			tx = session.beginTransaction();
			@SuppressWarnings("unchecked")
			Iterator<Measurement> iterator = database.getQuery(session,
					Measurement.class, null, null,
					Arrays.asList(new SortOrder("time", true))).iterate();

			int i = 0;
			for (; iterator.hasNext(); i++) {
				Measurement m = iterator.next();

				// Do something with filter here...

				if (i % 10000 == 0) {
					log.info("OK Handled " + i + " records");

					// print something here every 10000 records
					log.info(""+m);
				}
			}
			log.info("Read all " + i + " records");

			System.exit(0);
			// tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public static void main(String[] args) {
		new DatabaseConverter().run();
	}

}
