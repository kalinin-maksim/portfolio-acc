package edu.kalinin.acc.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "acc_posting_journal")
public class PostingView {
    @Id
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

    private String maker;

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
    private Timestamp operationTimeStamp;

    @Column(name = "source_of_posting")
    private String sourceOfPosting;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "customer_name")
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "side")
    private Posting.Side side;

    @Column(name = "currency_code")
    private String currencyCode;

    private BigDecimal amount;

    @Column(name = "local_amount")
    private BigDecimal localAmount;

    private String narrative;

    private boolean active;

    @Column(name = "accounting_event_type")
    private String accountingEventType;

    private String status;

    @Column(name = "deal_type")
    private String dealType;

    private String reverse;

    private String seq;

    private String customer;
}
