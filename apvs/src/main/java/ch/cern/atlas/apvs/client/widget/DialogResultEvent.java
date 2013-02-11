package ch.cern.atlas.apvs.client.widget;

public class DialogResultEvent {
	
	private String result;

	public DialogResultEvent(String result) {
		this.result = result;
	}
	
	public String getResult() {
		return result;
	}
}
