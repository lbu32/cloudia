package com.cloudia.repository;

import com.cloudia.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRole(String role);
    Role findById(int role_id);

    /** Mit Hilfe des JpaRepository stehen CRUD Methoden zu Verfügung und
     * weitere Annotationen @Transactional annotation sind nicht mehr nötig
     * https://spring.io/blog/2011/02/10/getting-started-with-spring-data-jpa/
     *
     */
}
