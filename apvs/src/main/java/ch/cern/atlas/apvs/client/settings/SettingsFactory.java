package ch.cern.atlas.apvs.client.settings;


import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

// @Category(SettingsCategory.class)
public interface SettingsFactory extends AutoBeanFactory {
	AutoBean<Settings> settings();
}
