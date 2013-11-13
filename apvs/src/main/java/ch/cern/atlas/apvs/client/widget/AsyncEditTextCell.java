package ch.cern.atlas.apvs.client.widget;

import java.util.HashMap;
import java.util.Map;

import ch.cern.atlas.apvs.client.widget.EditTextCell.ViewData;
import ch.cern.atlas.apvs.domain.Device;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class AsyncEditTextCell extends EditTextCell {

	enum State {
		none, sent, ok, error;
	}

	private Map<Object, StateData> stateDataMap = new HashMap<Object, StateData>();

	private static class StateData {

		State state;
		Throwable cause;
		int count;

		StateData(State state, Throwable cause) {
			this.state = state;
			this.cause = cause;
			count = 10;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((cause == null) ? 0 : cause.hashCode());
			result = prime * result + count;
			result = prime * result + ((state == null) ? 0 : state.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			StateData other = (StateData) obj;
			if (cause == null) {
				if (other.cause != null) {
					return false;
				}
			} else if (!cause.equals(other.cause)) {
				return false;
			}
			if (count != other.count) {
				return false;
			}
			if (state != other.state) {
				return false;
			}
			return true;
		}

		State getState() {
			return state;
		}

		void setState(State state) {
			this.state = state;
			cause = null;

			if (state == State.ok) {
				count--;
				if (count <= 0) {
					state = State.none;
					count = 10;
				}
			}
		}

		Throwable getCause() {
			return cause;
		}
	}

	private void clearStateData(Object key) {
		if (key != null) {
			stateDataMap.remove(key);
		}
	}

	private StateData getStateData(Object key) {
		return (key == null) ? null : stateDataMap.get(key);
	}

	private void setStateData(Object key, StateData stateData) {
		if (key == null) {
			return;
		}

		if (stateData == null) {
			clearViewData(key);
		} else {
			stateDataMap.put(key, stateData);
		}
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		super.render(context, value, sb);

		Object key = context.getKey();
		StateData stateData = getStateData(key);

		if (stateData != null) {
			switch (stateData.getState()) {
			case none:
				clearStateData(key);
				return;
			case sent:
				ViewData viewData = getViewData(context.getKey());
				if ((viewData == null) || viewData.getText().equals(value)) {
					stateData.setState(State.ok);
				}
				break;
			case ok:
				stateData.setState(State.ok);
				break;
			case error:
				break;
			default:
				// ignore
				break;
			}

			switch (stateData.getState()) {
			case none:
				break;
			case sent:
				sb.appendHtmlConstant("&nbsp;<i class=\"icon-time\"></i>");
				break;
			case ok:
				sb.appendHtmlConstant("&nbsp;<i class=\"icon-ok\"></i>");
				break;
			case error:
				sb.appendHtmlConstant("&nbsp;<i class=\"icon-exclamation-sign\" title=\""
						+ stateData.getCause() + "\"></i>");
				break;
			default:
				// ignore
				break;
			}
		}

		if (false) {
			sb.appendHtmlConstant("&nbsp;<i class=\"icon-star\"></i>");
			ViewData viewData = getViewData(context.getKey());
			if (viewData != null) {
				sb.appendHtmlConstant("&nbsp;o:" + viewData.getOriginal()
						+ "&nbsp;t:" + viewData.getText());
			}
			sb.appendHtmlConstant("&nbsp;v:" + value);
			if (stateData != null) {
				sb.appendHtmlConstant("&nbsp;c:" + stateData.count);
			}
		}
	}

	protected void onSuccess(Context context, Device device, String value,
			Void result) {
		Object key = context.getKey();
		StateData stateData = getStateData(key);

		if (stateData == null) {
			stateData = new StateData(State.sent, null);
			setStateData(key, stateData);
		}
	}

	protected void onFailure(Context context, Device device, String value,
			Throwable caught) {
		Object key = context.getKey();
		StateData stateData = getStateData(key);

		if (stateData == null) {
			stateData = new StateData(State.error, caught);
			setStateData(key, stateData);
		}

		clearViewData(device);
	}
}
