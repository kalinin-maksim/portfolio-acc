package edu.kalinin.acc.repository;

import org.hamcrest.Matchers;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import edu.kalinin.acc.entity.Account;
import edu.kalinin.acc.entity.AccountClass;
import edu.kalinin.acc.exception.FlexCubeAlgorithmException;
import edu.kalinin.acc.helper.SpringSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.instancio.Select.field;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EntityScan(basePackageClasses = Account.class)
@EnableJpaRepositories(basePackageClasses = AccountRepository.class)
@ContextConfiguration(classes = {
        SpringSupport.class,
        AccountRepository.class
})
class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    @Test
    void saveTest() {
        var accountModel = Instancio.of(Account.class)
                .generate(field("classCode"), gen -> gen.oneOf("4000","4020"))
                .generate(field("flexcubeCustomerId"), gen -> gen.oneOf("100010001","200020002"))
                .generate(field("branch"), gen -> gen.oneOf("001","002"))
                .generate(field("currencyCode"), gen -> gen.oneOf("ABC","XYZ"))
                .toModel();

        var idList = IntStream.range(0, 2000)
                .parallel()
                .mapToObj(operand -> {
                    var account = Instancio.of(accountModel).create();
                    try {
                        var accountSaved = accountRepository.save(account);
                        return accountSaved.getNumber();
                    }catch (FlexCubeAlgorithmException exception){
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        idList.forEach(id -> assertThat(id, notNullValue()));

        var idDistinctList = idList.stream().distinct().collect(Collectors.toList());

        assertThat(idList.size(), Matchers.equalTo(idDistinctList.size()));
    }

    @Test
    void getAccountCriteriaQuery() {

        var accountClass = Instancio.create(AccountClass.class);
        Map<String, String> keys = new HashMap<>();
        var accounts = accountRepository.getAccounts(accountClass, keys);

        assertThat(accounts.isPresent(), Matchers.equalTo(Boolean.FALSE));
    }
}