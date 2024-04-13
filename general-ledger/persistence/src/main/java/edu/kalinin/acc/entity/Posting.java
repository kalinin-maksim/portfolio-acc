package edu.kalinin.acc.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.OffsetDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "acc_posting")
public class Posting {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, length = 32)
    private String id;

    @Column(name = "operation_branch")
    private String operationBranch;

    @Column(name = "accounting_id")
    private String accountingId;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "entry_id")
    private String entryId;

    @Column(name = "posting_id")
    private String postingId;

    @Column(name = "maker")
    private String maker;

    @Column(name = "checker")
    private String checker;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "deal_id")
    private String dealId;

    @Column(name = "sub_deal_id")
    private String subDealId;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "value_date")
    private Date valueDate;

    @Column(name = "booking_date")
    private Date bookingDate;

    @Column(name = "operation_time_stamp")
    private OffsetDateTime operationTimeStamp;

    @Column(name = "source_of_posting")
    private String sourceOfPosting;

    @Column(name = "account_number")
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "side", length = 1)
    private Side side;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "local_amount")
    private BigDecimal localAmount;

    @Column(name = "narrative")
    private String narrative;

    @Column(name = "active")
    private boolean active;

    @Column(name = "accounting_event_type")
    private String accountingEventType;

    @Column(name = "status")
    private String status;

    @Column(name = "deal_type")
    private String dealType;

    @Column(name = "reverse")
    private boolean reverse;

    public enum Side {
        D, C
    }

    @Column(name = "account_is_gl")
    private boolean accountIsGl;

    @Override
    public String toString() {
        return new java.util.StringJoiner(", ", "{", "}")
                .add("\"id\":\"" + id + "\"")
                .add("\"accountNumber\":\"" + accountNumber + "\"")
                .add("\"bookingDate\":\"" + bookingDate + "\"")
                .add("\"side\":\"" + side + "\"")
                .add("\"amount\":\"" + amount+"\"")
                .add("\"currencyCode\":\"" + currencyCode+"\"")
                .toString();
    }
}
