package edu.kalinin.acc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import edu.kalinin.acc.entity.Account;
import edu.kalinin.acc.repository.AccountClassRepository;
import edu.kalinin.acc.repository.AccountRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountClassRepository accountClassRepository;

    public Account findOrCreate(String classId, Map<String, String> keys, UnaryOperator<Account> update) throws EntityNotFoundException {

        return findBy(classId, keys)
                .or(() -> constructAccount(classId))
                .map(update)
                .map(accountRepository::trySave)
                .orElseThrow();
    }

    private Optional<Account> constructAccount(String classId) {
        var account = new Account();
        account.setClassCode(classId);
        account.setState(Account.STATE.OPEN);
        account.setOpeningDate(new Date());
        return Optional.of(account);
    }

    private Optional<Account> findBy(String classId, Map<String, String> keys) {
        var accountClass = accountClassRepository.findById(classId).orElseThrow(EntityNotFoundException::new);
        return accountRepository.getAccounts(accountClass, keys);
    }

    public Account getById(String accountId) {
        return accountRepository.findById(accountId).orElseThrow(EntityNotFoundException::new);
    }

    public List<Account> getByCustomerId(String customerId) {
        return accountRepository.findByFlexcubeCustomerId(customerId);
    }

    public void delete(String accountId) {
        accountRepository.deleteById(accountId);
    }
}
