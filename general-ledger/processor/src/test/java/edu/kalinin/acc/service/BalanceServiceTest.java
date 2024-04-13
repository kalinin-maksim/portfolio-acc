package edu.kalinin.acc.service;

import com.opencsv.CSVReader;
import lombok.SneakyThrows;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import edu.kalinin.acc.entity.*;
import edu.kalinin.acc.helper.SpringSupport;
import edu.kalinin.acc.mapper.BalanceMapperImpl;
import edu.kalinin.acc.mapper.PostingMapperImpl;
import edu.kalinin.acc.repository.*;

import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.*;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.function.BinaryOperator.maxBy;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.BDDAssertions.then;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EntityScan(basePackageClasses = Balance.class)
@EnableJpaRepositories(basePackageClasses = BalanceRepository.class)
@ContextConfiguration(classes = {
        BalanceService.class,
        AccountingAlgebra.class,
        PostingMapperImpl.class,
        BalanceMapperImpl.class,
        SpringSupport.class
})
class BalanceServiceTest {

    DateTimeFormatter TIME = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();
    DateTimeFormatter DATE_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral('T')
            .append(TIME)
            .toFormatter();

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private PostingRepository postingRepository;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private GlBalanceRepository glBalanceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountClassRepository accountClassRepository;

    @Test
    void getBalanceTest() {
        //given
        var entriesFromCSV = getEntriesFromCSV();
        entryRepository.saveAll(entriesFromCSV);

        createAccount("3030", AccountClass.Sign.C, "001", "000000023", "INR");
        createAccount("3060", AccountClass.Sign.C, "001", "000000023", "INR");

        //when
        var operationDate = entriesFromCSV.stream()
                .min(comparing(Entry::getDebitValueDate))
                .map(Entry::getDebitValueDate)
                .get();

        balanceService.createPostings();

        balanceService.getBalance();
        balanceService.getGlBalance();

        //then
        {
            var resultBalanceFromDB = balanceRepository.findAll();
            var expectedBalanceFromCSV = getExpectedBalanceFromCSV();
            then(resultBalanceFromDB).hasSize(expectedBalanceFromCSV.size());

            var resultBalanceByAccountNumber = resultBalanceFromDB.stream()
                    .collect(groupingBy(Balance::getAccountNumber));

            var expectedBalanceByAccountNumber = expectedBalanceFromCSV.stream()
                    .collect(groupingBy(Balance::getAccountNumber));

            for (var entry : expectedBalanceByAccountNumber.entrySet()) {
                var account = entry.getKey();
                var expectedBalances = entry.getValue();

                var resultBalances = resultBalanceByAccountNumber.get(account);

                then(resultBalances).hasSize(1);

                then(resultBalances.get(0).getOpeningBalance().compareTo(expectedBalances.get(0).getOpeningBalance())).isEqualTo(0);
                then(resultBalances.get(0).getDrTurnover().compareTo(expectedBalances.get(0).getDrTurnover())).isEqualTo(0);
                then(resultBalances.get(0).getCrTurnover().compareTo(expectedBalances.get(0).getCrTurnover())).isEqualTo(0);
                then(resultBalances.get(0).getClosingBalance().compareTo(expectedBalances.get(0).getClosingBalance())).isEqualTo(0);
            }
        }

        {
            var resultGLBalanceFromDB = glBalanceRepository.findAll();
            var expectedGLBalanceFromCSV = getExpectedGLBalanceFromCSV();
            then(resultGLBalanceFromDB).hasSize(expectedGLBalanceFromCSV.size());

            var resultBalanceByAccountNumber = resultGLBalanceFromDB.stream()
                    .collect(groupingBy(GLBalance::getGlCode));

            var expectedBalanceByAccountNumber = expectedGLBalanceFromCSV.stream()
                    .collect(groupingBy(GLBalance::getGlCode));

            for (var entry : expectedBalanceByAccountNumber.entrySet()) {
                var account = entry.getKey();
                var expectedBalances = entry.getValue();

                var resultBalances = resultBalanceByAccountNumber.get(account);

                then(resultBalances).hasSize(1);

                then(resultBalances.get(0).getDrMovement().compareTo(expectedBalances.get(0).getDrMovement())).isEqualTo(0);
                then(resultBalances.get(0).getCrMovement().compareTo(expectedBalances.get(0).getCrMovement())).isEqualTo(0);
            }
        }
    }

    private Account createAccount(String class_id,
                                  AccountClass.Sign sign,
                                  String branch,
                                  String flexcubeCustomerId,
                                  String currencyCode) {
        var accountClass = Instancio.of(AccountClass.class)
                .set(Select.field(AccountClass::getId), class_id)
                .set(Select.field(AccountClass::getSign), sign)
                .create();
        var account = Instancio.of(Account.class)
                .set(Select.field(Account::getClassCode), accountClass.getId())
                .set(Select.field(Account::getBranch), branch)
                .create();
        account.setClassCode(accountClass.getId());
        account.setFlexcubeCustomerId(flexcubeCustomerId);
        account.setCurrencyCode(currencyCode);
        accountClassRepository.save(accountClass);
        return accountRepository.trySave(account);
    }

    @Test
    void getBalanceGeneratedTest() {

        List<String> accounts = new ArrayList<>();

        {
            var account = createAccount("1001", AccountClass.Sign.D, "001", "000000001", "INR");
            accounts.add(account.getNumber());
        }
        {
            var account = createAccount("1002", AccountClass.Sign.D, "001", "000000002", "INR");
            accounts.add(account.getNumber());
        }
        {
            var account = createAccount("1003", AccountClass.Sign.C, "001", "000000003", "INR");
            accounts.add(account.getNumber());
        }

        var entryBuilder = entryBuilder(accounts);

        //given
        var periodMonths = 12;
        var operationNumber = 5;

        for (var monthIndex = 0; monthIndex < periodMonths; monthIndex++) {
            var operationDate = Date.valueOf(LocalDate.of(2000, Month.of(monthIndex + 1), 1));

            for (var i = 0; i < operationNumber; i++) {
                var entry = entryBuilder.create();
                entry.setDebitValueDate(operationDate);
                entryRepository.save(entry);
            }

            balanceService.createPostings();
            balanceService.createPostings();//shouldn't affect as operation date was shifted
        }

        //when
        balanceService.getBalance();

        //then
        var entries = entryRepository.findAll();
        var postings = postingRepository.findAll();
        var balanceList = balanceRepository.findAll();
        then(entries).hasSize(periodMonths * operationNumber);
        then(postings).hasSize(2 * entries.size());

        {

            var creditAmountTotal = entries.stream()
                    .map(Entry::getCreditAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            var debitAmountTotal = entries.stream()
                    .map(Entry::getDebitAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            var crTurnoverTotal = balanceList.stream()
                    .map(Balance::getCrTurnover)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            var drTurnoverTotal = balanceList.stream()
                    .map(Balance::getDrTurnover)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            then(creditAmountTotal).isEqualTo(crTurnoverTotal);
            then(debitAmountTotal).isEqualTo(drTurnoverTotal);
        }

        {
            var creditAmountTotalByAccount = entries.stream()
                    .collect(toMap(Entry::getCreditAccount, Entry::getCreditAmount, BigDecimal::add));

            var debitAmountTotalByAccount = entries.stream()
                    .collect(toMap(Entry::getDebitAccount, Entry::getDebitAmount, BigDecimal::add));

            var closingBalanceByAccountNumber = balanceList.stream()
                    .collect(toMap(Balance::getAccountNumber, Function.identity(), maxBy(comparing(Balance::getBookingDate))));

            for (var entry : closingBalanceByAccountNumber.entrySet()) {
                var accountNumber = entry.getKey();
                var accountClass = accountRepository.getClass(accountNumber, accountClassRepository);
                var factor = balanceService.getFactor(accountClass);

                var closingBalance = entry.getValue().getClosingBalance();
                var creditAmountTotal = ofNullable(creditAmountTotalByAccount.get(accountNumber)).orElse(BigDecimal.ZERO);
                var debitAmountTotal = ofNullable(debitAmountTotalByAccount.get(accountNumber)).orElse(BigDecimal.ZERO);
                var saldo = debitAmountTotal.subtract(creditAmountTotal);
                var saldoToAdd = saldo.multiply(factor);
                then(closingBalance).isEqualTo(saldoToAdd);
            }
        }
    }

    private List<Entry> getEntriesFromCSV() {
        //entries
        var eventDate = new Date(1);
        return parseCsv("src/test/resources/test01/entries.csv").stream()
                .map(el -> Instancio.of(Entry.class)
                        .set(Select.field("debitAccount"), el.get(0))
                        .set(Select.field("creditAccount"), el.get(1))
                        .set(Select.field("debitAmount"), BigDecimal.valueOf(Double.parseDouble(el.get(2))))
                        .set(Select.field("creditAmount"), BigDecimal.valueOf(Double.parseDouble(el.get(3))))
                        .set(Select.field("operationBranch"), el.get(4))
                        .set(Select.field(Entry::getEventDate), eventDate)
                        .generate(Select.fields().matching(".*TimeStamp.*"), gen -> gen.oneOf(getDateTimeAsString()))
                        .create()
                ).collect(Collectors.toList());
    }


    private List<GLBalance> getExpectedGLBalanceFromCSV() {
        //gl_balance
        return parseCsv("src/test/resources/test01/gl_balance.csv").stream()
                .map(el -> Instancio.of(GLBalance.class)
                        .set(Select.field("glCode"), el.get(0))
                        .set(Select.field("drMovement"), BigDecimal.valueOf(Double.parseDouble(el.get(1))))
                        .set(Select.field("crMovement"), BigDecimal.valueOf(Double.parseDouble(el.get(2))))
                        .create()
                ).collect(Collectors.toList());
    }

    private List<Balance> getExpectedBalanceFromCSV() {
        //gl_account_balance
        return parseCsv("src/test/resources/test01/balance.csv").stream()
                .map(el -> Instancio.of(Balance.class)
                        .set(Select.field("accountNumber"), el.get(0))
                        .set(Select.field("openingBalance"), BigDecimal.valueOf(Double.parseDouble(el.get(1))))
                        .set(Select.field("drTurnover"), BigDecimal.valueOf(Double.parseDouble(el.get(2))))
                        .set(Select.field("crTurnover"), BigDecimal.valueOf(Double.parseDouble(el.get(3))))
                        .set(Select.field("closingBalance"), BigDecimal.valueOf(Double.parseDouble(el.get(4))))
                        .create()
                ).collect(Collectors.toList());
    }

    @SneakyThrows
    private List<List<String>> parseCsv(String path) {
        List<List<String>> parsedCsv = new ArrayList<>();
        try (var csvReader = new CSVReader(new FileReader(path))) {
            String[] strings;
            while ((strings = csvReader.readNext()) != null) {
                for (var string : strings) {
                    parsedCsv.add(Arrays.asList(string.split(",")));
                }
            }
        }
        return parsedCsv;
    }

    private InstancioApi<Entry> entryBuilder(List<String> accounts) {
        var accountingId = new AtomicInteger();
        return Instancio.of(Entry.class)
                .set(Select.field(Entry::getId), null)
                .supply(Select.field(Entry::getAccountingId), () -> String.valueOf(accountingId.incrementAndGet()))
                .supply(Select.field(Entry::getOperationBranch), () -> "001")
                .generate(Select.fields().matching(".*Account.*"), gen -> gen.oneOf(accounts))
                .generate(Select.fields().matching(".*Amount.*"), gen -> gen.oneOf(BigDecimal.valueOf(100), BigDecimal.valueOf(250), BigDecimal.valueOf(300)))
                .generate(Select.fields().matching(".*TimeStamp.*"), gen -> gen.oneOf(getDateTimeAsString()));
    }

    private String getDateTimeAsString() {
        return Instancio.create(OffsetDateTime.class).toString();
    }

}