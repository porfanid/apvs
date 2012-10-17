package ch.cern.atlas.apvs.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

import ch.cern.atlas.apvs.domain.History;
import ch.cern.atlas.apvs.domain.Ptu;

public class Ptus {

	private static Ptus instance = new Ptus();

	private SortedMap<String, Ptu> ptus = new ConcurrentSkipListMap<String, Ptu>();
	private DbHandler dbHandler;

	public static Ptus getInstance() {
		return instance;
	}
	
	public void setDbHandler(DbHandler dbHandler) {
		this.dbHandler = dbHandler;
	}

	public Ptu get(String ptuId) {
		return ptus.get(ptuId);
	}
	
	public void put(String ptuId, Ptu ptu) {
		ptus.put(ptuId, ptu);
	}
	
	public void remove(String ptuId) {
		ptus.remove(ptuId);
	}

	public History setHistory(String ptuId, String sensor, String unit) {
		Ptu ptu = ptus.get(ptuId);
		History history = ptu.getHistory(sensor);
		if ((history == null) && (dbHandler != null)) {
			history = dbHandler.getHistory(ptuId, sensor, unit);
			ptu.setHistory(sensor, history);
		}
		return history;
	}

	public List<String> getPtuIds() {
		List<String> result = new ArrayList<String>();
		result.addAll(ptus.keySet());
		return result;
	}
}
