package com.umbrella.corporation.VirusCollector.domain;


public class Virus {

    private Long id;
    private String name;
    private Boolean isIsolated;

    public Virus(Long id, String name, Boolean isIsolated) {
        this.id = id;
        this.name = name;
        this.isIsolated = isIsolated;
    }

    public Virus() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsolated(Boolean isolated) {
        isIsolated = isolated;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getIsolated() {
        return isIsolated;
    }
}
