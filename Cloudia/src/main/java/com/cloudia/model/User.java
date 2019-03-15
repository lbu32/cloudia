package com.cloudia.model;

/**
 * Created by Lena on 22.10.2018.
 */


import javax.persistence.*;


@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private int user_id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    //@ManyToOne(targetEntity= Organization.class)
    //@JoinColumn(name = "organization_id")
    @OneToOne
    private Organization organization;

    @OneToOne
    private Role role;


    public User(){}
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Organization organization() {
        return organization;
    }

    public void setOrganizationId(Organization organization) {
        this.organization = organization;
    }

    public Role getRole() {
        return role;
    }

    public void setRoleId(Role role) {
        this.role = role;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) { this.user_id = user_id; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


