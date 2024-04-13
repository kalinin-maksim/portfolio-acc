package edu.kalinin.acc.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static edu.kalinin.acc.service.ObjectComparator.FiledDescriptor.$;

public class ObjectComparator {
    @Data
    public static class Person {
        String name;
        int age;
    }

    public static void main(String[] args) {
        String csv1 = "name;age\nJohn;25\nJane;30\n";
        String expectedCsv = "name;age\nJane;30\nJohn;21\n";

        List<Person> list = createListFromCsv(Person.class, csv1);
        List<Person> expectedList = createListFromCsv(Person.class, expectedCsv);

        var mismatches = compareObjects(list, expectedList, List.of($("name", Person::getName)), List.of($("age", Person::getAge)));

        System.out.println(mismatches);
    }

    @SneakyThrows
    public static <T> List<T> createListFromCsvFile(Class<T> type, String filePath) {
        Reader reader = new FileReader(filePath);

        CsvToBean<T> csvReader = getCsvReader(type, reader);


        Stream<T> stream = csvReader.stream();

        return stream.collect(Collectors.toList());
    }

    @SneakyThrows
    public static <T> List<T> createListFromCsv(Class<T> type, String csv) {
        Reader reader = new BufferedReader(new StringReader(csv));

        CsvToBean<T> csvReader = getCsvReader(type, reader);

        Stream<T> stream = csvReader.stream();

        return stream.collect(Collectors.toList());
    }

    public static <T> StringBuilder compareObjects(List<T> objects,
                                                   List<T> samples,
                                                   List<FiledDescriptor<T>> keyGetters,
                                                   List<FiledDescriptor<T>> comparedFiledDescriptors) {

        var mismatches = new StringBuilder();

        if (objects.size() != samples.size()) {
            mismatches.append(format("set sizes doesn't matches: actual - '%s', expected - '%s'", objects.size(), samples.size()));
        } else {
            objects.sort(comparing(o -> getKeyString(o, keyGetters)));
            samples.sort(comparing(o -> getKeyString(o, keyGetters)));

            for (int rowId = 0; rowId < objects.size(); rowId++) {
                T object = objects.get(rowId);
                T sample = samples.get(rowId);

                Set<Object> objectKeySet = keyGetters.stream()
                        .map(descriptor -> descriptor.getter.apply(object))
                        .collect(toSet());

                Set<Object> sampleKeySet = keyGetters.stream()
                        .map(descriptor -> descriptor.getter.apply(sample))
                        .collect(toSet());

                if (!objectKeySet.equals(sampleKeySet)) {
                    mismatches.append(format("set of keys doesn't equals: object - '%s', sample - '%s'", objectKeySet, sampleKeySet));
                    continue;
                } else {
                    for (var filedDescriptor : comparedFiledDescriptors) {
                        Object expectedValue = filedDescriptor.getter.apply(sample);
                        Object actualValue = filedDescriptor.getter.apply(object);

                        if ((actualValue == null && expectedValue != null) ||
                                (actualValue != null && !actualValue.equals(expectedValue))) {
                            mismatches.append(format("in object on row '%s' with keys '%s' fields doesn't matches. filed '%s' expected '%s', but found '%s'",
                                    rowId + 1,
                                    objectKeySet,
                                    filedDescriptor.name,
                                    expectedValue,
                                    actualValue));
                        }
                    }
                }
            }
        }

        return mismatches;
    }

    private static <T> CsvToBean<T> getCsvReader(Class<T> type, Reader reader) {
        CsvToBean<T> csvReader = new CsvToBeanBuilder(reader)
                .withType(type)
                .withSeparator(';')
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                .build();
        return csvReader;
    }

    private static <T, R> String getKeyString(T obj, List<FiledDescriptor<T>> keyFields) {
        return keyFields.stream()
                .map(descriptor -> String.valueOf(descriptor.getter.apply(obj)))
                .collect(Collectors.joining("-"));
    }

    public static final class FiledDescriptor<T> {
        private final String name;
        private final Function<T, Object> getter;

        private FiledDescriptor(String name, Function<T, Object> getter) {
            this.name = name;
            this.getter = getter;
        }

        public static <T> FiledDescriptor<T> $(String name, Function<T, Object> getter) {
            return new FiledDescriptor<T>(name, getter);
        }
    }
}
