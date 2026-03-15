package com.anyview.repository;

import com.anyview.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByCode(String code);
    List<School> findByEnabledTrue();
    boolean existsByCode(String code);
    boolean existsByName(String name);
}
