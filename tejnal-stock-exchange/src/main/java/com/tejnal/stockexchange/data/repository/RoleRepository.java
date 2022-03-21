package com.tejnal.stockexchange.data.repository;

import com.tejnal.stockexchange.data.entity.Role;
import com.tejnal.stockexchange.data.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
