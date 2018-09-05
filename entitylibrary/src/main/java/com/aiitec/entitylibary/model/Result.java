package com.aiitec.entitylibary.model;

import com.aiitec.openapi.model.Entity;

public class Result extends Entity {

    private long id;
    private String name;
    private String timestamp;
    private String device;
    private String version ;
    private String description;
    private String trackViewUrl;
    private int forcedUpdate = -1;
    private int type;

    
    public String getDevice() {
        return device;
    }
    public void setDevice(String device) {
        this.device = device;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getTrackViewUrl() {
        return trackViewUrl;
    }
    public void setTrackViewUrl(String trackViewUrl) {
        this.trackViewUrl = trackViewUrl;
    }
    public int getForcedUpdate() {
        return forcedUpdate;
    }
    public void setForcedUpdate(int forcedUpdate) {
        this.forcedUpdate = forcedUpdate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
