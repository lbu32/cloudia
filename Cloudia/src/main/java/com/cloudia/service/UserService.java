package com.cloudia.service;

import com.cloudia.model.User;
import com.cloudia.repository.OrganizationRepository;
import com.cloudia.repository.RoleRepository;
import com.cloudia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private OrganizationRepository organizationRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder, OrganizationRepository organizationRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.organizationRepository = organizationRepository;
    }

    public User findUserByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public User setUserRole(String email, int role) {

        User u = userRepository.findByEmail(email);
        u.setRoleId(roleRepository.findById(role));
        return userRepository.save(u);
    }

    public User setOrganization(String email, int organizationId) {

        User u = userRepository.findByEmail(email);
        u.setOrganizationId(organizationRepository.findById(organizationId));
        return userRepository.save(u);
    }

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoleId(user.getRole());
        return userRepository.save(user);
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }
}