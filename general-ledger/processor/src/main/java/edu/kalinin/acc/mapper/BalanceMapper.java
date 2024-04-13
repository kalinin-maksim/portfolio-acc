package edu.kalinin.acc.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import edu.kalinin.acc.entity.Balance;
import edu.kalinin.acc.entity.GLBalance;
import edu.kalinin.acc.entity.Posting;

import static java.math.BigDecimal.ZERO;
import static edu.kalinin.acc.entity.Posting.Side.D;

@Mapper(componentModel = "spring")
public interface BalanceMapper {

    Balance clone(Balance balance);
    GLBalance clone(GLBalance balance);

    Balance toBalance(Posting posting);

    @Mapping(target = "glCode", source = "accountNumber")
    GLBalance toGLBalance(Posting posting);

    @AfterMapping
    default Balance update(@MappingTarget Balance balance, Posting posting) {
        if (posting.getSide() == D) {
            balance.setDrTurnover(posting.getAmount());
            balance.setDrTurnoverLocal(posting.getLocalAmount());

            balance.setCrTurnover(ZERO);
            balance.setCrTurnoverLocal(ZERO);
        } else {
            balance.setDrTurnover(ZERO);
            balance.setDrTurnoverLocal(ZERO);

            balance.setCrTurnover(posting.getAmount());
            balance.setCrTurnoverLocal(posting.getLocalAmount());
        }
        return balance;
    }

    @AfterMapping
    default GLBalance update(@MappingTarget GLBalance balance, Posting posting) {

        if (posting.getSide() == D) {
            balance.setDrMovement(posting.getAmount());
            balance.setDrMovementLocal(posting.getLocalAmount());

            balance.setCrMovement(ZERO);
            balance.setCrMovementLocal(ZERO);
        } else {
            balance.setDrMovement(ZERO);
            balance.setDrMovementLocal(ZERO);

            balance.setCrMovement(posting.getAmount());
            balance.setCrMovementLocal(posting.getLocalAmount());
        }
        return balance;
    }
}
