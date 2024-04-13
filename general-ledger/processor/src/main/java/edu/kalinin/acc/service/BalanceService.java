package edu.kalinin.acc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import edu.kalinin.acc.entity.Balance;
import edu.kalinin.acc.entity.GLBalance;
import edu.kalinin.acc.entity.Posting;
import edu.kalinin.acc.mapper.BalanceMapper;
import edu.kalinin.acc.mapper.PostingMapper;
import edu.kalinin.acc.repository.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static edu.kalinin.acc.helper.CollectorHelper.dimension;

@Component
@RequiredArgsConstructor
public class BalanceService {

    private final AccountingAlgebra algebra;

    private final EntryRepository entryRepository;
    private final ProcessedEntryRepository processedEntryRepository;
    private final PostingRepository postingRepository;
    private final BalanceRepository balanceRepository;
    private final GlBalanceRepository glBalanceRepository;
    private final AccountClassRepository accountClassRepository;
    private final AccountRepository accountRepository;

    private final PostingMapper postingMapper;
    private final BalanceMapper balanceMapper;

    @Transactional
    public void createPostings() {
        var entries = entryRepository.findEntriesToProcess();

        if (!entries.isEmpty()) {

            var postings = entries.stream()
                    .flatMap(entry -> postingMapper.toPostings(entry).stream())
                    .collect(Collectors.toList());

            postingRepository.saveAll(postings);

            processedEntryRepository.saveAll(postingMapper.toProcessedEntries(entries));

        }
    }

    public List<Balance> getBalance() {

        var postings = postingRepository.findByActiveTrue().stream()
                .filter(posting -> !balanceRepository.existsById(posting.getId()))
                .collect(Collectors.toList());

        var postingCollection = postings.stream()
                .collect(Collectors.toMap(dimension(Posting::getAccountNumber, Posting::getOperationBranch, Posting::getBookingDate, Posting::getSide), Function.identity(), algebra::postingSum))
                .values();

        var balanceCollection = postingCollection.stream()
                .filter(posting -> posting.getAccountNumber().length() == 20)
                .map(balanceMapper::toBalance)
                .collect(Collectors.toMap(dimension(Balance::getAccountNumber, Balance::getBranch, Balance::getBookingDate), Function.identity(), algebra::balanceSum))
                .values();


        List<Balance> balances = new ArrayList<>(balanceCollection);

        for (var balance : balances) {
            var accountClass = accountRepository.getClass(balance.getAccountNumber(), accountClassRepository);
            accountClassRepository.findById(accountClass);
            var factor = getFactor(accountClass);

            var crTotalAmount = algebra.getTotalAmount(postings,
                    balance.getAccountNumber(),
                    balance.getBookingDate(),
                    Posting.Side.C).orElse(BigDecimal.ZERO);
            var drTotalAmount = algebra.getTotalAmount(postings,
                    balance.getAccountNumber(),
                    balance.getBookingDate(),
                    Posting.Side.D).orElse(BigDecimal.ZERO);

            var openingBalance = drTotalAmount.subtract(crTotalAmount).multiply(factor);
            balance.setOpeningBalance(openingBalance);
            var saldo = balance.getDrTurnover().subtract(balance.getCrTurnover());
            var saldoToAdd = saldo.multiply(factor);
            balance.setClosingBalance(openingBalance.add(saldoToAdd));
            balance.setOpeningBalanceLocal(balance.getOpeningBalance());
            balance.setClosingBalanceLocal(balance.getClosingBalance());
            balance.setCurrencyCode(accountRepository.getCurrencyCode(balance.getAccountNumber()));
        }

        balanceRepository.saveAll(balances);

        return balanceRepository.findAll();
    }

    public List<GLBalance> getGlBalance() {

        var postings = postingRepository.findByActiveTrue();

        var postingCollection = postings.stream()
                .collect(Collectors.toMap(dimension(Posting::getAccountNumber, Posting::getOperationBranch, Posting::getBookingDate, Posting::getSide), Function.identity(), algebra::postingSum))
                .values();

        var balanceCollection = postingCollection.stream()
                .filter(posting -> posting.getAccountNumber().length() == 9)
                .map(balanceMapper::toGLBalance)
                .collect(Collectors.toMap(dimension(GLBalance::getGlCode, GLBalance::getBranch, GLBalance::getBookingDate), Function.identity(), algebra::glBalanceSum))
                .values();

        glBalanceRepository.saveAll(balanceCollection);

        return glBalanceRepository.findAll();
    }

    public BigDecimal getFactor(String factor) {
        if (factor.equals("C")) {
            return new BigDecimal("-1");
        }
        return BigDecimal.ONE;
    }
}