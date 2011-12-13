package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.place.shared.Place;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.mvp.client.AnimationMapper;

public class TabletMenuAnimationMapper implements AnimationMapper {

	@Override
	public Animation getAnimation(Place oldPlace, Place newPlace) {
		if (oldPlace == null) {
			return Animation.FADE;
		}

		return Animation.SLIDE;
	}

}
