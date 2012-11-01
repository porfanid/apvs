package ch.cern.atlas.apvs.client.validation;


public class OrValidator implements Validator {

	private Validator left;
	private Validator right;

	public OrValidator(Validator left, Validator right) {
		this.left = left;
		this.right = right;
	}
	
	@Override
	public String validate(String value) {
		String result = left.validate(value);
		if (result == null) {
			return result;
		}
		return right.validate(value);
	}
}
