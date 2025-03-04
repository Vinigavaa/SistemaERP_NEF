package com.funilaria.api.repositories;

import com.funilaria.api.models.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
    List<Work> findByClientNameContainingIgnoreCase(String clientName);
    boolean existsByClientNameAndCarPlateAndServiceDate(
            String clientName,
            String carPlate,
            LocalDate serviceDate
    );
}
