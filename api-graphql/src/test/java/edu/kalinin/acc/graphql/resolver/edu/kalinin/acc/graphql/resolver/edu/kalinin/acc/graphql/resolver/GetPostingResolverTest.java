package edu.kalinin.acc.graphql.resolver;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Disabled;
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
import edu.kalinin.acc.entity.Posting;
import edu.kalinin.acc.entity.PostingView;
import edu.kalinin.acc.entity.Posting_;
import edu.kalinin.acc.graphql.ComparisonType;
import edu.kalinin.acc.graphql.Filter;
import edu.kalinin.acc.helper.SpringSupport;
import edu.kalinin.acc.mapper.GraphqlPostingMapperImpl;
import edu.kalinin.acc.repository.PostingRepository;
import edu.kalinin.acc.repository.PostingViewRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static edu.kalinin.acc.helper.ConversionHelper.getSqlDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EntityScan(basePackageClasses = Posting.class)
@EnableJpaRepositories(basePackageClasses = PostingRepository.class)
@ContextConfiguration(classes = {
        GetPostingResolver.class,
        GraphqlPostingMapperImpl.class,
        SpringSupport.class
})
class GetPostingResolverTest {

    @Autowired
    GetPostingResolver getPostingResolver;

    @Autowired
    PostingViewRepository postingRepository;

    @Test
    void getPostingsByFilters() {
        var accountNumber = "1";
        var date = LocalDate.of(2019, 1, 2);

        var posting = new PostingView();
        posting.setId("0");
        posting.setAccountNumber(accountNumber);
        posting.setBookingDate(getSqlDate(date));
        postingRepository.save(posting);

        var filters = List.of(
                getFilter(Posting_.ACCOUNT_NUMBER, ComparisonType.eq, accountNumber),
                getFilter(Posting_.BOOKING_DATE, ComparisonType.gt, date.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)));
        var postings = getPostingResolver.getPostingsByFilters(filters);
        then(postings).hasSize(1);
    }

    @Test
    @Disabled
    void getPostingReport() {
        var accountNumber = "1";
        var date = LocalDate.of(2019, 1, 2);

        var posting = new PostingView();
        posting.setId("0");
        posting.setAccountNumber(accountNumber);
        posting.setBookingDate(getSqlDate(date));
        posting.setValueDate(Date.valueOf(date));
        postingRepository.save(posting);

        var filters = List.of(
                getFilter(Posting_.ACCOUNT_NUMBER, ComparisonType.eq, accountNumber),
                getFilter(Posting_.BOOKING_DATE, ComparisonType.gt, date.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)));
        var report = getPostingResolver.getPostingReport(filters);
        BDDAssertions.then(report.getContent()).hasSizeGreaterThan(0);
    }

    private Filter getFilter(String field, ComparisonType compareType, String value) {
        var filter = new Filter();
        filter.setField(field);
        filter.setCompareType(compareType);
        filter.setValue(List.of(value));
        return filter;
    }
}