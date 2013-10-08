package ch.cern.atlas.apvs.converter;

import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ch.cern.atlas.apvs.db.Database;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.SortOrder;

public class DatabaseConverter {

	private Database database;

	public DatabaseConverter() {
		database = Database.getInstance();
	}

	public void run() {
		System.out.println("START run()");
		long count = database.getCount(Measurement.class);
		System.out.println("Found: " + count + " records");

		Session session = null;
		Transaction tx = null;
		try {
			session = database.getSessionFactory().openSession();
			tx = session.beginTransaction();
			@SuppressWarnings("unchecked")
			Iterator<Measurement> iterator = database.getQuery(session,
					Measurement.class, null, null,
					new SortOrder[] { new SortOrder("date", true) }).iterate();

			int i = 0;
			for (; iterator.hasNext(); i++) {
				Measurement m = iterator.next();

				// Do something with filter here...

				if (i % 10000 == 0) {
					System.out.println("OK Handled " + i + " records");

					// print something here every 10000 records
					System.err.println(m);
				}
			}
			System.out.println("Read all " + i + " records");

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
