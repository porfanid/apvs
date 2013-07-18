package ch.cern.atlas.apvs.hibernate.types;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

import ch.cern.atlas.apvs.domain.InetAddress;

/**
 * Store an InetAddress as a string in the DB
 * 
 * @author duns
 * 
 */
public class InetAddressType extends
		AbstractSingleColumnStandardBasicType<InetAddress> implements
		DiscriminatorType<InetAddress> {

	private static final long serialVersionUID = 4821836751115243308L;
	public static final InetAddressType INSTANCE = new InetAddressType();

	public InetAddressType() {
		super(VarcharTypeDescriptor.INSTANCE, InetAddressTypeDescriptor.INSTANCE);
	}

	public String getName() {
		return "inetaddress";
	}

	@Override
	public String toString(InetAddress value) {
		return value.toString();
	}

	@Override
	public String objectToSQLString(InetAddress value, Dialect dialect)
			throws Exception {
		return StringType.INSTANCE.objectToSQLString(toString(value), dialect);
	}

	@Override
	public InetAddress stringToObject(String xml) throws Exception {
		return InetAddressTypeDescriptor.INSTANCE.fromString(xml);
	}
}
