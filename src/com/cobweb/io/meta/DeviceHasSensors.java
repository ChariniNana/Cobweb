package com.cobweb.io.meta;

import com.tinkerpop.blueprints.Vertex;


/**
 * The Class DeviceHasSensors.
 * @author YasithLokuge
 */
public class DeviceHasSensors {

	/** The device. */
	private Vertex device;
	
	/** The sensor. */
	private Vertex sensor;
	
	
	/**
	 * Gets the sensor.
	 *
	 * @return the sensor
	 */
	public Vertex getSensor() {
		return sensor;
	}
	
	/**
	 * Sets the sensor.
	 *
	 * @param sensor the new sensor
	 */
	public void setSensor(Vertex sensor) {
		this.sensor = sensor;
	}
	
	/**
	 * Gets the device.
	 *
	 * @return the device
	 */
	public Vertex getDevice() {
		return device;
	}
	
	/**
	 * Sets the device.
	 *
	 * @param device the new device
	 */
	public void setDevice(Vertex device) {
		this.device = device;
	}
	
	
}
