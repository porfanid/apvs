package ch.cern.atlas.apvs.hibernate.types;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

import ch.cern.atlas.apvs.domain.MacAddress;

public class MacAddressTypeDescriptor  extends AbstractTypeDescriptor<MacAddress> {

	private static final long serialVersionUID = 7158520191491763595L;
	public static final MacAddressTypeDescriptor INSTANCE = new MacAddressTypeDescriptor();

	public MacAddressTypeDescriptor() {
		super( MacAddress.class );
	}
	
	public String toString(MacAddress value) {
		return value == null ? null : value.toString();
	}

	public MacAddress fromString(String string) {
		return new MacAddress( string );
	}

	@SuppressWarnings({ "unchecked" })
	public <X> X unwrap(MacAddress value, Class<X> type, WrapperOptions options) {
		if ( value == null ) {
			return null;
		}
		if ( MacAddress.class.isAssignableFrom( type ) ) {
			return (X) value;
		}
		else if (String.class.isAssignableFrom( type ) ) {
			return (X) value.toString();
		}
		throw unknownUnwrap( type );
	}

	public <X> MacAddress wrap(X value, WrapperOptions options) {
		if ( value == null ) {
			return null;
		}
		if ( MacAddress.class.isInstance( value ) ) {
			return (MacAddress) value;
		}
		else if ( String.class.isInstance( value ) ) {
			return new MacAddress( ( (String) value ) );
		}
		throw unknownWrap( value.getClass() );
	}
	

}
