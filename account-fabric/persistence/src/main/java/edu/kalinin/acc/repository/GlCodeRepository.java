package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.kalinin.acc.entity.GlCode;

public interface GlCodeRepository extends JpaRepository<GlCode, String> {
}