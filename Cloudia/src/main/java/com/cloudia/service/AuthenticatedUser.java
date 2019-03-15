package com.cloudia.service;

import com.cloudia.model.Organization;
import com.cloudia.model.Role;
import com.cloudia.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Collection;

@Component("AuthenticatedUser")
@Scope("view")
public class AuthenticatedUser extends User implements UserDetails {
    private Organization organization;
    private Role role;
    AuthenticatedUser(User user) {
        super(user.getEmail(), user.getPassword());
        this.role=user.getRole();
        this.organization=user.organization();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(role.toString());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public Organization organization(){return organization; }
}
