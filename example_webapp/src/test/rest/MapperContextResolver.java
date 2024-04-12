package test.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jakarta.ws.rs.Consumes; //javax
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * RestService classes may use ContextResolver to acquire
 * shared singleton of ObjectMapper.
 */
@Provider
@Produces("application/json")
@Consumes("application/json")
public class MapperContextResolver implements ContextResolver<ObjectMapper> {
    private final ObjectMapper mapper;

    public MapperContextResolver() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // support java8 datetime types
		mapper.disable(SerializationFeature.INDENT_OUTPUT);
		//mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // enable=1433435279692 (utcMillis)
		//mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // disable=2015-06-04T16:25:27.056+0000 (ISO8601_utc) 
		DateFormat dtf = SimpleDateFormat.getDateTimeInstance();
		((SimpleDateFormat)dtf).applyPattern("yyyy-MM-dd'T'HH:mm:ssZ"); // 2015-06-04T19:25:27+0300, use customized format
		mapper.setDateFormat(dtf);
    }

    @Override public ObjectMapper getContext(Class<?> cls) {
        return mapper;
    }
}
