package com.cobweb.io.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.cobweb.io.meta.Device;
import com.cobweb.io.meta.DeviceType;
import com.cobweb.io.meta.LoggedUser;
import com.cobweb.io.meta.Payload;
import com.cobweb.io.meta.Sensor;
import com.cobweb.io.meta.SensorType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Vertex;


/**
 * The Class ReadService.
 *
 * @author Yasith Lokuge
 */

public class ReadService implements AbstractService{

	/** The invalid. */
	private final String INVALID = "Invalid user name or password"; 
	
	/**
	 * Instantiates a new read service.
	 *
	 * @param device the device
	 */
	public ReadService(Device device){
		
	}
	
	/**
	 * Instantiates a new read service.
	 *
	 * @param sensor the sensor
	 */
	public ReadService(Sensor sensor){
		
	}
	
	/**
	 * Instantiates a new read service.
	 */
	public ReadService(){
		
	}


	
	/**
	 * Read.
	 *
	 * @param loggedUser the logged user
	 * @param data the data
	 * @return the string
	 */
	public String read(LoggedUser loggedUser,String data){
	
		String email= loggedUser.getEmail();
		String password = loggedUser.getPassword();
		String info;
		
		List<ODocument> result =  graph.getRawGraph().query(new OSQLSynchQuery<Object>("select from User where email=\""+email+"\" and password=\""+password+"\""));
		 		
		if(!result.isEmpty()){			
			Vertex v = graph.getVertex(result.get(0).getIdentity());
			info = v.getProperty(data);
			return info;
		}else{
			return INVALID;
		}
		
	}
	
	/**
	 * Gets the user names list.
	 *
	 * @return the list
	 */
	public List<String> getUserNamesList(){
		
		List<String> userNameList = new ArrayList<>();
		List<ODocument> result =  graph.getRawGraph().query(new OSQLSynchQuery<Object>("Select email from User"));		
		
		for (ODocument oDocument : result) {
			userNameList.add(oDocument.field("email"));
		}		
		return userNameList;		
	}
	
	
	/**
	 * Check user name exists.
	 *
	 * @param email the email
	 * @return true, if successful
	 */
	public boolean checkUserNameExists(String email){				
		try {
			ODocument result = (ODocument) graph.getRawGraph().query(new OSQLSynchQuery<Object>("Select email from User where email='"+email+"'")).get(0);
			String data = result.field("email");
			return data.equals(email);
		} catch (Exception e) {			
			return false;
		}	
	}
	
	/**
	 * Check password exists.
	 *
	 * @param password the password
	 * @return true, if successful
	 */
	public boolean checkPasswordExists(String password){				
		try {
			ODocument result = (ODocument) graph.getRawGraph().query(new OSQLSynchQuery<Object>("Select password from User where password='"+password+"'")).get(0);
			String data = result.field("password");
			return data.equals(password);
		} catch (Exception e) {			
			return false;
		}	
	}
	
	/**
	 * Gets the user id.
	 *
	 * @param email the email
	 * @return the user id
	 */
	public String getUserId(String email){
		ODocument result =  (ODocument) graph.getRawGraph().query(new OSQLSynchQuery<Object>("Select idValue from User where email='"+email+"'")).get(0);
		String idValue = result.field("idValue");		
		return idValue;
	}

	/**
	 * Gets the salt.
	 *
	 * @param userId the user id
	 * @return the salt
	 */
	public String getSalt(String userId){		
		ODocument result =  (ODocument) graph.getRawGraph().query(new OSQLSynchQuery<Object>("Select salt from User where idValue='"+userId+"'")).get(0);
		String salt = result.field("salt");
		return salt;
	}
	
	
	/**
	 * Gets the device id list.
	 *
	 * @param userId the user id
	 * @return the device id list
	 */
	public List<String> getDeviceIdList(String userId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(out('UserHasDevices')[isDeleted = false]) from User where idValue='"+userId+"'"));
		List<String>	deviceIdList = new ArrayList<String>();
		
		for (ODocument result:resultList) {			
			deviceIdList.add(result.field("idValue"));			
		}		
		return deviceIdList;		
	}
	
	
	/**
	 * Gets the sensor id list.
	 *
	 * @param userId the user id
	 * @return the sensor id list
	 */
	public List<String> getSensorIdList(String userId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(out('UserHasDevices')[isDeleted = false].out('DeviceHasSensors')[isDeleted = false]) from User where idValue='"+userId+"'"));
		List<String>	sensorIdList = new ArrayList<String>();
		
		for (ODocument result:resultList) {			
			sensorIdList.add(result.field("idValue"));			
		}		
		return sensorIdList;		
	}
	
	
	
	/**
	 * Gets the sensor list.
	 *
	 * @param userId the user id
	 * @return the sensor list
	 */
	public List<Sensor> getSensorList(String userId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(out('UserHasDevices')[isDeleted = false].out('DeviceHasSensors')[isDeleted = false]) from User where idValue='"+ userId+"'"));
		List<Sensor>	sensorList = new ArrayList<Sensor>();
		
		for (ODocument result:resultList) {
			
			String name 		= result.field("name"); 
			String id 			= result.field("idValue");
			String description 	= result.field("description");
			String sensorType 	= result.field("sensorType");
			String otherType 	= result.field("otherType");
			String imageUrl		= result.field("imageUrl");
			String parentDevice = getParentDeviceIdFromSensor(id);
			
			Sensor sensor = new Sensor(name, description, SensorType.valueOf(sensorType));
			sensor.setId(id);
			sensor.setOtherType(otherType);
			sensor.setParentDeviceId(parentDevice);
			
			try {
				sensor.setImageUrl(new URL(imageUrl));
			} catch (MalformedURLException e) {				
				e.printStackTrace();
			}						
			sensorList.add(sensor);
		}		
		return sensorList;				
	}
	
	
	/**
	 * Gets the device list.
	 *
	 * @param userId the user id
	 * @return the device list
	 */
	public List<Device> getDeviceList(String userId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(out('UserHasDevices')[isDeleted = false]) from User where idValue='"+ userId+"'"));
		List<Device>	deviceList = new ArrayList<Device>();
		
		for (ODocument result:resultList) {
			
			String name 		= result.field("name"); 
			String id 			= result.field("idValue");
			String description 	= result.field("description");
			String deviceType 	= result.field("deviceType");
			String otherType 	= result.field("otherType");
			String imageUrl		= result.field("imageUrl");
			
			List<String> attachedSensorList = getAttachedSensorList(id);
			
			Device device = new Device(name, description, DeviceType.valueOf(deviceType));
			device.setId(id);
			device.setOtherType(otherType);
			device.setSensorIdList(attachedSensorList);
			try {
				device.setImageUrl(new URL(imageUrl));
			} catch (MalformedURLException e) {				
				e.printStackTrace();
			}						
			deviceList.add(device);
		}		
		return deviceList;				
	}
	
	/**
	 * Check device exists.
	 *
	 * @param deviceId the device id
	 * @return true, if successful
	 */
	public boolean checkDeviceExists(String deviceId){
		try {
			ODocument result = (ODocument) graph.getRawGraph().query(new OSQLSynchQuery<Object>("Select idValue from Device where idValue='"+deviceId+"'")).get(0);
			String data = result.field("idValue");
			return data.equals(deviceId);
		} catch (Exception e) {			
			return false;
		}
	}

	/**
	 * Gets the payload list.
	 *
	 * @param sensorId the sensor id
	 * @return the payload list
	 */
	public List<Payload> getDevicePayloadList(String deviceId){
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(out('DeviceHasPayload')[isDeleted = false]) from Device where idValue='"+deviceId+"'"));
		
		List<Payload>	payloadList = new ArrayList<Payload>();

		
		for (ODocument result:resultList) {
			
			String id = result.field("idValue");
			String message = result.field("message");
			String dateTime = result.field("dateTime");
			
			Payload payload = new Payload(message);
			payload.setId(id);
			payload.setMessage(message);
			
			payload.setDeviceId(deviceId);			
			String userId = getParentUserIdFromDevice(deviceId);		
			payload.setUserId(userId);			
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			
			
			try {
				Date date = format.parse(dateTime);
				payload.setDateTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			payloadList.add(payload);
		}
		
		return payloadList;		
	}
	
	
	/**
	 * Gets the sensor payload list.
	 *
	 * @param sensorId the sensor id
	 * @return the sensor payload list
	 */
	public List<Payload> getSensorPayloadList(String sensorId){
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(out('SensorHasPayload')[isDeleted = false]) from Sensor where idValue='"+sensorId+"'"));
		
		List<Payload>	payloadList = new ArrayList<Payload>();

		
		for (ODocument result:resultList) {
			
			String id = result.field("idValue");
			String message = result.field("message");
			String dateTime = result.field("dateTime");
			
			Payload payload = new Payload(message);
			payload.setId(id);
			payload.setMessage(message);
			payload.setSensorId(sensorId);
			
			String deviceId = getParentDeviceIdFromSensor(sensorId);
			String userId = getParentUserIdFromDevice(deviceId);
			
			payload.setDeviceId(deviceId);
			payload.setUserId(userId);
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			
			
			try {
				Date date = format.parse(dateTime);
				payload.setDateTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			payloadList.add(payload);
		}
		
		return payloadList;		
	}
	
	
	
	/**
	 * Gets the attached sensor list.
	 *
	 * @param deviceId the device id
	 * @return the attached sensor list
	 */
	public List<String> getAttachedSensorList(String deviceId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(out('DeviceHasSensors')[isDeleted = false]) from Device where idValue='"+deviceId+"'"));
		List<String>	sensorIdList = new ArrayList<String>();
		
		for (ODocument result:resultList) {			
			String id = result.field("idValue");			
			sensorIdList.add(id);
		}		
		return sensorIdList;		
	}
	
	
	/**
	 * Gets the parent device id.
	 *
	 * @param sensorId the sensor id
	 * @return the parent device id
	 */
	public String getParentDeviceIdFromSensor(String sensorId){
		
		ODocument result = (ODocument) graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(in('DeviceHasSensors').idValue) from Sensor where idValue='"+sensorId+"'")).get(0);
		String data = result.field("value");
		return data;		
	}
	
	/**
	 * Gets the parent device id from payload.
	 *
	 * @param payloadId the payload id
	 * @return the parent device id from payload
	 */
	public String getParentDeviceIdFromPayload(String payloadId){
		
		ODocument result = (ODocument) graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(in('DeviceHasPayload').idValue) from Payload where idValue='"+payloadId+"'")).get(0);
		String data = result.field("value");
		return data;		
	}
	
	/**
	 * Gets the parent sensor id from payload.
	 *
	 * @param payloadId the payload id
	 * @return the parent sensor id from payload
	 */
	public String getParentSensorIdFromPayload(String payloadId){
		
		ODocument result = (ODocument) graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(in('SensorHasPayload').idValue) from Payload where idValue='"+payloadId+"'")).get(0);
		String data = result.field("value");
		return data;		
	}
	
	
	
	/**
	 * Gets the parent user id.
	 *
	 * @param deviceId the device id
	 * @return the parent user id
	 */
	public String getParentUserIdFromDevice(String deviceId){
		
		ODocument result = (ODocument) graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(in('UserHasDevices').idValue) from Device where idValue='"+deviceId+"'")).get(0);
		String data = result.field("value");
		return data;		
	}
	
	/**
	 * Gets the parent user id from sensor.
	 *
	 * @param sensorId the sensor id
	 * @return the parent user id from sensor
	 */
	public String getParentUserIdFromSensor(String sensorId){
		
		ODocument result = (ODocument) graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(in('DeviceHasSensors').in('UserHasDevices').idValue) from Sensor where idValue='"+sensorId+"'")).get(0);
		String data = result.field("value");
		return data;		
	}
	
	/**
	 * Gets the subscribed device payloads.
	 *
	 * @param userId the user id
	 * @return the subscribed device payloads
	 */
	public List<Payload> getSubscribedDevicePayloads(String userId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(outE('UserSubscribesDevice')[isAccepted=true].inV().out('DeviceHasPayload')[isDeleted=false]) from User where idValue='"+userId+"'"));
		List<Payload>	payloadList = new ArrayList<Payload>();

		
		for (ODocument result:resultList) {
			
			String id = result.field("idValue");
			String message = result.field("message");
			String dateTime = result.field("dateTime");
			//String sensorId = getParentSensorIdFromPayload(id);
			
			Payload payload = new Payload(message);
			payload.setId(id);
			payload.setMessage(message);
			//payload.setSensorId(sensorId);
			
			String deviceId = getParentDeviceIdFromPayload(id);
			String ownerId = getParentUserIdFromDevice(deviceId);
			
			payload.setDeviceId(deviceId);
			payload.setUserId(ownerId);
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			
			
			try {
				Date date = format.parse(dateTime);
				payload.setDateTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			payloadList.add(payload);	
				
		}
		return payloadList;			
	}
	
	
	/**
	 * Gets the subscribed sensor payloads.
	 *
	 * @param userId the user id
	 * @return the subscribed sensor payloads
	 */
	public List<Payload> getSubscribedSensorPayloads(String userId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(outE('UserSubscribesSensor')[isAccepted=true].inV().out('SensorHasPayload')[isDeleted=false]) from User where idValue='"+userId+"'"));
		List<Payload>	payloadList = new ArrayList<Payload>();

		
		for (ODocument result:resultList) {
			
			String id = result.field("idValue");
			String message = result.field("message");
			String dateTime = result.field("dateTime");
			String sensorId = getParentSensorIdFromPayload(id);
			
			Payload payload = new Payload(message);
			payload.setId(id);
			payload.setMessage(message);
			payload.setSensorId(sensorId);
			
			String deviceId = getParentDeviceIdFromSensor(sensorId);
			String ownerId = getParentUserIdFromDevice(deviceId);
			
			payload.setDeviceId(deviceId);
			payload.setUserId(ownerId);
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			
			
			try {
				Date date = format.parse(dateTime);
				payload.setDateTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			payloadList.add(payload);	
				
		}
		return payloadList;			
	}
	
	
	/**
	 * Gets the subscribed sensor id list.
	 *
	 * @param userId the user id
	 * @return the subscribed sensor id list
	 */
	public List<String> getSubscribedSensorIdList(String userId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(outE('UserSubscribesSensor')[isAccepted=true].inV()) from User where idValue='"+userId+"'"));
		List<String>	sensorIdList = new ArrayList<String>();
		
		for (ODocument result:resultList) {			
			String id = result.field("idValue");			
			sensorIdList.add(id);
		}		
		return sensorIdList;
	}
	
	
	/**
	 * Gets the subscribed device id list.
	 *
	 * @param userId the user id
	 * @return the subscribed device id list
	 */
	public List<String> getSubscribedDeviceIdList(String userId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(outE('UserSubscribesDevice')[isAccepted=true].inV()) from User where idValue='"+userId+"'"));
		List<String>	deviceIdList = new ArrayList<String>();
		
		for (ODocument result:resultList) {			
			String id = result.field("idValue");			
			deviceIdList.add(id);
		}		
		return deviceIdList;
	}
	
	/**
	 * Gets the sensor payload id list.
	 *
	 * @param userId the user id
	 * @return the sensor payload id list
	 */
	public List<String> getSensorPayloadIdList(String userId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(out('UserHasDevices')[isDeleted = false].out('DeviceHasSensors')[isDeleted = false].out('SensorHasPayload')[isDeleted = false]) from User where idValue='"+userId+"'"));
		List<String>	sensorPayloadIdList = new ArrayList<String>();
		
		for (ODocument result:resultList) {			
			String id = result.field("idValue");			
			sensorPayloadIdList.add(id);
		}		
		return sensorPayloadIdList;		
	}
	
	/**
	 * Gets the device payload id list.
	 *
	 * @param userId the user id
	 * @return the device payload id list
	 */
	public List<String> getDevicePayloadIdList(String userId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(out('UserHasDevices')[isDeleted = false].out('DeviceHasPayload')[isDeleted = false]) from User where idValue='"+userId+"'"));
		List<String>	devicePayloadIdList = new ArrayList<String>();
		
		for (ODocument result:resultList) {			
			String id = result.field("idValue");			
			devicePayloadIdList.add(id);
		}		
		return devicePayloadIdList;		
	}
	

	/**
	 * Gets the device subscriber request list.
	 *
	 * @param deviceId the device id
	 * @return the device subscriber request list
	 */
	public List<String> getDeviceSubscriberRequestList(String deviceId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(inE('UserSubscribesDevice')[isAccepted=false].outV()) from Device where idValue='"+deviceId+"'"));
		List<String>	deviceSubscriberRequestList = new ArrayList<String>();
		
		for (ODocument result:resultList) {			
			String id = result.field("idValue");	
			deviceSubscriberRequestList.add(id);
		}		
		return deviceSubscriberRequestList;		
	}
	
	
	/**
	 * Gets the sensor subscriber request list.
	 *
	 * @param sensorId the sensor id
	 * @return the sensor subscriber request list
	 */
	public List<String> getSensorSubscriberRequestList(String sensorId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(inE('UserSubscribesSensor')[isAccepted=false].outV()) from Sensor where idValue='"+sensorId+"'"));
		List<String>	sensorSubscriberRequestList = new ArrayList<String>();
		
		for (ODocument result:resultList) {			
			String id = result.field("idValue");			
			sensorSubscriberRequestList.add(id);
		}		
		return sensorSubscriberRequestList;		
	}
	
	
	/**
	 * Gets the device subscription list.
	 *
	 * @param deviceId the device id
	 * @return the device subscription list
	 */
	public List<String> getDeviceSubscriptionList(String deviceId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(inE('UserSubscribesDevice')[isAccepted=true].outV()) from Device where idValue='"+deviceId+"'"));
		List<String>	deviceSubscriptionList = new ArrayList<String>();
		
		for (ODocument result:resultList) {			
			String id = result.field("idValue");	
			deviceSubscriptionList.add(id);
		}		
		return deviceSubscriptionList;		
	}
	
	
	/**
	 * Gets the sensor subscription list.
	 *
	 * @param sensorId the sensor id
	 * @return the sensor subscription list
	 */
	public List<String> getSensorSubscriptionList(String sensorId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(inE('UserSubscribesSensor')[isAccepted=true].outV()) from Sensor where idValue='"+sensorId+"'"));
		List<String>	sensorSubscriptionList = new ArrayList<String>();
		
		for (ODocument result:resultList) {			
			String id = result.field("idValue");			
			sensorSubscriptionList.add(id);
		}		
		return sensorSubscriptionList;		
	}
	
	
	/**
	 * Gets the user subscribed device list.
	 *
	 * @param userId the user id
	 * @return the user subscribed device list
	 */
	public List<String> getUserSubscribedDeviceList(String userId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(outE('UserSubscribesDevice')[isAccepted=true].inV()[isDeleted=false]) from User where idValue='"+userId+"'"));
		List<String>	deviceSubscriptionList = new ArrayList<String>();
		
		for (ODocument result:resultList) {			
			String id = result.field("idValue");			
			deviceSubscriptionList.add(id);
		}		
		return deviceSubscriptionList;		
	}
	
	/**
	 * Gets the user subscribed sensor list.
	 *
	 * @param userId the user id
	 * @return the user subscribed sensor list
	 */
	public List<String> getUserSubscribedSensorList(String userId){
		
		List<ODocument> resultList = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(outE('UserSubscribesSensor')[isAccepted=true].inV()[isDeleted=false]) from User where idValue='"+userId+"'"));
		List<String>	sensorSubscriptionList = new ArrayList<String>();
		
		for (ODocument result:resultList) {			
			String id = result.field("idValue");			
			sensorSubscriptionList.add(id);
		}		
		return sensorSubscriptionList;		
	}

	
	/**
	 * Gets the user followers id list.
	 *
	 * @param userId the user id
	 * @return the user followers id list
	 */
	public List<String> getUserFollowersIdList(String userId){
		
		List<String>	followerIdList = new ArrayList<String>();
		List<ODocument> resultList1 = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(in('UserFollowsUser')) from User where idValue='"+userId+"'"));
		List<ODocument> resultList2 = graph.getRawGraph().query(new OSQLSynchQuery<Object>("select expand(out('UserFollowsUser')) from User where idValue='"+userId+"'"));
		
		for (ODocument result:resultList1) {			
			String id = result.field("idValue");			
			followerIdList.add(id);
		}		
		
		for (ODocument result:resultList2) {			
			String id = result.field("idValue");			
			followerIdList.add(id);
		}			
		return followerIdList;
	}

}
