package com.cobweb.io.service;

import com.orientechnologies.orient.core.sql.OCommandSQL;

// TODO: Auto-generated Javadoc
/**
 * The Class UpdateService.
 *
 * @author Yasith Lokuge
 */

public class UpdateService implements AbstractService{

	/**
	 * Sets the user device subscription.
	 *
	 * @param userId the user id
	 * @param deviceId the device id
	 * @return true, if successful
	 */
	public boolean setUserDeviceSubscription(String userId, String deviceId){
		int result = graph.command(new OCommandSQL("UPDATE UserSubscribesDevice SET isAccepted=true WHERE outV().idValue = '"+userId+"' AND inV().idValue ='"+deviceId+"'")).execute();
		return result == 1;	
	}
	
	/**
	 * Sets the user sensor subscription.
	 *
	 * @param userId the user id
	 * @param sensorId the sensor id
	 * @return true, if successful
	 */
	public boolean setUserSensorSubscription(String userId, String sensorId){
		int result = graph.command(new OCommandSQL("UPDATE UserSubscribesSensor SET isAccepted=true WHERE outV().idValue = '"+userId+"' AND inV().idValue ='"+sensorId+"'")).execute();
		return result == 1;	
	}
}
