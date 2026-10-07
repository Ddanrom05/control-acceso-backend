package com.example.control_acceso.repository;

import com.example.control_acceso.model.LogAcceso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogAccesoRepository extends JpaRepository<LogAcceso, Long> {
}