package com.mobiauto.backend.interview.repository;

import com.mobiauto.backend.interview.model.Oportunidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OportunidadeRepository extends JpaRepository<Oportunidade, Long> {}