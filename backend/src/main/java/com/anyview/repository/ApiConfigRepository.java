package com.anyview.repository;

import com.anyview.entity.ApiConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiConfigRepository extends JpaRepository<ApiConfig, Long> {
    
    List<ApiConfig> findByEnabledTrue();
    
    Optional<ApiConfig> findByName(String name);
    
    List<ApiConfig> findByProvider(String provider);
    
    Optional<ApiConfig> findFirstByEnabledTrueOrderByUpdatedAtDesc();
}
