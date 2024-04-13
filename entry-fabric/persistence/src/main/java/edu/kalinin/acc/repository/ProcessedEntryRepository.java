package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.kalinin.acc.entity.ProcessedEntry;

@Repository
public interface ProcessedEntryRepository extends JpaRepository<ProcessedEntry, String> {

}