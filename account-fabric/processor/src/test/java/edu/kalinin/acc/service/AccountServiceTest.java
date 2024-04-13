package edu.kalinin.acc.service;

import lombok.SneakyThrows;
import org.instancio.Instancio;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import edu.kalinin.acc.entity.Account;
import edu.kalinin.acc.entity.AccountClass;
import edu.kalinin.acc.helper.SpringSupport;
import edu.kalinin.acc.repository.AccountClassRepository;
import edu.kalinin.acc.repository.AccountRepository;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;
import static edu.kalinin.acc.entity.Account_.CURRENCY_CODE;
import static edu.kalinin.acc.entity.Account_.FLEXCUBE_CUSTOMER_ID;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EntityScan(basePackageClasses = Account.class)
@EnableJpaRepositories(basePackageClasses = AccountRepository.class)
@ContextConfiguration(classes = {
        SpringSupport.class,
        AccountService.class,
        AccountRepository.class
})
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountClassRepository accountClassRepository;

    @SneakyThrows
    @ParameterizedTest(name = "{index} - find or create account with customer: {0}")
    @CsvSource(value = {
            "4000,000000012,INR,4000000000012INR001Z"
    })
    void findOrCreateTest(String classCode,
                          String flexcubeCustomerId,
                          String currencyCode,
                          String number) {
        //given
        var accountClass = Instancio.create(AccountClass.class);
        accountClass.setId(classCode);
        accountClassRepository.save(accountClass);
        //when
        Map<String, String> keys = new HashMap<>();
        keys.put(FLEXCUBE_CUSTOMER_ID, flexcubeCustomerId);
        keys.put(CURRENCY_CODE, currencyCode);

        accountService.findOrCreate(classCode, keys, account -> {
            account.setClassCode(classCode);
            account.setFlexcubeCustomerId(flexcubeCustomerId);
            account.setCurrencyCode(currencyCode);
            return account;
        });

        //then
        var accounts = accountRepository.findAll();
        then(accounts).hasSize(1);
        then(accounts.get(0)).matches(account -> account.getNumber().equals(number));
    }
}