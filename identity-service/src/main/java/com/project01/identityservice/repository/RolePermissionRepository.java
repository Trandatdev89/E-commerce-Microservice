package com.project01.identityservice.repository;

import com.project01.identityservice.entity.RolePermissionEntity;
import com.project01.identityservice.entity.id.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, RolePermissionId> {
    List<RolePermissionEntity> findAllByRoleId(Long roleId);
}
