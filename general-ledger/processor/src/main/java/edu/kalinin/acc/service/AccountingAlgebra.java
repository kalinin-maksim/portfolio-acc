package edu.kalinin.acc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import edu.kalinin.acc.entity.Balance;
import edu.kalinin.acc.entity.GLBalance;
import edu.kalinin.acc.entity.Posting;
import edu.kalinin.acc.mapper.BalanceMapper;
import edu.kalinin.acc.mapper.PostingMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountingAlgebra {

    private final PostingMapper postingMapper;
    private final BalanceMapper balanceMapper;

    public Posting postingSum(Posting posting1, Posting posting2) {
        var posting = postingMapper.clone(posting2);

        posting.setAmount(posting1.getAmount().add(posting2.getAmount()));
        posting.setLocalAmount(posting1.getLocalAmount().add(posting2.getLocalAmount()));

        return posting;
    }

    public Balance balanceSum(Balance balance1, Balance balance2) {
        var balance = balanceMapper.clone(balance2);

        balance.setDrTurnover(balance1.getDrTurnover().add(balance2.getDrTurnover()));
        balance.setCrTurnover(balance1.getCrTurnover().add(balance2.getCrTurnover()));

        balance.setDrTurnoverLocal(balance1.getDrTurnoverLocal().add(balance2.getDrTurnoverLocal()));
        balance.setCrTurnoverLocal(balance1.getCrTurnoverLocal().add(balance2.getCrTurnoverLocal()));

        return balance;
    }

    public GLBalance glBalanceSum(GLBalance balance1, GLBalance balance2) {
        var balance = balanceMapper.clone(balance2);

        balance.setDrMovement(balance1.getDrMovement().add(balance2.getDrMovement()));
        balance.setCrMovement(balance1.getCrMovement().add(balance2.getCrMovement()));

        balance.setDrMovementLocal(balance1.getDrMovementLocal().add(balance2.getDrMovementLocal()));
        balance.setCrMovementLocal(balance1.getCrMovementLocal().add(balance2.getCrMovementLocal()));

        return balance;
    }

    public Optional<BigDecimal> getTotalAmount(List<Posting> postings, String accountNumber, Date date, Posting.Side side) {
        return postings.stream()
                .filter(posting -> posting.getAccountNumber().equals(accountNumber))
                .filter(posting -> posting.getBookingDate().before(date))
                .filter(posting -> posting.getSide().equals(side))
                .map(Posting::getAmount)
                .reduce(BigDecimal::add);
    }

}
