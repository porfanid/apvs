package ch.cern.atlas.apvs.server;

import com.google.web.bindery.event.shared.Event;

public class MessageEvent extends Event<MessageEvent.Handler> {

	public interface Handler {
		void onMessageReceived(MessageEvent event);
	}
	
	public static final Type<MessageEvent.Handler> TYPE = new Type<MessageEvent.Handler>();

	private String prefix;
	private String msg;
	
	public MessageEvent() {
	}

	public MessageEvent(String prefix, String msg) {
		this.prefix = prefix;
		this.msg = msg;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getMessage() {
		return msg;
	}
	
	@Override
	public Type<MessageEvent.Handler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onMessageReceived(this);
	}
	
	@Override
	public String toString() {
		return "MessageEvent "+prefix+" "+msg;
	}

}
