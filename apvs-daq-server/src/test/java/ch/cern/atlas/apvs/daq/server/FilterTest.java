package ch.cern.atlas.apvs.daq.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.InetAddress;
import ch.cern.atlas.apvs.domain.MacAddress;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.ptu.server.Temperature;

public class FilterTest {

	Filter f = new ValueFilter();

	Device device = new Device("PTU099", InetAddress.getByName("localhost"), "Test Device", new MacAddress("00:00:00:00:00:00"), "localhost");

	long now = new Date().getTime();
	Date t0 = new Date(now+20000);
	Date t1 = new Date(now+40000);
	Date t2 = new Date(now+60000);
	Date t3 = new Date(now+80000);
	Date t4 = new Date(now+100000);
	Date t5 = new Date(now+120000);

	double v0 = 0.1;
	double v1 = 0.1;
	double v2 = 0.1;
	double v3 = 0.12;
	double v4 = 0.14;
	double v5 = 0.2;
	double r = 0.05;

	Measurement m0, m1, m2, m3, m4, m5;

	List<Measurement> l;

	@Before
	public void before() {
		m0 = new Temperature(device, v0, t0);
		m1 = new Temperature(device, v1, t1);
		m2 = new Temperature(device, v2, t2);
		m3 = new Temperature(device, v3, t3);
		m4 = new Temperature(device, v4, t4);
		m5 = new Temperature(device, v5, t5);
		l = new ArrayList<Measurement>();
	}

	@Test
	public void initialValue() {
		System.out.println("FilterTest 1");
		boolean b = f.filter(m0, l, r);
		Assert.assertFalse(b);
		Assert.assertEquals(1, l.size());
		Assert.assertEquals(m0, l.get(0));
	}

	@Test
	public void twoTimesSameValue() {
		System.out.println("FilterTest 2");
		f.filter(m0, l, r);
		boolean b = f.filter(m1, l, r);		
		Assert.assertFalse(b);
		Assert.assertEquals(2, l.size());
		Assert.assertEquals(m0, l.get(0));
		Assert.assertEquals(m1, l.get(1));
	}

	@Test
	public void threeTimesSameValue() {
		System.out.println("FilterTest 3 ");
		f.filter(m0, l, r);
		f.filter(m1, l, r);
		boolean b = f.filter(m2, l, r);		
		Assert.assertTrue("Wrong return ", b);
		Assert.assertEquals(2, l.size());
		Assert.assertEquals(m0, l.get(0));
		Assert.assertEquals(m1.getValue(), l.get(1).getValue());
		Assert.assertEquals(m2.getDate(), l.get(1).getDate());
	}

	@Test
	public void valueAboveResulotion() {
		System.out.println("FilterTest 4 ");
		f.filter(m0, l, r);
		f.filter(m1, l, r);
		f.filter(m2, l, r);
		boolean b = f.filter(m5, l, r);
		Assert.assertFalse(b);
		Assert.assertEquals(3, l.size());
		Assert.assertEquals(m0, l.get(0));
		Assert.assertEquals(m2, l.get(1));
		Assert.assertEquals(m5, l.get(2));
	}

	@Test
	public void valueBelowResolution() {
		System.out.println("FilterTest 5");
		f.filter(m0, l, r);
		f.filter(m1, l, r);
		f.filter(m2, l, r);
		boolean b = f.filter(m3, l, r);
		Assert.assertTrue(b);
		Assert.assertEquals(3, l.size());
		Assert.assertEquals(m0, l.get(0));
		Assert.assertEquals(m2, l.get(1));
		Assert.assertEquals(m2.getValue(), l.get(2).getValue());
		Assert.assertEquals(m3.getDate(), l.get(2).getDate());
	}

//	@Test
	public void secondValueAboveResolution() {
		System.out.println("FilterTest 6");
		f.filter(m0, l, r);
		f.filter(m1, l, r);
		f.filter(m2, l, r);
		f.filter(m3, l, r);		// first
		boolean b = f.filter(m5, l, r);		// second
		Assert.assertFalse(b);
		Assert.assertEquals(3, l.size());
		Assert.assertEquals(m0, l.get(0));
		Assert.assertEquals(m2.getValue(), l.get(1).getValue());
		Assert.assertEquals(m3.getDate(), l.get(1).getDate());
		Assert.assertEquals(m5, l.get(2));
	}

//	@Test
	public void secondValueBelowResolution() {
		System.out.println("FilterTest 7");
		f.filter(m0, l, r);
		f.filter(m1, l, r);
		f.filter(m2, l, r);
		f.filter(m3, l, r);		// first
		boolean b = f.filter(m4, l, r);		// second
		Assert.assertTrue(b);
		Assert.assertEquals(2, l.size());
		Assert.assertEquals(m0, l.get(0));
		Assert.assertEquals(m2.getValue(), l.get(1).getValue());
		Assert.assertEquals(m4.getDate(), l.get(1).getDate());
	}

//	@Test
	public void disconnectAfterLastValue() {
		System.out.println("FilterTest 8");
		f.filter(m0, l, r);
		f.filter(m1, l, r);
		f.filter(m2, l, r);
		// disconnect !
		boolean b = f.filter(null, l, r);
		Assert.assertTrue(b);
		Assert.assertEquals(2, l.size());
		Assert.assertFalse(l.get(l.size()-1).isConnected());
	}

	//@Test
	public void discardChange() {
		System.out.println("FilterTest 9");
		Filter d = new Filter() {

			@Override
			public boolean filter(Measurement current, List<Measurement> list,
					double resolution) {
				return false;
			}
		};

		Assert.assertFalse(d.filter(m0, l, r));
	}

	//@Test
	public void addChange() {
		System.out.println("FilterTest 10");
		Filter a = new Filter() {

			@Override
			public boolean filter(Measurement current, List<Measurement> list,
					double resolution) {
				list.add(current);
				return false;
			}
		};

		boolean update = a.filter(m0, l, 0.001);

		Assert.assertFalse(update);
		Assert.assertEquals(1, l.size());
	}

	//@Test
	public void updateChange() {
		System.out.println("FilterTest 11");
		Filter u = new Filter() {

			@Override
			public boolean filter(Measurement current, List<Measurement> list,
					double resolution) {

				// update date
				list.get(list.size()-1).setDate(current.getDate());
				return true;
			}
		};

		l.add(m0);
		boolean update = u.filter(m1, l, 0.001);

		Assert.assertTrue(update);
		Assert.assertEquals(1, l.size());
		Assert.assertEquals(t1, l.get(l.size()-1).getDate());
	}

	//@Test
	public void disconnectChange() {
		System.out.println("FilterTest 12");
		Filter d = new Filter() {

			@Override
			public boolean filter(Measurement current, List<Measurement> list,
					double resolution) {
				list.get(list.size()-1).disconnect();
				return true;
			}
		};

		l.add(m0);
		boolean update = d.filter(m1, l, 0.001);
		Assert.assertTrue(update);
		Assert.assertEquals(1, l.size());
		Assert.assertFalse(l.get(l.size()-1).isConnected());
	}


}
