package edu.kalinin.acc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import edu.kalinin.acc.service.ResponseCodeServiceCache.ResponseCodeWithPredicate;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResponseCodeService {

    private final ResponseCodeServiceCache responseCodeServiceCache;

    public List<BigDecimal> getCodes(String str) {
        return responseCodeServiceCache.getPredicates().stream()
                .filter(predicate -> predicate.getPredicate().test(str))
                .map(ResponseCodeWithPredicate::getCode)
                .collect(Collectors.toList());
    }
}
