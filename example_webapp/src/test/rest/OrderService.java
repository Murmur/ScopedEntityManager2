package test.rest;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest; // replace javax.*
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET; // replace javax.*
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.ext.Providers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
//import es.claro.persistence.ScopedEntityManager;
import es.claro.persistence.PersistenceManager;
import test.JPAUtils;
import test.OrderHeader;
import test.OrderHeader2;
import test.OrderRow;

// Singleton: one instance per webapp context, default is a new instance per http request.
@Path("") @Singleton
public class OrderService {
	@Context private Providers providers;
	
	@Inject private MyService myService;

	@GET @Path("/{apiver}/test1")
	@Produces({"text/plain;charset=UTF-8"})
	public final Response test1(
			@Context HttpServletRequest req,
			@PathParam("apiver") String apiver,
			@QueryParam("key") @DefaultValue("defval") String key) {
		
		String debugReq = req.getContextPath() + "," + req.getHeader("User-Agent");
				
	    Response.ResponseBuilder rb =
	      Response
	        .ok(
	        	"Hello "+key + ", api="+apiver
	        	+ "\nreq="+debugReq
	        	+ "\nmyServiceCounter="+myService.getCounter()
	        	,MediaType.TEXT_PLAIN
	        )
	        .status(Response.Status.OK);
	    return rb.build();
	}
	
	@GET @Path("/{apiver}/test1b")
	@Produces({"application/json;charset=UTF-8"})
	public final Response test1b(
			@Context HttpServletRequest req,
			@PathParam("apiver") String apiver,
			@QueryParam("key") @DefaultValue("defval") String key) {
		
		// hardcoded json without jackson json generator
		String str = "{\n"
			+ "\"key1\":\"value1\","
			+ "\n\"key2\": " + System.currentTimeMillis() +","
			+ "\n\"key3\":\"value3 \uD83D\uDE00 \uD834\uDD60 \uD83D\uDCEB\""  +","
			+ "\n\"myServiceCounter\": " + myService.getCounter()
			+ "\n}";
		
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);		
	    Response.ResponseBuilder rb = Response.ok(str, "application/json;charset=UTF-8")
	    	.cacheControl(cc).status(Response.Status.OK);
	  	return rb.build();
	}		

	@GET @Path("/{apiver}/test1c")
	@Produces({"application/json;charset=UTF-8"})
	public final Response test1c(
			@Context HttpServletRequest req,
			@PathParam("apiver") String apiver,
			@QueryParam("key") @DefaultValue("defval") String key) {

		Map<String,String> stats = PersistenceManager.getInstance().getStatistics();
		stats.put("key1", "Map to json conversion test");
		stats.put("myServiceCounter", ""+myService.getCounter());
		
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);		
	    Response.ResponseBuilder rb = Response.ok(stats, "application/json;charset=UTF-8")
	    	.cacheControl(cc).status(Response.Status.OK);
	  	return rb.build();
	}

	@GET @Path("/{apiver}/test1d")
	@Produces({"application/json;charset=UTF-8"})
	public final Response test1d(
			@Context HttpServletRequest req,
			@PathParam("apiver") String apiver,
			@QueryParam("key") @DefaultValue("defval") String key) throws JsonProcessingException {		

		// json format of Calendar and Instant is configured in ObjectMapper(MapperContextResolver.java)
		List<Object> values=new ArrayList<Object>();
		values.add("first");
		values.add(2);
		values.add( Arrays.asList("Calendar", Calendar.getInstance()) ); // "2024-04-14T19:52:40+0300"		
		values.add( Arrays.asList("Instant", Instant.now()) ); // "2024-04-14T16:52:40.231Z", without custom formatter "2024-04-14T16:52:40.2317344" 
		values.add( Arrays.asList("LocalDateTime", LocalDateTime.now()) ); // "2024-04-14T19:52:40" without timezone info

		ZonedDateTime zonedNow = ZonedDateTime.now();
		values.add( Arrays.asList("ZonedDateTime", zonedNow) ); // "2024-04-14T19:52:40+0300"		
		DateTimeFormatter utcFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC"));
		values.add( Arrays.asList("ZonedDateTimeUTC", utcFormat.format(zonedNow)) ); // "2024-04-14T16:52:40Z" 
		DateTimeFormatter nycFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ").withZone(ZoneId.of("America/New_York")); 
		values.add( Arrays.asList("ZonedDateTimeNewYork", nycFormat.format(zonedNow)) ); // "2024-04-14T12:52:40-0400" 
		DateTimeFormatter bkkFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ").withZone(ZoneId.of("Asia/Bangkok")); 
		values.add( Arrays.asList("ZonedDateTimeBangkok", bkkFormat.format(zonedNow)) ); // "2024-04-14T23:52:40+0700" 
		
		values.add("myServiceCounter is "+myService.getCounter());
		values.add("last");

		ObjectMapper om = MyApplication.getObjectMapper(null); // test getter without a providers context value (providers)
		String data = om.writeValueAsString(values);

		CacheControl cc = new CacheControl();
		cc.setNoCache(true);		
	    Response.ResponseBuilder rb = Response.ok(data, "application/json;charset=UTF-8")
	    	.cacheControl(cc).status(Response.Status.OK);
	  	return rb.build();
	}	
	
	@GET @Path("/{apiver}/test2")
	@Produces({"application/json;charset=UTF-8"})
	public final Response test2(
			@Context HttpServletRequest req,
			@PathParam("apiver") String apiver,
			@QueryParam("key") @DefaultValue("defval") String key) {
		
		// use streaming output and Jackson JSON generator
		StreamingOutput stream = new StreamingOutput() {
			@Override public void write(OutputStream os) throws IOException, WebApplicationException {
				JsonGenerator jsonG = MyApplication.createJsonGenerator(providers, os);
				try {
					jsonG.writeStartObject();
					
					jsonG.writeObjectFieldStart("request");
					jsonG.writeStringField("context", req.getContextPath());
					jsonG.writeStringField("userAgent", req.getHeader("User-Agent"));
					jsonG.writeNumberField("myServiceCounter", myService.getCounter());
					jsonG.writeEndObject();

					jsonG.writeArrayFieldStart("items");
					for(int idx=0; idx<5; idx++) {
						OrderHeader oh = new OrderHeader();
						oh.setCustId(1001+idx);
						oh.setComment("Comment for " + oh.getCustId());
						oh.setUpdated(Calendar.getInstance());
						
						if(idx==1 || idx==3) {
							oh.setRows( new ArrayList<OrderRow>() );
							for(int idxB=0; idxB<3; idxB++) {
								OrderRow row = new OrderRow();
								row.setComment("Row idx "+idx+"_"+idxB);
								row.setQuantity(1+idxB);
								row.setUpdated(oh.getUpdated());
								oh.getRows().add(row);
							}
						}
						
						jsonG.writeObject(oh);
					}
					jsonG.writeEndArray();
					
					jsonG.writeEndObject();
					jsonG.flush();
				} finally {
					jsonG.close();
				}				
			}
		};
		
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);
		return Response.ok().type("application/json;charset=UTF-8")
			.cacheControl(cc).entity(stream).build();
	}	

	// /jpaexample2/rest/v1/orders?sort=asc|desc
	@GET @Path("/{apiver}/orders")
	@Produces({"application/json;charset=UTF-8"})
	public List<OrderHeader> getOrders(
			@Context HttpServletRequest req,
			@PathParam("apiver") String apiver,
			@QueryParam("sort") @DefaultValue("") String sort) {
		// ScopedContextListener closes EM at the end of http request(rollback+close if not committed)
		//ScopedEntityManager em = (ScopedEntityManager)PersistenceManager.getInstance().getEntityManager();
		EntityManager em = PersistenceManager.getInstance().getEntityManager();

		// test a typed JPA query
		List<OrderHeader> list;
		if(!sort.isEmpty()) {
			String sql = "SELECT bean FROM OrderHeader bean ORDER BY bean.custId %s, bean.updated %s";
			TypedQuery<OrderHeader> q;
			if(sort.equals("asc")) q = em.createQuery(String.format(sql, sort,sort), OrderHeader.class);
			else if(sort.equals("desc")) q = em.createQuery(String.format(sql, sort,sort), OrderHeader.class);
			else throw RestException.createBadRequest("Invalid argument", "Invalid argument sort ("+sort+")");
			list = (List<OrderHeader>)q.getResultList();
		} else {
			list = JPAUtils.findAll(em, OrderHeader.class);
		}
		return list;
	}	

	// /jpaexample2/rest/v1/orders2?sort=asc|desc
	@GET @Path("/{apiver}/orders2")
	@Produces({"application/json;charset=UTF-8"})
	public List<OrderHeader2> getOrders2(
			@Context HttpServletRequest req,
			@PathParam("apiver") String apiver,
			@QueryParam("sort") @DefaultValue("") String sort) {
		// ScopedContextListener closes EM at the end of http request(rollback+close if not committed)
		//ScopedEntityManager em = (ScopedEntityManager)PersistenceManager.getInstance().getEntityManager();
		EntityManager em = PersistenceManager.getInstance().getEntityManager();

		// test a typed JPA query, use OrderHeader2 jpa object
		List<OrderHeader2> list;
		if(!sort.isEmpty()) {
			String sql = "SELECT bean FROM OrderHeader2 bean ORDER BY bean.custId %s, bean.updated %s";
			TypedQuery<OrderHeader2> q;
			if(sort.equals("asc")) q = em.createQuery(String.format(sql, sort,sort), OrderHeader2.class);
			else if(sort.equals("desc")) q = em.createQuery(String.format(sql, sort,sort), OrderHeader2.class);
			else throw RestException.createBadRequest("Invalid argument", "Invalid argument sort ("+sort+")");
			list = (List<OrderHeader2>)q.getResultList();
		} else {
			list = JPAUtils.findAll(em, OrderHeader2.class);
		}
		return list;
	}	

}
