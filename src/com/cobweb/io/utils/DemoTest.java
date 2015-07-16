package com.cobweb.io.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cobweb.io.meta.Device;
import com.cobweb.io.meta.DeviceType;
import com.cobweb.io.meta.Payload;
import com.cobweb.io.meta.Sensor;
import com.cobweb.io.meta.SensorType;
import com.cobweb.io.meta.User;
import com.cobweb.io.service.AbstractService;
import com.cobweb.io.service.ReadService;

public class DemoTest implements AbstractService{

	 

	public static void main(String args[]) throws Exception{
		
//		HashGenerator hashGenerator = new HashGenerator();
//		
//		String salt1 = hashGenerator.generateSalt();
//		String salt2 = hashGenerator.generateSalt();
//		String salt3 = hashGenerator.generateSalt();
//		
//		User user1 = new User("Yasith", "Lokuge", "yasith1@gmail.com", hashGenerator.saltHashPassword("qwerty",salt1), salt1);
//		User user2 = new User("Tharindu", "Kumara", "hatkumara@yahoo.com", hashGenerator.saltHashPassword("1qaz",salt2), salt2);
//		User user3 = new User("Sandaruwan", "Gunasinghe", "sandaruwan.gunasinghe@gmail.com", hashGenerator.saltHashPassword("2wsx",salt3), salt3);
//		
//		Device device1 = new Device("iphone", "iphone", DeviceType.IPHONE);
//		Device device2 = new Device("windowsphone", "windowsphone", DeviceType.WINDOWSPHONE);
//		Device device3 = new Device("windowsphone", "windowsphone", DeviceType.WINDOWSPHONE);
//		
//		Sensor sensor1 = new Sensor("humidity", "humidity sensor", SensorType.HUMIDITY);
//		Sensor sensor2 = new Sensor("pressure", "pressure sensor", SensorType.PRESSURE);
//		Sensor sensor3 = new Sensor("pressure", "pressure sensor", SensorType.PRESSURE);
//		
//		Payload payload1 = new Payload("32C");
//		Payload payload2 = new Payload("weeC");
//		Payload payload3 = new Payload("23e");
//		Payload payload4 = new Payload("34m");
//		Payload payload5 = new Payload("45cm");
//		Payload payload6 = new Payload("222");
//		Payload payload7 = new Payload("ffff");
//		
//		CobwebWeaver cobwebWeaver = new CobwebWeaver();
//		
//		cobwebWeaver.addUser(user1);
//		cobwebWeaver.addUser(user2);
//		cobwebWeaver.addUser(user3);
//
//		cobwebWeaver.addDevice(user1.getUid(), device1);
//		cobwebWeaver.addDevice(user2.getUid(), device2);
//		cobwebWeaver.addDevice(user3.getUid(), device3);
//		
//		cobwebWeaver.addSensor(device1.getId(), sensor1);
//		cobwebWeaver.addSensor(device2.getId(), sensor2);
//		cobwebWeaver.addSensor(device3.getId(), sensor3);
//		
//		cobwebWeaver.addSensorPayload(sensor1.getId(), payload1);
//		cobwebWeaver.addSensorPayload(sensor1.getId(), payload2);
//		cobwebWeaver.addSensorPayload(sensor2.getId(), payload3);
//		cobwebWeaver.addSensorPayload(sensor2.getId(), payload4);
//		cobwebWeaver.addSensorPayload(sensor3.getId(), payload5);
//		cobwebWeaver.addSensorPayload(sensor3.getId(), payload6);
//		
//		cobwebWeaver.addDevicePayload(device2.getId(), payload7);
//		
//		cobwebWeaver.addFollowUser(user1.getUid(),user2.getUid());
//		cobwebWeaver.addFollowUser(user2.getUid(),user3.getUid());
//		cobwebWeaver.addFollowUser(user3.getUid(),user1.getUid());
//		
//		cobwebWeaver.addSensorSubscription(user3.getUid(), sensor1.getId());
//		cobwebWeaver.addSensorSubscription(user2.getUid(), sensor3.getId());
//		cobwebWeaver.addDeviceSubscription(user1.getUid(), device2.getId());
		
	//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>//
		
		//System.out.println(regexCheck("samsung galaxy y","^[a-zA-Z0-9 ]*{3,15}$"));
		
		ReadService readService = new ReadService();
		
		//System.out.println(readService.getSensorIds(readService.getUserId("yasith1@gmail.com")));
		//readService.ReadUserNames();
		//System.out.println(readService.ReadDeviceIds("yasith1@gmail.com").get(1));
		//System.out.println(readService.CheckUserNameExists("yasith1@gmail.com"));
		//System.out.println(validate("aaa  "));
		//MosquittoAuth mosquittoAuth = new MosquittoAuth();
		
		//System.out.println(readService.getUserId("yasith1@gmail.com"));
		//System.out.println(mosquittoAuth.checkPassword("yasith1@gmail.com", "qwerty"));
		
		//System.out.println(readService.getSalt("yasith1@gmail.com"));
		
	//	System.out.println(readService.getParentDeviceId("134f3e28-274f-448f-a64b-46733bf6d7c8"));
		
//		List<Payload> payloadList = readService.getDevicePayloadList("ac94bb5a-04fb-4d9d-b230-91307b2bb8ee");
//		
//		for (Payload payload : payloadList) {
//			System.out.println(payload.getMessage());
//			System.out.println(payload.getId());
//			System.out.println(payload.getTimeStamp());
//		}
		
		
	}
	
	


}
