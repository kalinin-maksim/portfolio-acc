package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.kalinin.acc.entity.LogEntry;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, String> {

}