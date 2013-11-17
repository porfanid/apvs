package ch.cern.atlas.apvs.client.validation;

public class AndValidator<T> implements Validator<T> {

	private Validator<T> left;
	private Validator<T> right;

	public AndValidator(Validator<T> left, Validator<T> right) {
		this.left = left;
		this.right = right;
	}
	
	@Override
	public Validation validate(T value) {
		Validation result = left.validate(value);
		if (result.isValid()) {
			return right.validate(value);
		}
		return result;
	}

}
