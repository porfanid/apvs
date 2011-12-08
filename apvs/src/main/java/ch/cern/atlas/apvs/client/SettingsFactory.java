package ch.cern.atlas.apvs.client;


import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanFactory.Category;

@Category(SettingsCategory.class)
public interface SettingsFactory extends AutoBeanFactory {
	AutoBean<Settings> settings();
}
