package com.iavtar.gfj_be.repository;

import com.iavtar.gfj_be.entity.Dashboard;
import com.iavtar.gfj_be.entity.enums.DashboardTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Integer> {
    Optional<Dashboard> findByName(DashboardTypes dashboardType);
}
