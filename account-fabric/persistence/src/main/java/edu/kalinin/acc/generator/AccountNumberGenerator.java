package edu.kalinin.acc.generator;

import lombok.Value;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import edu.kalinin.acc.entity.Account;
import edu.kalinin.acc.exception.FlexCubeAlgorithmException;
import edu.kalinin.acc.repository.AccountRepository;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

public class AccountNumberGenerator extends SequenceStyleGenerator {

    private static final String NUMBER_FORMAT = "%s%s%s%03d";
    private final Map<Dimension, Long> counter = new ConcurrentHashMap<>();

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(LongType.INSTANCE, params, serviceRegistry);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        var account = (Account) object;
        var dimension = Dimension.of(account.getClassCode(),
                account.getFlexcubeCustomerId(),
                account.getCurrencyCode());

        counter.computeIfAbsent(dimension, code -> getLastCode(dimension));

        var nextCode = counter.computeIfPresent(dimension, (d, code) -> code + 1);

        if (nextCode > 999L) throw new FlexCubeAlgorithmException(account.getNumber());

        var number = format(NUMBER_FORMAT,
                dimension.getClassCode(),
                dimension.getFlexcubeCustomerId(),
                dimension.getCurrencyCode(),
                nextCode);
        return number + AccountKeyGenerator.key(number);
    }

    private Long getLastCode(Dimension dimension) {
        return AccountRepository.getThis().findByClassCodeAndFlexcubeCustomerIdAndCurrencyCode(dimension.getClassCode(),
                        dimension.getFlexcubeCustomerId(),
                        dimension.getCurrencyCode())
                .map(Account::getNumber)
                .map(number -> Long.parseLong(number.substring(16, 19)))
                .orElse(0L);
    }

    @Value(staticConstructor = "of")
    private static class Dimension {
        String classCode;
        String flexcubeCustomerId;
        String currencyCode;
    }
}
