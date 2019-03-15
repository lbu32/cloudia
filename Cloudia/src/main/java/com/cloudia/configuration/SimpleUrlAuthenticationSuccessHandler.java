package com.cloudia.configuration;

import com.cloudia.model.Ticket;
import com.cloudia.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    protected boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        if (authentication == null) {
            return false;
        }

        Ticket ticket = (Ticket) targetDomainObject;
        if (ticket == null) {
            return true;
        }
        User user = (User) authentication.getPrincipal();

        if (user.organization() == ticket.getOrganizationId()) {
            return true;
        }

        if ("privateMessage".equals(permission)) {
            return false;
        } else if ("message".equals(permission) && "ROLE_ADMIN".equals(user.getRole())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

    }
}

