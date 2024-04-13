package edu.kalinin.acc.service;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import edu.kalinin.acc.repository.ResponseCodeRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ResponseCodeServiceCache {

    private final ResponseCodeRepository responseCodeRepository;

    @Cacheable("dictionaries")
    public List<ResponseCodeWithPredicate> getPredicates(){
        return responseCodeRepository.findAll().stream()
                .map(responseCode -> new ResponseCodeWithPredicate(Pattern.compile(responseCode.getRegExp()).asPredicate(), responseCode.getCode()))
                .collect(Collectors.toList());
    }

    @Value
    public static class ResponseCodeWithPredicate{
        Predicate<String> predicate;
        BigDecimal code;
    }

}
