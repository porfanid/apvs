package ch.cern.atlas.apvs.hibernate.types;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.IntegerTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

/**
 * Store a double as a string in the DB
 * 
 * @author duns
 * 
 */
public class IntegerStringType extends
		AbstractSingleColumnStandardBasicType<Integer> implements
		DiscriminatorType<Integer> {
	private static final long serialVersionUID = -3280088437791582590L;
	public static final IntegerStringType INSTANCE = new IntegerStringType();

	public IntegerStringType() {
		super(VarcharTypeDescriptor.INSTANCE, IntegerTypeDescriptor.INSTANCE);
	}

	public String getName() {
		return "integer_string";
	}

	@Override
	public String toString(Integer value) {
		return Integer.toString(value);
	}

	@Override
	public String objectToSQLString(Integer value, Dialect dialect)
			throws Exception {
		return StringType.INSTANCE.objectToSQLString(toString(value), dialect);
	}

	@Override
	public Integer stringToObject(String xml) throws Exception {
		return IntegerTypeDescriptor.INSTANCE.fromString(xml);
	}
}
