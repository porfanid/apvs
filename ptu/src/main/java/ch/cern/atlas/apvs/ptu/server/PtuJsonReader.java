package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;

import ch.cern.atlas.apvs.domain.PtuConstants;

import com.cedarsoftware.util.io.JsonReader;

public class PtuJsonReader extends JsonReader {

	public PtuJsonReader(InputStream in) {
		super(in);
	}

	public PtuJsonReader(InputStream in, boolean noObjects) {
		super(in, noObjects);
	}

    protected Object createJavaObjectInstance(Class clazz, JsonObject jsonObj) throws IOException {
    	jsonObj.put("@type", "ch.cern.atlas.apvs.domain.Measurement");
    	jsonObj.put("name", jsonObj.get("sensor"));
    	jsonObj.remove("sensor");
    	return super.createJavaObjectInstance(clazz, jsonObj);
    }
    
    @Override
    protected Date convertToDate(Object rhs) {
    	try {
			return PtuConstants.dateFormat.parse((String)rhs);
		} catch (ParseException e) {
			return null;
		}
    }

}
