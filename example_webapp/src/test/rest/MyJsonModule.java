package test.rest;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Custom module to add custom serializer-deserializer for java datatypes.
 * Module is registered to ObjectMapper then this is a default formatter for datatypes. 
 */
public class MyJsonModule extends SimpleModule {
	private static final long serialVersionUID = 1L;

	// "2024-04-14T16:52:40.911Z"
	static final DateTimeFormatter instantFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	public MyJsonModule() {
		addSerializer(Instant.class, new InstantSerializer()); // Object-to-JSON
		addDeserializer(Instant.class, new InstantDeserializer()); // JSON-to-Object
	}
}

class InstantSerializer extends JsonSerializer<Instant> {	
	@Override public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		// add UTC timezone to Instant time then format
		gen.writeString(value.atZone(ZoneOffset.UTC).format(MyJsonModule.instantFormat));		
	}	  
}

class InstantDeserializer extends JsonDeserializer<Instant> {
	@Override public Instant deserialize(JsonParser p, DeserializationContext context) throws IOException {
		LocalDateTime dt = LocalDateTime.parse(p.getValueAsString(), MyJsonModule.instantFormat);
		return dt.toInstant(ZoneOffset.UTC);
	}
}
