package ch.cern.atlas.apvs.domain;

import org.hibernate.cfg.Configuration;

import ch.cern.atlas.apvs.db.Database;

public class TestDatabase extends Database {
	
	TestDatabase() {
		super(new Configuration().configure());
	}
}
