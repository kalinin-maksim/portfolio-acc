package edu.kalinin.acc.api.impl;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import edu.kalinin.acc.api.BalanceApi;
import edu.kalinin.acc.api.GlBalanceApi;
import edu.kalinin.acc.entity.Balance;
import edu.kalinin.acc.entity.GLBalance;
import edu.kalinin.acc.mapper.ApiMapperImpl;
import edu.kalinin.acc.service.BalanceService;

import java.util.List;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        BalanceService.class,
        BalanceApiImpl.class,
        ApiMapperImpl.class
})
class AccountApiImplTest {

    @Autowired
    BalanceApi balanceApi;

    @Autowired
    GlBalanceApi glBalanceApi;

    @MockBean
    BalanceService balanceService;

    @Test
    void getBalance() {
        //given
        var balance1 = new Balance();
        Mockito.when(balanceService.getBalance()).thenReturn(List.of(balance1));
        //when
        var balance = balanceApi.createPostingAndBalance().getBody();
        //then
        BDDAssertions.then(balance).hasSize(1);
    }

    @Test
    void getGLBalance() {
        //given
        var glBalance1 = new GLBalance();
        Mockito.when(balanceService.getGlBalance()).thenReturn(List.of(glBalance1));
        //when
        var gLBalance = glBalanceApi.getGlBalance().getBody();
        //then
        BDDAssertions.then(gLBalance).hasSize(1);
    }
}