package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.kalinin.acc.entity.Balance;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, String> {


}