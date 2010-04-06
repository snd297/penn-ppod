package edu.upenn.cis.ppod.util;

import com.google.inject.ImplementedBy;
import com.google.inject.assistedinject.Assisted;

/**
 * @author Sam Donnelly
 * 
 */
@ImplementedBy(JettyWebServer.class)
public interface IServletContainer extends IService {
	static interface IFactory {
		IServletContainer create(@Assisted("host") String host, int port,
				@Assisted("contextPath") String contextPath,
				@Assisted("war") String war);
	}
}
