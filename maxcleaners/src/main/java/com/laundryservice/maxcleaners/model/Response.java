package com.laundryservice.maxcleaners.model;

/**
 * Author Tejesh
 */
public class Response {
    private boolean serviceStatus;
    private String message;

    public boolean getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(boolean serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Response [serviceStatus=" + serviceStatus + ", message=" + message + "]";
    }

}
