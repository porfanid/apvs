package ch.cern.atlas.apvs.client.validation;

public class AndValidator implements Validator {

	private Validator left;
	private Validator right;

	public AndValidator(Validator left, Validator right) {
		this.left = left;
		this.right = right;
	}
	
	@Override
	public Validation validate(String value) {
		Validation result = left.validate(value);
		if (result.isValid()) {
			return right.validate(value);
		}
		return result;
	}

}
