package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import ch.cern.atlas.apvs.client.settings.Proxy;

public class ProxyTest {

	@Test
	public void configurationTest() throws IOException {
		InputStream in = getClass().getResourceAsStream("httpd-proxy-test.conf");
		Proxy proxy = ProxyConf.load(in);
		System.err.println(proxy);
	}

}
