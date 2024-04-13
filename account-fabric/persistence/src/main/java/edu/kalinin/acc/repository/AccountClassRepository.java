package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.kalinin.acc.entity.AccountClass;

import java.util.Optional;

@Repository
public interface AccountClassRepository extends JpaRepository<AccountClass, String> {


    @Override
    Optional<AccountClass> findById(String s);
}