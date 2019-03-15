package com.cloudia.model;

/**
 * Created by Lena on 22.10.2018.
 */

import javax.persistence.*;

@Entity
@Table(name = "organization")
public class Organization {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)

    private Integer organization_id;

    private String organizationName;

    private String organizationToken;


    @ManyToOne
    @JoinColumn(name = "cloud")
    private Cloud cloud;

    public Cloud getCloud() {
        return cloud;
    }

    public void setCloud(Cloud cloud_id) {
        this.cloud = cloud;
    }

    public String getOrganizationToken() { return organizationToken; }

    public void setOrganizationToken(String organizationToken) { this.organizationToken = organizationToken; }

    public Integer getOrganizationId() {
        return organization_id;
    }

    public void setOrganizationId(Integer organization_id) {
        this.organization_id= organization_id;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

}
