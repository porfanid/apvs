package ch.cern.atlas.apvs.ptu.server;

import ch.cern.atlas.apvs.domain.Packet;

import com.google.web.bindery.event.shared.Event;

public class MessageEvent extends Event<MessageEvent.Handler> {

	public interface Handler {
		void onMessageReceived(MessageEvent event);
	}
	
	public static final Type<MessageEvent.Handler> TYPE = new Type<MessageEvent.Handler>();

	private String prefix;
	private Packet packet;
	
	public MessageEvent() {
	}

	public MessageEvent(String prefix, Packet packet) {
		this.prefix = prefix;
		this.packet = packet;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public Packet getPacket() {
		return packet;
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
		return "MessageEvent "+prefix+" "+packet;
	}

}
