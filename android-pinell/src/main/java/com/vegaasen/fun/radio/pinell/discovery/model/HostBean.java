package com.vegaasen.fun.radio.pinell.discovery.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HostBean implements Parcelable {

    public static final int TYPE_GATEWAY = 0;
    public static final int TYPE_COMPUTER = 1;

    private int deviceType = TYPE_COMPUTER;
    private int isAlive = 1;
    private int position = 0;
    private int responseTime = 0; // ms
    private String ipAddress = null;
    private String hostname = null;
    private String hardwareAddress = NetInfo.NOMAC;
    private String nicVendor = "Unknown";
    private String os = "Unknown";
    private HashMap<Integer, String> services = null;
    private HashMap<Integer, String> banners = null;
    private Set<Integer> portsOpen = null;
    private List<Integer> portsClosed = null;

    public HostBean() {
    }

    public HostBean(Parcel in) {
        readFromParcel(in);
    }

    @SuppressWarnings("unchecked")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public HostBean createFromParcel(Parcel in) {
            return new HostBean(in);
        }

        public HostBean[] newArray(int size) {
            return new HostBean[size];
        }
    };

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getIsAlive() {
        return isAlive;
    }

    public void setIsAlive(int isAlive) {
        this.isAlive = isAlive;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getHardwareAddress() {
        return hardwareAddress;
    }

    public void setHardwareAddress(String hardwareAddress) {
        this.hardwareAddress = hardwareAddress;
    }

    public String getNicVendor() {
        return nicVendor;
    }

    public void setNicVendor(String nicVendor) {
        this.nicVendor = nicVendor;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public HashMap<Integer, String> getServices() {
        return services;
    }

    public void setServices(HashMap<Integer, String> services) {
        this.services = services;
    }

    public HashMap<Integer, String> getBanners() {
        return banners;
    }

    public void setBanners(HashMap<Integer, String> banners) {
        this.banners = banners;
    }

    public Set<Integer> getPortsOpen() {
        return portsOpen;
    }

    public void setPortsOpen(Set<Integer> portsOpen) {
        this.portsOpen = portsOpen;
    }

    public List<Integer> getPortsClosed() {
        return portsClosed;
    }

    public void setPortsClosed(List<Integer> portsClosed) {
        this.portsClosed = portsClosed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(deviceType);
        dest.writeInt(isAlive);
        dest.writeString(ipAddress);
        dest.writeString(hostname);
        dest.writeString(hardwareAddress);
        dest.writeString(nicVendor);
        dest.writeString(os);
        dest.writeInt(responseTime);
        dest.writeInt(position);
        dest.writeMap(services);
        dest.writeMap(banners);
        dest.writeList(Lists.newArrayList(portsOpen));
        dest.writeList(portsClosed);
    }

    @Override
    public String toString() {
        return "HostBean{" +
                "deviceType=" + deviceType +
                ", isAlive=" + isAlive +
                ", position=" + position +
                ", responseTime=" + responseTime +
                ", ipAddress='" + ipAddress + '\'' +
                ", hostname='" + hostname + '\'' +
                ", hardwareAddress='" + hardwareAddress + '\'' +
                ", nicVendor='" + nicVendor + '\'' +
                ", os='" + os + '\'' +
                ", services=" + services +
                ", banners=" + banners +
                ", portsOpen=" + portsOpen +
                ", portsClosed=" + portsClosed +
                '}';
    }

    @SuppressWarnings("unchecked")
    private void readFromParcel(Parcel in) {
        deviceType = in.readInt();
        isAlive = in.readInt();
        ipAddress = in.readString();
        hostname = in.readString();
        hardwareAddress = in.readString();
        nicVendor = in.readString();
        os = in.readString();
        responseTime = in.readInt();
        position = in.readInt();
        services = in.readHashMap(null);
        banners = in.readHashMap(null);
        portsOpen = Sets.newHashSet(in.readArrayList(Integer.class.getClassLoader()));
        portsClosed = in.readArrayList(Integer.class.getClassLoader());
    }
}
