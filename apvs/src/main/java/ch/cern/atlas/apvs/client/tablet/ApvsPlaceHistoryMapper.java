package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.tablet.AboutPlace.AboutPlaceTokenizer;
import ch.cern.atlas.apvs.client.tablet.HomePlace.HomePlaceTokenizer;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ HomePlaceTokenizer.class, AboutPlaceTokenizer.class })
public interface ApvsPlaceHistoryMapper extends PlaceHistoryMapper {
}
