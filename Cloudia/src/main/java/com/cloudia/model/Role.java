package com.cloudia.model;

/**
 * Created by Lena on 22.10.2018.
 */


import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;



@Entity
@Table(name = "role")
public class Role{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer roleId;
    private String role;


    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }



}


