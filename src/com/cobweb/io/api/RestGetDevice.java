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

import com.cobweb.io.meta.Device;
import com.cobweb.io.service.DeleteService;
import com.cobweb.io.service.GraphFactory;
import com.cobweb.io.service.ReadService;
import com.cobweb.io.transformers.VertexToDevice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tinkerpop.blueprints.Vertex;

/**
 * The Class RestGetDevice.
 * @author Yasith Lokuge
 */
@Path("/device")
public class RestGetDevice {

	/** The Constant JSON_ERROR. */
	private static final String JSON_ERROR				= "{\"error\":\"JSON Parsing error\"}";	
	
	/** The Constant UNKNOWN_DEVICE_ID. */
	private static final String UNKNOWN_DEVICE_ID 		= "{\"error\":\"Unknown Device Id or Unauthorized Device\"}";
	
	/**
	 * Gets the device.
	 *
	 * @param deviceId the device id
	 * @return the device
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{deviceId}")
	public String getDevice(@PathParam("deviceId") String deviceId){
		
		ReadService readService = new ReadService();
		Subject currentUser = SecurityUtils.getSubject();
		String email = (String) currentUser.getPrincipal();
		String userId = readService.getUserId(email);
		
		List<String> userDeviceIdList = readService.getDeviceIdList(userId);
		List<String> subscribedDeviceIdList = readService.getSubscribedDeviceIdList(userId);
		
		
		if(!(userDeviceIdList.contains(deviceId) || subscribedDeviceIdList.contains(deviceId)))
			return UNKNOWN_DEVICE_ID;
		
		GraphFactory graphFactory = new GraphFactory();
		VertexToDevice toDevice = new VertexToDevice();
		Vertex deviceVertex = graphFactory.getDeviceVertex(deviceId);
		Device device = toDevice.transform(deviceVertex);
		
		List<String> attachedSensorList = readService.getAttachedSensorList(deviceId);
		String ownerId = readService.getParentUserIdFromDevice(deviceId);
		
		device.setSensorIdList(attachedSensorList);
		device.setParentUserId(ownerId);
		
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

		try {
			return objectWriter.writeValueAsString(device);
		} catch (JsonProcessingException e) {		
			return JSON_ERROR;
		}
	}
	
	/**
	 * Delete device.
	 *
	 * @param deviceId the device id
	 * @return the response
	 */
	@DELETE
	@Path("{deviceId}")
	public Response deleteDevice(@PathParam("deviceId") String deviceId) {
		
		ReadService readService = new ReadService();
		Subject currentUser = SecurityUtils.getSubject();
		String email = (String) currentUser.getPrincipal();
		String userId = readService.getUserId(email);
		
		List<String> userDeviceIdList = readService.getDeviceIdList(userId);
		
		if(!userDeviceIdList.contains(deviceId))
			return Response.status(Response.Status.UNAUTHORIZED).build();
		
		DeleteService deleteService = new DeleteService();
		
		if(deleteService.deleteDevice(deviceId))
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NOT_ACCEPTABLE).build();
		
	}
}
