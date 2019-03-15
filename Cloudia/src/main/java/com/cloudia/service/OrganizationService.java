package com.cloudia.service;

import com.cloudia.model.Organization;
import com.cloudia.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    // @Autowired
    //  private Organization organizationName;
    public List<Organization> findAll(){
        return organizationRepository.findAll();
    }


    public Organization findById(int organizationId){

        return organizationRepository.findById(organizationId);
    }

    public Organization findByOrganizationName(String organizationName){

        return organizationRepository.findByOrganizationName(organizationName);
    }

    public Organization saveOrganization(Organization organization) {
        organization.setOrganizationId(organization.getOrganizationId());
        organization.setOrganizationName(organization.getOrganizationName());
        organization.setOrganizationToken(organization.getOrganizationToken());
        return organizationRepository.save(organization);
    }
}
