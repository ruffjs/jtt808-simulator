package cn.org.hentai.simulator.entity;

import cn.org.hentai.simulator.task.ConnectionState;


/**
 * Created by houcheng when 2018/11/29.
 */
public class TaskInfo {
    private long id;
    private long routeId;
    private String routeName;
    private int routeMileages;

    private String vehicleNumber;
    private String deviceSn;
    private String simNumber;

    private String state;
    private long startTime;
    private double longitude;
    private double latitude;
    private long reportTime;
    private int stateFlags;
    private int warningFlags;

    private ConnectionState connectionState = ConnectionState.NotConnected;

    private ConnectionState.DisconnectReason disconnectReason;

    public TaskInfo withSimNumber(String sim) {
        setSimNumber(sim);
        return this;
    }

    public TaskInfo withLatitude(double lat) {
        setLatitude(lat);
        return this;
    }

    public TaskInfo withLongitude(double lng) {
        setLongitude(lng);
        return this;
    }

    public TaskInfo withStartTime(long stime) {
        setStartTime(stime);
        return this;
    }

    public TaskInfo withState(String state) {
        setState(state);
        return this;
    }

    public TaskInfo withDeviceSn(String sn) {
        setDeviceSn(sn);
        return this;
    }

    public TaskInfo withVehicleNumber(String vehicleNumber) {
        setVehicleNumber(vehicleNumber);
        return this;
    }

    public TaskInfo withRouteMileages(int routeMileages) {
        setRouteMileages(routeMileages);
        return this;
    }

    public TaskInfo withRouteName(String name) {
        setRouteName(name);
        return this;
    }

    public TaskInfo withRouteId(long routeId) {
        setRouteId(routeId);
        return this;
    }

    public TaskInfo withId(long id) {
        setId(id);
        return this;
    }

    public TaskInfo withReportTime(long time) {
        setReportTime(time);
        return this;
    }

    public TaskInfo withStateFlags(int flags) {
        setStateFlags(flags);
        return this;
    }

    public TaskInfo withWarningFlags(int flags) {
        setWarningFlags(flags);
        return this;
    }

    public int getStateFlags() {
        return stateFlags;
    }

    public void setStateFlags(int stateFlags) {
        this.stateFlags = stateFlags;
    }

    public int getWarningFlags() {
        return warningFlags;
    }

    public void setWarningFlags(int warningFlags) {
        this.warningFlags = warningFlags;
    }

    public long getReportTime() {
        return reportTime;
    }

    public void setReportTime(long reportTime) {
        this.reportTime = reportTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRouteId() {
        return routeId;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getRouteMileages() {
        return routeMileages;
    }

    public void setRouteMileages(int routeMileages) {
        this.routeMileages = routeMileages;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getSimNumber() {
        return simNumber;
    }

    public void setSimNumber(String simNumber) {
        this.simNumber = simNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }


    public ConnectionState.DisconnectReason getDisconnectReason() {
        return disconnectReason;
    }

    public void setDisconnectReason(ConnectionState.DisconnectReason disconnectReason) {
        this.disconnectReason = disconnectReason;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "id=" + id +
                ", routeId=" + routeId +
                ", routeName='" + routeName + '\'' +
                ", routeMileages=" + routeMileages +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", deviceSn='" + deviceSn + '\'' +
                ", simNumber='" + simNumber + '\'' +
                ", state='" + state + '\'' +
                ", startTime=" + startTime +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", reportTime=" + reportTime +
                ", stateFlags=" + stateFlags +
                ", warningFlags=" + warningFlags +
                ", connectionState=" + connectionState +
                '}';
    }
}
