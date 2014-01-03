package org.springframework.prototype;

import org.springframework.data.rest.webmvc.RepositoryRestMvcConfiguration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { JpaConfiguration.class } ;
	}
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebConfiguration.class, RepositoryRestMvcConfiguration.class } ;
	}
	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
}
