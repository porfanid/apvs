package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.tablet.CameraPlace.CameraPlaceTokenizer;
import ch.cern.atlas.apvs.client.tablet.HomePlace.HomePlaceTokenizer;
import ch.cern.atlas.apvs.client.tablet.ImagePlace.ImagePlaceTokenizer;
import ch.cern.atlas.apvs.client.tablet.ModelPlace.ModelPlaceTokenizer;
import ch.cern.atlas.apvs.client.tablet.ProcedureMenuPlace.ProcedureMenuPlaceTokenizer;
import ch.cern.atlas.apvs.client.tablet.ProcedurePlace.ProcedurePlaceTokenizer;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ HomePlaceTokenizer.class, ModelPlaceTokenizer.class,
		CameraPlaceTokenizer.class, ProcedureMenuPlaceTokenizer.class,
		ProcedurePlaceTokenizer.class, ImagePlaceTokenizer.class })
public interface TabletPlaceHistoryMapper extends PlaceHistoryMapper {
}
