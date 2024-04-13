package edu.kalinin.acc.helper;

import lombok.Value;
import lombok.experimental.UtilityClass;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.Boolean.parseBoolean;
import static edu.kalinin.acc.helper.ParseHelper.toBigDecimal;
import static edu.kalinin.acc.helper.ParseHelper.toDate;

@UtilityClass
public class JPAHelper {

    public static <E> Optional<E> getOne(Function<CriteriaBuilder, CriteriaQuery<E>> criteriaConstructor) {
        var entityManager = SpringSupport.getContext().getBean(EntityManager.class);
        var criteriaQuery = criteriaConstructor.apply(entityManager.getCriteriaBuilder());
        var resultList = entityManager.createQuery(criteriaQuery).getResultList();
        if (resultList.size() > 1) throw new IllegalStateException();
        return resultList.stream().findFirst();
    }


    public static <T> List<T> getList(Class<T> entityClass, List<JpaFilter> jpaFilters) {
        var entityManager = SpringSupport.getContext().getBean(EntityManager.class);
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(entityClass);
        var root = cq.from(entityClass);
        var predicates = jpaFilters.stream()
                .map(jpaFilter -> getPredicate(cb, root, jpaFilter.field, jpaFilter.compareType, jpaFilter.value.toArray(String[]::new)))
                .toArray(Predicate[]::new);
        cq.where(predicates);
        return entityManager.createQuery(cq).getResultList();
    }

    public static <T> Predicate getPredicate(CriteriaBuilder cb,
                                             Root<T> root,
                                             String fieldName,
                                             String comparisonType,
                                             String[] values) {
        var value = cast(values[0], root.get(fieldName).type());
        var value2 = values.length > 1 ? cast(values[1], root.get(fieldName).type()) : null;

        switch (comparisonType) {
            case "eq":
                return cb.equal(root.get(fieldName), value);
            case "neq":
                return cb.notEqual(root.get(fieldName), value);
            case "like":
                return cb.like(root.get(fieldName), "%" + value + "%");
            case "gt":
                return cb.greaterThan(root.get(fieldName), getValue(value));
            case "gte":
                return cb.greaterThanOrEqualTo(root.get(fieldName), getValue(value));
            case "lt":
                return cb.lessThan(root.get(fieldName), getValue(value));
            case "lte":
                return cb.lessThanOrEqualTo(root.get(fieldName), getValue(value));
            case "between":
                return cb.between(root.get(fieldName), getValue(value), getValue(value2));
            default:
                throw new IllegalArgumentException("Invalid comparison type: " + comparisonType);
        }
    }

    private static Comparable<?> cast(String value, Expression<Class<?>> type) {
        if (type.getJavaType().equals(String.class)) return value;
        if (type.getJavaType().equals(BigDecimal.class)) return toBigDecimal(value);
        if (type.getJavaType().equals(Date.class)) return toDate(value);
        if (type.getJavaType().isPrimitive()) {
            switch (type.getJavaType().getSimpleName()) {
                case "boolean":
                    return parseBoolean(value);
                case "integer":
                    return Integer.parseInt(value);
                case "long":
                    return Long.parseLong(value);
            }
        }
        try {
            return (Comparable<?>) type.getJavaType().getDeclaredMethod("valueOf", String.class).invoke(null, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }
        throw new IllegalArgumentException("Not supported type: " + type.getJavaType());
    }

    @SuppressWarnings("java:S3740")
    private static Comparable getValue(Comparable<?> value) {
        return value;
    }

    @Value
    public static class JpaFilter {
        String field;
        String compareType;
        List<String> value;
    }
}
