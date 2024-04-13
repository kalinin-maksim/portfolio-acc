package edu.kalinin.acc.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import edu.kalinin.acc.api.BalanceApi;
import edu.kalinin.acc.api.GlBalanceApi;
import edu.kalinin.acc.dto.AccountBalance;
import edu.kalinin.acc.dto.AccountGLBalance;
import edu.kalinin.acc.mapper.ApiMapper;
import edu.kalinin.acc.service.BalanceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BalanceApiImpl implements BalanceApi, GlBalanceApi {

    private final BalanceService balanceService;

    private final ApiMapper apiMapper;

    @Override
    public ResponseEntity<List<AccountBalance>> createPostingAndBalance() {
        balanceService.createPostings();
        var balance = balanceService.getBalance();
        var dto = apiMapper.toAccountBalanceDtos(balance);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<List<AccountGLBalance>> getGlBalance() {
        balanceService.createPostings();
        var glBalance = balanceService.getGlBalance();
        var dto = apiMapper.toAccountGLBalanceDtos(glBalance);
        return ResponseEntity.ok(dto);
    }
}
