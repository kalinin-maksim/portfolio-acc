package edu.kalinin.acc.entity;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import edu.kalinin.acc.generator.AccountKeyGenerator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class AccountKeyGeneratorTest {

    @ParameterizedTest(name = "{index} - check if account {0}{1} is valid")
    @CsvSource(value = {
            "4101000000012USD112,5",
            "4000000000046INR046,Q"
    })
    void keyGeneratorTest(String account, char keyExpected) {
        var key = AccountKeyGenerator.key(account);
        assertThat(key, equalTo(keyExpected));
    }
}