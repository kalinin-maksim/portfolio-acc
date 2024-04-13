package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.kalinin.acc.entity.Posting;

import java.util.List;

public interface PostingRepository extends JpaRepository<Posting, String> {
    List<Posting> findByActiveTrue();
}