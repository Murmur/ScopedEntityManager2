package test.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jakarta.ws.rs.Consumes; //javax
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * RestService classes may use ContextResolver to acquire
 * shared singleton of ObjectMapper.
 */
@Provider
@Produces("application/json")
@Consumes("application/json")
public class MapperContextResolver implements ContextResolver<ObjectMapper> {
	private static MapperContextResolver instance; // hack: singleton access to ObjectMapper outside of Jersey http request
	private final ObjectMapper mapper; // thread-safe if we don't change a configuration
	
	public MapperContextResolver() {
		//System.out.println("**"+this.getClass().getName()+","+System.currentTimeMillis());
		mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.INDENT_OUTPUT);
		mapper.findAndRegisterModules(); //mapper.registerModule(new JavaTimeModule()); // support java8 datetime types

		mapper.registerModule( new MyJsonModule() ); // customize few Java8 datetime formatters (Instant time type)
		
		//mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // enable=1433435279692 (utcMillis)
		//mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // disable=2015-06-04T16:25:27.056+0000 (ISO8601_utc) 
		DateFormat dtf = SimpleDateFormat.getDateTimeInstance();
		((SimpleDateFormat)dtf).applyPattern("yyyy-MM-dd'T'HH:mm:ssZ"); // 2015-06-04T19:25:27+0300, use customized format
		mapper.setDateFormat(dtf); // this works for legacy Calendar datatype
		instance = this;
	}
	
	public static MapperContextResolver getInstance() { return instance; }

	@Override public ObjectMapper getContext(Class<?> cls) {
		return mapper;
	}
}
