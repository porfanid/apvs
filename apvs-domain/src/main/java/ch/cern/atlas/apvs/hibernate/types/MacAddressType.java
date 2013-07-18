package ch.cern.atlas.apvs.hibernate.types;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

import ch.cern.atlas.apvs.domain.MacAddress;

/**
 * Store a MacAddress as a string in the DB
 * 
 * @author duns
 * 
 */
public class MacAddressType extends
		AbstractSingleColumnStandardBasicType<MacAddress> implements
		DiscriminatorType<MacAddress> {

	private static final long serialVersionUID = 6415021901258194814L;
	public static final MacAddressType INSTANCE = new MacAddressType();

	public MacAddressType() {
		super(VarcharTypeDescriptor.INSTANCE, MacAddressTypeDescriptor.INSTANCE);
	}

	public String getName() {
		return "macaddress";
	}

	@Override
	public String toString(MacAddress value) {
		return value.toString();
	}

	@Override
	public String objectToSQLString(MacAddress value, Dialect dialect)
			throws Exception {
		return StringType.INSTANCE.objectToSQLString(toString(value), dialect);
	}

	@Override
	public MacAddress stringToObject(String xml) throws Exception {
		return MacAddressTypeDescriptor.INSTANCE.fromString(xml);
	}
}
