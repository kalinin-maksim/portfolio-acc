package edu.kalinin.acc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.kalinin.acc.entity.Entry;
import edu.kalinin.acc.helper.Observer;
import edu.kalinin.acc.repository.EntryRepository;

@Service
@RequiredArgsConstructor
@Log4j2
public class EntryService {

    private final EntryRepository entryRepository;

    @Transactional
    public void trySave(Entry entry, Observer<String> observer) {
        observer.run(() -> entryRepository.save(entry).getId());
    }
}
