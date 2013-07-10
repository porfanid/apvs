package ch.cern.atlas.apvs.hibernate.types;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.DoubleTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

/**
 * Store a double as a string in the DB
 * 
 * @author duns
 * 
 */
public class DoubleStringType extends
		AbstractSingleColumnStandardBasicType<Double> implements
		DiscriminatorType<Double> {
	private static final long serialVersionUID = -5030380059564971170L;
	public static final DoubleStringType INSTANCE = new DoubleStringType();

	public DoubleStringType() {
		super(VarcharTypeDescriptor.INSTANCE, DoubleTypeDescriptor.INSTANCE);
	}

	public String getName() {
		return "double_string";
	}

	@Override
	public String toString(Double value) {
		return Double.toString(value);
	}

	@Override
	public String objectToSQLString(Double value, Dialect dialect)
			throws Exception {
		return StringType.INSTANCE.objectToSQLString(toString(value), dialect);
	}

	@Override
	public Double stringToObject(String xml) throws Exception {
		return DoubleTypeDescriptor.INSTANCE.fromString(xml);
	}
}
