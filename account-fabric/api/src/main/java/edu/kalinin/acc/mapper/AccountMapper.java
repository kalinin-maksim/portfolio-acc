package edu.kalinin.acc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import edu.kalinin.acc.dto.AccountPostRq;
import edu.kalinin.acc.entity.Account;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    edu.kalinin.acc.dto.Account toDto(Account account);

    List<edu.kalinin.acc.dto.Account> toDtos(List<Account> accounts);

    Account update(@MappingTarget Account account, AccountPostRq accountPostRq);

    default String toString(Date date) {
        return date != null ? new SimpleDateFormat("yyyy-MM-dd").format(date) : null;
    }

    default UnaryOperator<Account> updaterWith(AccountPostRq accountPostRq) {
        return account -> {
            update(account, accountPostRq);
            return account;
        };
    }
}
