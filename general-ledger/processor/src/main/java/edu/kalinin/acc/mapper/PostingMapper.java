package edu.kalinin.acc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import edu.kalinin.acc.entity.Entry;
import edu.kalinin.acc.entity.Posting;
import edu.kalinin.acc.entity.ProcessedEntry;
import edu.kalinin.acc.helper.ParseHelper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {
                ParseHelper.class
        })
public interface PostingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postingId", ignore = true)
    @Mapping(target = "valueDate", ignore = true)
    @Mapping(target = "bookingDate", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "side", ignore = true)
    @Mapping(target = "currencyCode", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "localAmount", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(source = "entry.id", target = "entryId")
    Posting toPosting(Entry entry);

    Posting clone(Posting posting);

    @Mapping(target = "date", expression = "java(new java.sql.Timestamp(new java.util.Date().getTime()))")
    ProcessedEntry toProcessedEntry(Entry entry);

    List<ProcessedEntry> toProcessedEntries(List<Entry> entry);

    default List<Posting> toPostings(Entry entry) {
        var postingDt = toPosting(entry);
        postingDt.setActive(true);
        postingDt.setAccountNumber(entry.getDebitAccount());
        postingDt.setSide(Posting.Side.D);
        postingDt.setAmount(entry.getDebitAmount());
        postingDt.setLocalAmount(entry.getDebitLocalAmount());
        postingDt.setValueDate(entry.getDebitValueDate());
        postingDt.setCurrencyCode(entry.getDebitCurrencyCode());
        postingDt.setAccountIsGl(entry.isDebitIsGl());
        update(postingDt, entry);
        initNull(postingDt);

        var postingCt = toPosting(entry);
        postingCt.setActive(true);
        postingCt.setAccountNumber(entry.getCreditAccount());
        postingCt.setSide(Posting.Side.C);
        postingCt.setAmount(entry.getCreditAmount());
        postingCt.setLocalAmount(entry.getCreditLocalAmount());
        postingCt.setValueDate(entry.getCreditValueDate());
        postingCt.setCurrencyCode(entry.getCreditCurrencyCode());
        postingCt.setAccountIsGl(entry.isCreditIsGl());
        update(postingCt, entry);
        initNull(postingCt);

        return Arrays.asList(postingDt, postingCt);
    }

    default void initNull(Posting posting) {
        if (posting.getAmount() == null) posting.setAmount(BigDecimal.ZERO);
        if (posting.getLocalAmount() == null) posting.setLocalAmount(BigDecimal.ZERO);
    }

    private void update(Posting posting, Entry entry) {
        if (!entry.isMultiOffset()) {
            posting.setPostingId(entry.getId());
        }
        posting.setBookingDate(entry.getEventDate());
        posting.setStatus(getStatus(entry));
    }

    private String getStatus(Entry entry) {
        if (entry.getSourceOfPosting().equals("land.UI")) {
            return "In review";
        } else {
            return "Authorized";
        }
    }
}
