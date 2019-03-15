package com.cloudia.configuration;

import com.cloudia.model.Ticket;
import com.cloudia.model.User;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class SimplePermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null) {
            return false;
        }
        Ticket tickets = (Ticket) targetDomainObject;
        if (tickets == null) {
            return true;
        }
        User user = (User) authentication.getPrincipal();

        if (user.organization() == tickets.getOrganizationId()) {
            return true;
        }

        if ("privatetickets".equals(permission)) {
            return false;
        } else if ("tickets".equals(permission) && "ROLE_ADMIN".equals(user.getRole())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}

