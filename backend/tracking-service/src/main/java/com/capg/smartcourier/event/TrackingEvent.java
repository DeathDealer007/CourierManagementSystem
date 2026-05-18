package com.capg.smartcourier.event;

public class TrackingEvent {

    private String trackingNumber;
    private String status;
    private String location;
    private String message;

    public TrackingEvent() {}

    public TrackingEvent(String trackingNumber, String status, String location, String message) {
        this.trackingNumber = trackingNumber;
        this.status = status;
        this.location = location;
        this.message = message;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}