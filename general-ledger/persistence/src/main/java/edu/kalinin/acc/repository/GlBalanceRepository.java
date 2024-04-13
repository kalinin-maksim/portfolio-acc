package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.kalinin.acc.entity.GLBalance;

@Repository
public interface GlBalanceRepository extends JpaRepository<GLBalance, String> {


}