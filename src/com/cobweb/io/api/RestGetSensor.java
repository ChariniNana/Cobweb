package com.cobweb.io.api;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.cobweb.io.meta.Sensor;
import com.cobweb.io.service.DeleteService;
import com.cobweb.io.service.GraphFactory;
import com.cobweb.io.service.ReadService;
import com.cobweb.io.transformers.VertexToSensor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tinkerpop.blueprints.Vertex;

/**
 * The Class RestGetSensor.
 * @author Yasith Lokuge
 */
@Path("/api/sensor")
public class RestGetSensor {

	/** The Constant JSON_ERROR. */
	private static final String JSON_ERROR				= "{\"error\":\"JSON Parsing error\"}";	
	
	/** The Constant UNKNOWN_SENSOR_ID. */
	private static final String UNKNOWN_SENSOR_ID 		= "{\"error\":\"Unknown Sensor Id or Unauthorized Sensor\"}";
	
	/**
	 * Gets the sensor.
	 *
	 * @param sensorId the sensor id
	 * @return the sensor
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{sensorId}")
	public String getSensor(@PathParam("sensorId") String sensorId){
		
		ReadService readService = new ReadService();
		Subject currentUser = SecurityUtils.getSubject();
		String email = (String) currentUser.getPrincipal();
		String userId = readService.getUserId(email);
		
		List<String> userSensorIdList = readService.getSensorIdList(userId);
		List<String> subscribedSensorIdList = readService.getSubscribedSensorIdList(userId);
		
		
		if(!(userSensorIdList.contains(sensorId) || subscribedSensorIdList.contains(sensorId)))
			return UNKNOWN_SENSOR_ID;
		
		GraphFactory graphFactory = new GraphFactory();
		VertexToSensor toSensor = new VertexToSensor();
		Vertex sensorVertex = graphFactory.getSensorVertex(sensorId);
		Sensor sensor = toSensor.transform(sensorVertex);
		
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

		try {
			return objectWriter.writeValueAsString(sensor);
		} catch (JsonProcessingException e) {		
			return JSON_ERROR;
		}
	}
	
	/**
	 * Delete sensor.
	 *
	 * @param sensorId the sensor id
	 * @return the response
	 */
	@DELETE
	@Path("{sensorId}")
	public Response deleteSensor(@PathParam("sensorId") String sensorId) {
		
		ReadService readService = new ReadService();
		Subject currentUser = SecurityUtils.getSubject();
		String email = (String) currentUser.getPrincipal();
		String userId = readService.getUserId(email);
		
		List<String> userSensorIdList = readService.getSensorIdList(userId);
		
		if(!userSensorIdList.contains(sensorId))
			return Response.status(Response.Status.UNAUTHORIZED).build();
		
		DeleteService deleteService = new DeleteService();
		
		if(deleteService.deleteSensor(sensorId))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NOT_ACCEPTABLE).build();
		
	}
}
