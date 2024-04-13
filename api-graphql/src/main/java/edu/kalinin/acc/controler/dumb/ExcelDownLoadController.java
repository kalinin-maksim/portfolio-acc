package edu.kalinin.acc.controler.dumb;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import edu.kalinin.acc.entity.PostingView;
import edu.kalinin.acc.graphql.Filter;
import edu.kalinin.acc.mapper.GraphqlPostingMapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static edu.kalinin.acc.helper.ExcelHelper.getExcelFileAsBytes;
import static edu.kalinin.acc.helper.JPAHelper.getList;

@Controller
@Log4j2
@RequiredArgsConstructor
public class ExcelDownLoadController {

    private final GraphqlPostingMapper graphqlMapper;

    @PostMapping("/postings/download")
    public ResponseEntity<byte[]> downloadFile(@RequestBody Filter[] filters) throws IOException {
        byte[] body = getBytes(Arrays.stream(filters).collect(Collectors.toList()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=postings.xls")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(body.length)
                .body(body);
    }

    private byte[] getBytes(List<Filter> filters) throws IOException {

        return getExcelFileAsBytes("/download/postingJournalTemplate.xls", getData(filters), log);
    }

    private Map<String, Object> getData(List<Filter> filters) {
        Map<String, Object> data = new HashMap<>();
        List<PostingView> postings = getList(PostingView.class, graphqlMapper.toJpaFilter(filters));
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
