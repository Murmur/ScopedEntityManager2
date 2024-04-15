package test.rest;

import java.util.*;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jakarta.rs.cfg.Annotations;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.ObjectMapper;

//@ApplicationPath("rest") path is configured in web.xml file
public class MyApplication extends ResourceConfig {
	
	public MyApplication() {
		Map<String, Object> props = new HashMap<>();        
		props.put(ServerProperties.WADL_FEATURE_DISABLE, true);  // disable "/rest/application.wadl" even if app had JAXB libs enabled
		setProperties(props);

		register(new JacksonJsonProvider(new Annotations[] { Annotations.JACKSON }));
		register(MapperContextResolver.class); // see createJsonGenerator(), use singleton ObjectMapper in JerseyJson app
		// register CDI @Inject classes: bind(ImplementationClass).to(InterfaceClass)
		register(new AbstractBinder() {
		    @Override protected void configure() {
		    	bind(MyService.class).to(MyService.class);
		    }
		});

		// register "/rest/*" resource service url entrypoints
		register(OrderService.class);
	}
    
	/**
	 * Create JsonGenerator instance, this is called by RestService classes.
	 * Generator is created from shared singleton ObjectMapper instance.
	 * @param providers		instance was injected in a service class
	 * @param os	output stream to be used in jsongenerator
	 * @return	new JsonGenerator
	 * @throws IOException
	 */
	public static JsonGenerator createJsonGenerator(Providers providers, OutputStream os) throws IOException {
		ObjectMapper mapper = getObjectMapper(providers);
		// generator should not close stream when jsonG.close() is called, most likely it's ServletOutputStream 
		JsonGenerator jsonG = mapper.getFactory().createGenerator(os, JsonEncoding.UTF8);
		jsonG.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
		return jsonG;
	}
	
	/**
	 * Return application-level singleton of ObjectMapper
	 * @param providers  see OrderService.java
	 * @return	ObjectMapper for json object handling
	 */
	public static ObjectMapper getObjectMapper(Providers providers) {
		// if providers==NULL then use a singleton hack, this provides access to ObjectMapper
		// without jersey request context. Backend jobs may use this to reuse a json mapper.
		if(providers==null) {
			return MapperContextResolver.getInstance().getContext(ObjectMapper.class);
		} else {
			ContextResolver<ObjectMapper> resolver = providers.getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE);
			return resolver.getContext(ObjectMapper.class);
		}
	}
		
}
