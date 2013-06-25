package ch.cern.atlas.apvs.client.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Mark Donszelmann
 */
public class Proxy {
	
	private List<String> forward = new ArrayList<String>(); 
	private List<String> reverse = new ArrayList<String>(); 
	
	public Proxy() {
	}
	
	public void put(String src, String dst) {
		forward.add(src);
		reverse.add(dst);
	}
	
	public String getUrl(String url) {
		return translate(url, forward, reverse);
	}
	
	public String getReverseUrl(String url) {
		return translate(url, reverse, forward);		
	}

	private String translate(String url, List<String> src, List<String> dst) {
		for (int index = 0; index < src.size(); index++) {
			String prefix = src.get(index);
			if (url.startsWith(prefix)) {
				return dst.get(index) + url.substring(prefix.length());
			}
		}
		return url;
	}
}
