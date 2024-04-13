package edu.kalinin.acc.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import edu.kalinin.acc.api.AccountApi;
import edu.kalinin.acc.dto.Account;
import edu.kalinin.acc.dto.AccountPostRq;
import edu.kalinin.acc.mapper.AccountMapper;
import edu.kalinin.acc.service.AccountService;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static edu.kalinin.acc.entity.Account_.*;

@RestController
@RequiredArgsConstructor
public class AccountApiImpl implements AccountApi {

    private final AccountService accountService;

    private final AccountMapper accountMapper;

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler
    @ResponseBody
    String handle(EntityNotFoundException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    String handle(MethodArgumentNotValidException exception) {
        return exception.getMessage();
    }

    @Override
    public ResponseEntity<Account> addAccount(Account account) {
        return null;
    }

    @Override
    public ResponseEntity<Account> addAccountWithParameters(String classId, String glId, AccountPostRq accountPostRq) {

        Map<String, String> keys = new HashMap<>();
        keys.put(BRANCH, accountPostRq.getBranch());
        keys.put(CURRENCY_CODE, accountPostRq.getCurrencyCode());
        keys.put(FLEXCUBE_CUSTOMER_ID, accountPostRq.getFlexcubeCustomerId());
        keys.put(DEAL_ID, accountPostRq.getDealId());

        var account = accountService.findOrCreate(classId, keys, accountMapper.updaterWith(accountPostRq));
        var dto = accountMapper.toDto(account);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<Void> deleteAccount(String accountId) {
        accountService.delete(accountId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<Account>> getAccountByCustomerId(String customerId) {
        var accounts = accountService.getByCustomerId(customerId);
        var dtos = accountMapper.toDtos(accounts);
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<Account> getAccountById(String accountId) {
        var account = accountService.getById(accountId);
        var dto = accountMapper.toDto(account);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<Account> updateAccount(Account account) {
        return null;
    }

    @Override
    public ResponseEntity<Void> updateAccountWithState(String accountId, String state) {
        return null;
    }
}
