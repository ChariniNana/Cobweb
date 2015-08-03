package com.cobweb.io.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.cobweb.io.service.DeleteService;
import com.cobweb.io.service.ReadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Path("/sensor/subscribers")
public class RestSensorSubscribers {

	/** The Constant JSON_ERROR. */
	private static final String JSON_ERROR				= "{\"status\":\"JSON Parsing error\"}";	
	
	/** The Constant UNKNOWN_SENSOR_ID. */
	private static final String UNKNOWN_SENSOR_ID 		= "{\"status\":\"Unknown Sensor Id or Unauthorized Sensor\"}";
	
	
	/**
	 * Gets the subscribers.
	 *
	 * @param sensorId the sensor id
	 * @return the subscribers
	 */
	@GET
	@Path("{sensorId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSubscribers(@PathParam("sensorId") String sensorId){
		
		ReadService readService = new ReadService();
		Subject currentUser = SecurityUtils.getSubject();
		String email = (String) currentUser.getPrincipal();
		String userId = readService.getUserId(email);
		
		List<String> userSensorIdList = readService.getSensorIdList(userId);
		
		if(!userSensorIdList.contains(sensorId))
			return UNKNOWN_SENSOR_ID;

		List<String> subscribersIdList = readService.getSensorSubscriptionList(sensorId);
		
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

		try {
			return objectWriter.writeValueAsString(subscribersIdList);
		} catch (JsonProcessingException e) {		
			return JSON_ERROR;
		}		
	}
	
	
	/**
	 * Delete sensor.
	 *
	 * @param sensorId the sensor id
	 * @param subscribersId the subscribers id
	 * @return the response
	 */
	@DELETE
	@Path("{sensorId}")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response deleteSensor(@PathParam("sensorId") String sensorId, String subscribersId) {
		
		ReadService readService = new ReadService();
		Subject currentUser = SecurityUtils.getSubject();
		String email = (String) currentUser.getPrincipal();
		String userId = readService.getUserId(email);
		
		List<String> userSensorIdList = readService.getSensorIdList(userId);
		List<String> subscribersIdList = readService.getSensorSubscriptionList(sensorId);
		
		if(!userSensorIdList.contains(sensorId))
			return Response.status(Response.Status.UNAUTHORIZED).build();
		
		if(!subscribersIdList.contains(subscribersId))
			return Response.status(Response.Status.UNAUTHORIZED).build();
		
		DeleteService deleteService = new DeleteService();
		
		if(deleteService.deleteUserSensorSubscription(userId, sensorId))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NOT_ACCEPTABLE).build();
		
	}
}
