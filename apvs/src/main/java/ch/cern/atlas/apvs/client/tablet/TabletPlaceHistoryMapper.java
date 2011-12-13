package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.tablet.AboutPlace.AboutPlaceTokenizer;
import ch.cern.atlas.apvs.client.tablet.CameraPlace.CameraPlaceTokenizer;
import ch.cern.atlas.apvs.client.tablet.HomePlace.HomePlaceTokenizer;
import ch.cern.atlas.apvs.client.tablet.ModelPlace.ModelPlaceTokenizer;
import ch.cern.atlas.apvs.client.tablet.ProcedurePlace.ProcedurePlaceTokenizer;
import ch.cern.atlas.apvs.client.tablet.RadiationMapPlace.RadiationMapPlaceTokenizer;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ HomePlaceTokenizer.class, AboutPlaceTokenizer.class,
		RadiationMapPlaceTokenizer.class, ModelPlaceTokenizer.class,
		CameraPlaceTokenizer.class, ProcedurePlaceTokenizer.class })
public interface TabletPlaceHistoryMapper extends PlaceHistoryMapper {
}
