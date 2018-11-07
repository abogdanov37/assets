package com.assets.transfer;

import java.util.Map;

public class Asset {
    private long id;
    private String name;
    private Long parentId;
    private Map<String, String> attributes;

    public Asset() {

    }

    public Asset(long id, String name, Long parentId, Map<String, String> attributes) {
        this.setId(id);
        this.setName(name);
        this.setParentId(parentId);
        this.setAttributes(attributes);
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

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
