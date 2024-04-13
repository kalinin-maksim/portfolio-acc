package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.kalinin.acc.entity.Account;
import edu.kalinin.acc.entity.AccountClass;
import edu.kalinin.acc.exception.FlexCubeAlgorithmException;
import edu.kalinin.acc.helper.JPAHelper;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static edu.kalinin.acc.entity.Account_.*;
import static edu.kalinin.acc.helper.SpringSupport.getContext;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByClassCodeAndFlexcubeCustomerIdAndCurrencyCode(String classCode, String flexcubeCustomerId, String currencyCode);

    List<Account> findByFlexcubeCustomerId(String flexcubeCustomerId);

    static AccountRepository getThis() {
        return getContext().getBean(AccountRepository.class);
    }

    default Optional<Account> getAccounts(AccountClass accountClass, Map<String, String> keys) {
        return JPAHelper.getOne(getAccountCriteriaQuery(accountClass, keys));
    }

    default String getCurrencyCode(String number) {
        return findById(number).orElseThrow(EntityNotFoundException::new).getCurrencyCode();
    }

    default String getClass(String number, AccountClassRepository accountClassRepository){
        var accountClass = findById(number).orElseThrow(() -> {
            throw new EntityNotFoundException(format("account not found by number '%s'", number));
        }).getClassCode();
        return accountClassRepository.findById(accountClass).orElseThrow(() -> {
            throw new EntityNotFoundException(format("account class not found by id '%s'", accountClass));
        }).getId();
    }

    default Function<CriteriaBuilder, CriteriaQuery<Account>> getAccountCriteriaQuery(AccountClass accountClass, Map<String, String> keys) {
        return builder -> {
            var criteriaQuery = builder.createQuery(Account.class);
            var accountRoot = criteriaQuery.from(Account.class);
            var searchFields = accountClass.getAccountSearchFields().stream()
                    .map(AccountClass.AccountSearchField::getAccountField)
                    .map(Enum::name)
                    .collect(Collectors.toList());
            searchFields.add(BRANCH);
            searchFields.add(CURRENCY_CODE);
            searchFields.add(FLEXCUBE_CUSTOMER_ID);
            var predicates = searchFields.stream()
                    .map(filedName -> builder.equal(accountRoot.get(filedName), keys.get(filedName)))
                    .collect(Collectors.toList());
            predicates.add(builder.equal(accountRoot.get(CLASS_CODE), accountClass.getId()));
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
            return criteriaQuery;
        };
    }

    default Account trySave(Account account) {
        var tryCounter = 0;
        var tryLimit = 100;
        while (tryCounter < tryLimit) {
            try {
                return save(account);
            } catch (FlexCubeAlgorithmException ignored) {
                tryCounter++;
            }
        }
        throw new FlexCubeAlgorithmException(account.getNumber());
    }
}