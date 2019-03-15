package com.cloudia.model;

/**
 * Created by Lena on 22.10.2018.
 */

import javax.persistence.*;

@Entity
@Table(name = "cloud")
public class Cloud {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer cloudId;

    private String cloudName;

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }
    public String getCloudName() {
        return cloudName;
    }
    public Integer getCloudId() {
        return cloudId;
    }

    public void setCloudId(Integer cloudId) {
        this.cloudId = cloudId;
    }

}