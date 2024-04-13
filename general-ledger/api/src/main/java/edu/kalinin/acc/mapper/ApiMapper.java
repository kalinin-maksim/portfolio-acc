package edu.kalinin.acc.mapper;

import org.mapstruct.Mapper;
import edu.kalinin.acc.entity.Balance;
import edu.kalinin.acc.entity.GLBalance;
import edu.kalinin.acc.helper.ParseHelper;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {
                ParseHelper.class
        })
public interface ApiMapper {

    List<edu.kalinin.acc.dto.AccountBalance> toAccountBalanceDtos(List<Balance> balance);

    List<edu.kalinin.acc.dto.AccountGLBalance> toAccountGLBalanceDtos(List<GLBalance> glBalance);

}
