package test.rest;

import java.util.*;

import jakarta.ws.rs.core.Response; // javax
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.WebApplicationException;

public class RestException extends WebApplicationException {
	private static final long serialVersionUID = 1L;
	
	public RestException(Response res) { super(res); }
	
	public static RestException createNotFound(String err, String desc) {
		Map<String,Object> obj = new HashMap<String,Object>();
		obj.put("error", err==null ? "not_found" : err);
		if (desc!=null) obj.put("error_description", desc);
		ResponseBuilder builder = Response.status(Response.Status.NOT_FOUND);
		return new RestException(builder.entity(obj).build());
	}

	public static RestException createForbidden(String err, String desc) {
		Map<String,Object> obj = new HashMap<String,Object>();
		obj.put("error", err==null ? "forbidden" : err);
		if (desc!=null) obj.put("error_description", desc);
		ResponseBuilder builder = Response.status(Response.Status.FORBIDDEN);
		return new RestException(builder.entity(obj).build());
	}

	public static RestException createBadRequest(String err, String desc) {
		Map<String,Object> obj = new HashMap<String,Object>();
		obj.put("error", err==null ? "bad_request" : err);
		if (desc!=null) obj.put("error_description", desc);
		ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
		return new RestException(builder.entity(obj).build());
	}

	public static RestException createUnAuthorized(String err, String desc) {
		Map<String,Object> obj = new HashMap<String,Object>();
		obj.put("error", err==null ? "unauthorized" : err);
		if (desc!=null) obj.put("error_description", desc);
		ResponseBuilder builder = Response.status(Response.Status.UNAUTHORIZED);
		return new RestException(builder.entity(obj).build());
	}

	public static RestException createInternalError(String err, String desc) {
		Map<String,Object> obj = new HashMap<String,Object>();
		obj.put("error", err==null ? "internal_error" : err);
		if (desc!=null) obj.put("error_description", desc);
		ResponseBuilder builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
		return new RestException(builder.entity(obj).build());
	}

	
	/*public RestException(int status, String msg, String desc) {
		super(Response.status(status)
				.entity(createEntity(msg, desc)).type("application/json").build());		
	}
	
	private Map<String,String> createEntity(String msg, String desc) {
		NotFoundException.x();
		return null;
	}*/
	
}
