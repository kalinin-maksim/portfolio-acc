package edu.kalinin.acc.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import edu.kalinin.acc.entity.PostingView;
import edu.kalinin.acc.graphql.Filter;
import edu.kalinin.acc.graphql.PostingOutput;
import edu.kalinin.acc.helper.FormatHelper;
import edu.kalinin.acc.helper.JPAHelper;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true),
        uses = {FormatHelper.class})
public interface GraphqlPostingMapper {
    PostingOutput toGraphqlEntity(PostingView posting);
    List<JPAHelper.JpaFilter> toJpaFilter(List<Filter> filters);
}
