package com.example.backend.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "record")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataRecord {

    @XmlElement
    private String id;

    @XmlElement
    private String name;

    @XmlElement
    private String source;

    public DataRecord() {
    }

    public DataRecord(String id, String name, String source) {
        this.id = id;
        this.name = name;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
