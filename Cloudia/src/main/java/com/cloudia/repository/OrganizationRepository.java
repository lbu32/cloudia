package com.cloudia.repository;

import com.cloudia.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("organizationRepository")
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
    Organization findById(int organization_id);
    Organization findByOrganizationName(String organizationName);
}
