package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import ch.cern.atlas.apvs.client.settings.Proxy;

public class ProxyConf {

	public static Proxy load(InputStream in) throws IOException {
		Proxy proxy = new Proxy();

		Pattern pattern = Pattern
				.compile("^ProxyPass[\\s]+([^\\s]+)[\\s]+([^\\s]+)");

		for (Iterator<String> i = IOUtils.readLines(in, "UTF-8").iterator(); i
				.hasNext();) {
			String line = i.next().trim();
			Matcher matcher = pattern.matcher(line);
			if (matcher.matches()) {
				String src = matcher.group(1);
				String dst = matcher.group(2);
				proxy.put(src, dst);
			}
		}

		return proxy;
	}
}
