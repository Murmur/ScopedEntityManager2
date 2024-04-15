package test.rest;

import java.io.IOException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
	static final DateTimeFormatter instantFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	// "2024-04-14T19:52:40" without timezone
	static final DateTimeFormatter localFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	// "2024-04-14T19:52:40+0300" with a system timezone
	static final DateTimeFormatter zonedFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
	
	public MyJsonModule() {
		// Instant is +/- offset from unixepoc UTC (seconds+nanoseconds)
		addSerializer(Instant.class, new InstantSerializer()); // Object-to-JSON
		addDeserializer(Instant.class, new InstantDeserializer()); // JSON-to-Object
		// LocalDateTime is a calendar time without a timezone info
		addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
		addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
		// ZonedDateTime is a calendar time with a timezone info
		addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer());
		addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
		// legacy Calendar is handled by mapper.setDateFormat(), see MapperContextResolver.java 
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

class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {	
	@Override public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString(value.format(MyJsonModule.localFormat));	
	}	  
}

class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
	@Override public LocalDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException {
		return LocalDateTime.from(MyJsonModule.localFormat.parse(p.getValueAsString()));
	}
}

class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {	
	@Override public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString(value.format(MyJsonModule.zonedFormat));	
	}	  
}

class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
	@Override public ZonedDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException {
		return ZonedDateTime.from(MyJsonModule.zonedFormat.parse(p.getValueAsString()));
	}
}
