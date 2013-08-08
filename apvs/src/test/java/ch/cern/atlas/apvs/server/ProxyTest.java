package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ch.cern.atlas.apvs.client.settings.Proxy;

@RunWith(JUnit4.class)
public class ProxyTest {
	
	private Proxy proxy;

	@Before
	public void setup() throws IOException {
		InputStream in = getClass().getResourceAsStream("httpd-proxy-test.conf");
		proxy = ProxyConf.load(in, new Proxy(true, "https://atwss.cern.ch"));		
	}
	
	@Test
	public void forward() throws IOException {		
		Assert.assertEquals("http://localhost:8095/index.html", proxy.getUrl("https://atwss.cern.ch/APVS/index.html"));
		Assert.assertEquals("http://localhost:8190/Worker1.mjpg", proxy.getUrl("https://atwss.cern.ch/streams/1/helmet/Worker1.mjpg"));
	}

	@Test
	public void reverse() throws IOException {
		Assert.assertEquals("https://atwss.cern.ch/APVS/index.html", proxy.getReverseUrl("http://localhost:8095/index.html"));
		Assert.assertEquals("https://atwss.cern.ch/streams/1/helmet/Worker1.mjpg", proxy.getReverseUrl("http://localhost:8190/Worker1.mjpg"));
	}

}
