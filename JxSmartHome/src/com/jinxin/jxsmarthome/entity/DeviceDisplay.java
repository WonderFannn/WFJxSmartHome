package com.jinxin.jxsmarthome.entity;

import java.io.Serializable;

import org.teleal.cling.model.meta.Device;

/**
 * @author DLNA设备信息类
 * @company 金鑫智慧
 */
@SuppressWarnings("rawtypes")
public class DeviceDisplay implements Serializable{

	private static final long serialVersionUID = 1L;
	
	
	Device device;

    public DeviceDisplay( Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDisplay that = (DeviceDisplay) o;
        return device.equals(that.device);
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

    @Override
    public String toString() {
        String name =
                device.getDetails() != null && device.getDetails().getFriendlyName() != null
                        ? device.getDetails().getFriendlyName()
                        : device.getDisplayString();
        return device.isFullyHydrated() ? name : name + " *";
    }

}
