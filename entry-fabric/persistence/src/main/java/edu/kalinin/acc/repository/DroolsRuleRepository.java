package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.kalinin.acc.entity.DroolsRule;

@Repository
public interface DroolsRuleRepository extends JpaRepository<DroolsRule, String> {
}