package test.rest;

import jakarta.ws.rs.ext.Provider;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jakarta.rs.cfg.Annotations;

@Provider
public class JsonProvider extends JacksonJsonProvider {
	public JsonProvider() {
		// provider looks for a shared singleton ObjectMapper by MapperContextResolver class		
		super( new Annotations[] { Annotations.JACKSON } ) ;
	}
}
