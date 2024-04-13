package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import edu.kalinin.acc.entity.Entry;

import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<Entry, String> {
    @Query("select e from Entry e where e.id not in (select id from ProcessedEntry)")
    List<Entry> findEntriesToProcess();
}