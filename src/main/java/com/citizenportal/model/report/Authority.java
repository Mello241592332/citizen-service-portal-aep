package com.citizenportal.model.report;

import com.citizenportal.model.request.Sector;

public class Authority {

    private String id;
    private String name;
    private Sector sector;

    public Authority(String id, String name, Sector sector) {
        this.id = id;
        this.name = name;
        this.sector = sector;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Sector getSector() { return sector; }
    public void setSector(Sector sector) { this.sector = sector; }
}