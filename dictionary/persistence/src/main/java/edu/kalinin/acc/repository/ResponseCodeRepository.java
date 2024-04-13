package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.kalinin.acc.entity.ResponseCode;

@Repository
public interface ResponseCodeRepository extends JpaRepository<ResponseCode, String> {

}