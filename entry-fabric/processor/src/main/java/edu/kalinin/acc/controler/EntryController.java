package edu.kalinin.acc.controler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.kalinin.acc.repository.EntryRepository;

@RestController
@RequestMapping(path = "/entry")
@RequiredArgsConstructor
public class EntryController {

    private final EntryRepository entryRepository;

    @GetMapping("/json")
    public String getAllEntries() throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entryRepository.findAll());
    }
}