package com.ybg.urule.server;
import java.util.Properties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import com.bstek.urule.URulePropertyPlaceholderConfigurer;

/** @author Jacky.gao 相当于 configure.properties 
 * @since 2016年10月12日 */
 @Component 
public class PropertiesConfiguration extends URulePropertyPlaceholderConfigurer implements InitializingBean {
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Properties props = new Properties();
		props.setProperty("urule.repository.xml", "classpath:mysql.xml");
		setProperties(props);
	}
}