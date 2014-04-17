package ch.cern.atlas.apvs.hibernate.types;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

import ch.cern.atlas.apvs.domain.InetAddress;

public class InetAddressTypeDescriptor  extends AbstractTypeDescriptor<InetAddress> {

	private static final long serialVersionUID = -2851210283400909740L;
	public static final InetAddressTypeDescriptor INSTANCE = new InetAddressTypeDescriptor();

	public InetAddressTypeDescriptor() {
		super( InetAddress.class );
	}
	
	public String toString(InetAddress value) {
		return value == null ? null : value.toString();
	}

	public InetAddress fromString(String string) {
		try {
			return InetAddress.getByName( string );
		} catch (IllegalArgumentException e) {
			return InetAddress.getLocalHost();
		}
	}

	@SuppressWarnings({ "unchecked" })
	public <X> X unwrap(InetAddress value, Class<X> type, WrapperOptions options) {
		if ( value == null ) {
			return null;
		}
		if ( InetAddress.class.isAssignableFrom( type ) ) {
			return (X) value;
		}
		else if (String.class.isAssignableFrom( type ) ) {
			return (X) value.getHostAddress();
		}
		throw unknownUnwrap( type );
	}

	public <X> InetAddress wrap(X value, WrapperOptions options) {
		if ( value == null ) {
			return null;
		}
		if ( InetAddress.class.isInstance( value ) ) {
			return (InetAddress) value;
		}
		else if ( String.class.isInstance( value ) ) {
			try {
				return InetAddress.getByName( ( (String) value ) );
			} catch (IllegalArgumentException e) {
				throw unknownWrap( value.getClass() );
			}
		}
		throw unknownWrap( value.getClass() );
	}	

}
