package test.rest;

import java.util.*;
import java.io.IOException;
import java.io.OutputStream;

//import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application; // javax.ws.rs.core.Application;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.jakarta.rs.cfg.Annotations;
//import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

//@ApplicationPath("rest") path is configured in web.xml file
public class MyApplication extends Application {

	@Override public Set<Class<?>> getClasses() {	
		// ResourceService and Provider classes
		Set<Class<?>> list = new HashSet<Class<?>>();
		list.add(MapperContextResolver.class); // see also createJsonGenerator()
		list.add(JsonProvider.class); // provider is a singleton
		list.add(OrderService.class);
		return list;
	}
	
	//@Override public Set<Object> getSingletons() { // deprecated, use JsonProvider subclass
	//	Set<Object> list = new HashSet<Object>();
	//	// provider looks for shared singleton ObjectMapper by MapperContextResolver class
	//	list.add( new JacksonJsonProvider( new Annotations[] { Annotations.JACKSON } ) );
	//  return list;
	//}
	
    @Override public Map<String, Object> getProperties() {
       Map<String, Object> props = new HashMap<>();        
       props.put("jersey.config.server.wadl.disableWadl", true); // disable wadl.xml
       return props;
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
		ContextResolver<ObjectMapper> resolver = providers.getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE);
		return resolver.getContext(ObjectMapper.class);
	}
		
}
