package org.springframework.prototype;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.data.rest.repository.invoke.RepositoryQueryMethod;
import org.springframework.data.rest.repository.jpa.JpaRepositoryExporter;
import org.springframework.data.rest.repository.jpa.JpaRepositoryMetadata;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.velocity.SpringResourceLoader;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/js")
public class JsRepoController {
	
	private JpaRepositoryExporter exporter;

	@Autowired
	public JsRepoController(JpaRepositoryExporter exporter) {
		this.exporter = exporter;
	}

	@RequestMapping(value="/{repository}.js", method=GET)
	public ResponseEntity<String> generateRepositoryModule(@PathVariable("repository") String repository, UriComponentsBuilder uriBuilder) {
		if (!exporter.repositoryNames().contains(repository)) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
		
		VelocityEngine velocityEngine = new VelocityEngine();
		initSpringResourceLoader(velocityEngine, "classpath:/jstemplates");
		StringWriter writer = new StringWriter();
		VelocityContext context = new VelocityContext();
		String resourcePath = getResourcePath(repository, uriBuilder);
		context.put("resourcePath", resourcePath);
		context.put("repoName", StringUtils.capitalize(repository));
		context.put("queryMethods", buildQueryMethodMap(exporter.repositoryMetadataFor(repository), resourcePath));

		velocityEngine.mergeTemplate("repository.vm", "UTF-8", context, writer);
		return javaScriptResponse(writer.toString());
	}

	private String getResourcePath(String repository, UriComponentsBuilder uriBuilder) {
		String resourcePath = uriBuilder.path("/" + repository).build().toString();
		return resourcePath;
	}

	@RequestMapping(value="/{repository}-cola.js", method=GET)
	public ResponseEntity<String> colaAdapter(@PathVariable("repository") String repository, UriComponentsBuilder uriBuilder) {
		VelocityEngine velocityEngine = new VelocityEngine();
		initSpringResourceLoader(velocityEngine, "classpath:/jstemplates");
		
		StringWriter writer = new StringWriter();
		VelocityContext context = new VelocityContext();
		context.put("repoName", repository);
		velocityEngine.mergeTemplate("cola-adapter.vm", "UTF-8", context, writer);
		
		String jsModule = writer.toString();
		return javaScriptResponse(jsModule);
	}

	private Map<String, List<String>> buildQueryMethodMap(JpaRepositoryMetadata metadata, String resourcePath) {
		Map<String, List<String>> queryMethodMap = new HashMap<String, List<String>>();
		
		Map<String, RepositoryQueryMethod> queryMethods = metadata.queryMethods();
		for (String key : queryMethods.keySet()) {
			RepositoryQueryMethod queryMethod = queryMethods.get(key);
			List<String> paramNames = Arrays.asList(queryMethod.paramNames());
			queryMethodMap.put(queryMethod.method().getName(), paramNames);
		}
		
		return queryMethodMap;
	}
	
	private void initSpringResourceLoader(VelocityEngine velocityEngine, String resourceLoaderPath) {
		velocityEngine.setProperty(
				RuntimeConstants.RESOURCE_LOADER, SpringResourceLoader.NAME);
		velocityEngine.setProperty(
				SpringResourceLoader.SPRING_RESOURCE_LOADER_CLASS, SpringResourceLoader.class.getName());
		velocityEngine.setProperty(
				SpringResourceLoader.SPRING_RESOURCE_LOADER_CACHE, "true");
		velocityEngine.setApplicationAttribute(
				SpringResourceLoader.SPRING_RESOURCE_LOADER, new DefaultResourceLoader());
		velocityEngine.setApplicationAttribute(
				SpringResourceLoader.SPRING_RESOURCE_LOADER_PATH, resourceLoaderPath);
	}

	private ResponseEntity<String> javaScriptResponse(String jsModule) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "javascript"));
		ResponseEntity<String> response = new ResponseEntity<String>(jsModule, headers, HttpStatus.OK);
		return response;
	}
}
