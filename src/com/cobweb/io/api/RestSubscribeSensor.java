package com.cobweb.io.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.cobweb.io.service.DeleteService;
import com.cobweb.io.service.ReadService;
import com.cobweb.io.utils.CobwebWeaver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


/**
 * The Class RestSubscribeSensor.
 * @author Yasith Lokuge
 */
@Path("/sensor/subscriptions")
public class RestSubscribeSensor {
	
	/** The Constant JSON_ERROR. */
	private static final String JSON_ERROR				= "{\"error\":\"JSON Parsing error\"}";	
	
	/** The Constant SUCCESS. */
	private static final String SUCCESS					= "SUCCESS";
	
	/** The Constant ERROR. */
	private static final String ERROR					= "ERROR";
	
	/** The Constant UNKNOWN_SENSOR_ID. */
	private static final String UNKNOWN_SENSOR_ID 		= "Unknown Sensor Id or Unauthorized Sensor";
	
	
	/**
	 * Gets the sensor subscriptions.
	 *
	 * @return the sensor subscriptions
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSensorSubscriptions(){
		
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
		ReadService readService = new ReadService();
		
		Subject currentUser = SecurityUtils.getSubject();
		String email = (String) currentUser.getPrincipal();
		String userId = readService.getUserId(email);
				
		List<String> subscriptionIdList = readService.getUserSubscribedSensorList(userId);
				
		try {
			return objectWriter.writeValueAsString(subscriptionIdList);
		} catch (JsonProcessingException e) {
			return JSON_ERROR;
		}		
	}
	
	
	/**
	 * Delete sensor subscription.
	 *
	 * @param sensorId the sensor id
	 * @return the string
	 */
	@DELETE
	@Consumes(MediaType.TEXT_PLAIN)
	public String deleteSensorSubscription(String sensorId){
		
		DeleteService deleteService = new DeleteService();
		ReadService readService = new ReadService();
		
		Subject currentUser = SecurityUtils.getSubject();
		String email = (String) currentUser.getPrincipal();
		String userId = readService.getUserId(email);
		List<String> sensorIdList	= readService.getSensorIdList(userId);

		if(!sensorIdList.contains(sensorId))					
			return UNKNOWN_SENSOR_ID;			
		
		boolean status = deleteService.deleteUserSensorSubscription(userId, sensorId);
		
		if(status)
			return SUCCESS;
		else
			return ERROR;	
	}

	/**
	 * Subscribe.
	 *
	 * @param sensorId the sensor id
	 * @return the string
	 */
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public String subscribe(String sensorId){
		
		ReadService readService = new ReadService();
		Subject currentUser = SecurityUtils.getSubject();
		String email = (String) currentUser.getPrincipal();
		String userId = readService.getUserId(email);
		
		String ownerId = readService.getParentUserIdFromSensor(sensorId);
		List<String> friendList = readService.getFriendsIdList(userId);
		
		if(!friendList.contains(ownerId))
			return UNKNOWN_SENSOR_ID;	
		
		CobwebWeaver cobwebWeaver = new CobwebWeaver();
		cobwebWeaver.addSensorSubscription(userId, sensorId);
		
		return SUCCESS;
	}
}
