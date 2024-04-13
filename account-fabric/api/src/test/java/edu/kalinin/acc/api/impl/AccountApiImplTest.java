package edu.kalinin.acc.api.impl;

import org.hamcrest.Matchers;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import edu.kalinin.acc.api.AccountApi;
import edu.kalinin.acc.dto.AccountPostRq;
import edu.kalinin.acc.entity.Account;
import edu.kalinin.acc.entity.AccountClass;
import edu.kalinin.acc.mapper.AccountMapperImpl;
import edu.kalinin.acc.repository.AccountClassRepository;
import edu.kalinin.acc.repository.AccountRepository;
import edu.kalinin.acc.service.AccountService;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AccountService.class,
        AccountApiImpl.class,
        AccountMapperImpl.class
})
class AccountApiImplTest {

    @Autowired
    AccountApi accountApi;

    @MockBean
    AccountRepository accountRepository;
    @MockBean
    AccountClassRepository accountClassRepository;

    @Test
    void addAccountWithParameters() {

        Mockito.when(accountClassRepository.findById(any())).thenReturn(Optional.of(Instancio.create(AccountClass.class)));
        Mockito.when(accountRepository.trySave(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var classId = "";
        var glId = "";
        var accountPostRq = Instancio.create(AccountPostRq.class);

        var response = accountApi.addAccountWithParameters(classId, glId, accountPostRq);

        assertThat(response, Matchers.notNullValue());
    }

    @Test
    void deleteAccount() {
        var voidResponseEntity = accountApi.deleteAccount("1");
        assertThat(voidResponseEntity, Matchers.notNullValue());
    }

    @Test
    void getAccountByCustomerId() {
        var accountByCustomerId = accountApi.getAccountByCustomerId("1");
        assertThat(accountByCustomerId, Matchers.notNullValue());
    }

    @Test
    void getAccountById() {
        Mockito.when(accountRepository.findById(any())).thenReturn(Optional.of(Instancio.create(Account.class)));

        var accountById = accountApi.getAccountById("1");
        assertThat(accountById, Matchers.notNullValue());
    }
}