package com.citizenportal.model.report;

public class Authority {

    private String id;
    private String name;

    // Construtor que aceita id e name
    public Authority(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}