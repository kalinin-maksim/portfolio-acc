package edu.kalinin.acc.graphql.resolver;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import edu.kalinin.acc.dto.DownloadedFile;
import edu.kalinin.acc.entity.PostingView;
import edu.kalinin.acc.graphql.Filter;
import edu.kalinin.acc.graphql.PostingOutput;
import edu.kalinin.acc.mapper.GraphqlPostingMapper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static edu.kalinin.acc.helper.ExcelHelper.getExcelFileAsBytes;
import static edu.kalinin.acc.helper.JPAHelper.getList;

@Component
@RequiredArgsConstructor
@Log4j2
public class GetPostingResolver implements GraphQLQueryResolver {

    private final GraphqlPostingMapper graphqlMapper;

    public List<PostingOutput> getPostingsByFilters(List<Filter> filters) {
        return getList(PostingView.class, graphqlMapper.toJpaFilter(filters)).stream()
                .map(graphqlMapper::toGraphqlEntity)
                .collect(Collectors.toList());
    }

    public DownloadedFile getPostingReport(List<Filter> filters) {
        byte[] body = getExcelFileAsBytes("/download/postingJournalTemplate.xls", getData(filters), log);

        var downloadedFile = new DownloadedFile();
        downloadedFile.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        downloadedFile.setContent(body);
        downloadedFile.setFileName("PostingJournal.xls");
        downloadedFile.setEmpty(body.length == 0);
        downloadedFile.setSize(body.length);
        return downloadedFile;
    }

    private Map<String, Object> getData(List<Filter> filters) {
        Map<String, Object> data = new HashMap<>();
        List<PostingView> postings = getList(PostingView.class, graphqlMapper.toJpaFilter(filters));
        log.info("getPostingReport >> postings size: {}", postings.size());
        data.put("operationBranch", getOperationBranch(postings));
        data.put("startDate", getStartDate(postings));
        data.put("endDate", getEndDate(postings));
        data.put("filters", getFilters(filters));
        data.put("postings", postings);
        return data;
    }

    private String getStartDate(List<PostingView> postings) {
        return postings.stream().map(PostingView::getBookingDate).min(Date::compareTo).map(java.sql.Date::toString).orElse("");
    }

    private String getEndDate(List<PostingView> postings) {
        return postings.stream().map(PostingView::getBookingDate).max(Date::compareTo).map(java.sql.Date::toString).orElse("");
    }

    private String getFilters(List<Filter> filters) {
        return filters.toString();
    }

    private String getOperationBranch(List<PostingView> postings) {
        var result = postings.stream().map(PostingView::getOperationBranch)
                .distinct().collect(Collectors.joining(", "));
        return result.isEmpty() ? "..." : result;
    }
}
