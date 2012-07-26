package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Arguments {
	
	List<String> list;
	
	public Arguments(String args) {
		list = args != null ? Arrays.asList(args.split(";")) : new ArrayList<String>();
	}

	public Arguments() {
		list = new ArrayList<String>();
	}

	public String getArg(int index) {
		if ((index < 0) || (index >= list.size())) return "";
		return list.get(index);
	}
	
	public List<String> getArgs(int index) {
		if ((index < 0) || (index >= list.size())) return new ArrayList<String>();
		List<String> args = new ArrayList<String>();
		for (int i=index; i<list.size(); i++) {
			args.add(list.get(i));
		}
		return args;
	}
}
