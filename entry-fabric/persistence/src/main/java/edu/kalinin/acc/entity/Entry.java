package edu.kalinin.acc.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "acc_entry")
public class Entry {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, length = 32)
    private String id;

    @Column(name = "accounting_id")
    private String accountingId;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "parent_accounting_id")
    private String parentAccountingId;

    @Column(name = "accounting_event_type")
    private String accountingEventType;

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

    @Column(name = "deal_type")
    private String dealType;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "operation_branch")
    private String operationBranch;

    @Column(name = "operation_time_stamp")
    private String operationTimeStamp;

    @Column(name = "source_of_posting")
    private String sourceOfPosting;

    @Column(name = "debit_account")
    private String debitAccount;

    @Column(name = "credit_account")
    private String creditAccount;

    @Column(name = "debit_currency_code")
    private String debitCurrencyCode;

    @Column(name = "credit_currency_code")
    private String creditCurrencyCode;

    @Column(name = "debit_value_date")
    private Date debitValueDate;

    @Column(name = "credit_value_date")
    private Date creditValueDate;

    @Column(name = "debit_amount")
    private BigDecimal debitAmount;

    @Column(name = "credit_amount")
    private BigDecimal creditAmount;

    @Column(name = "debit_local_amount")
    private BigDecimal debitLocalAmount;

    @Column(name = "credit_local_amount")
    private BigDecimal creditLocalAmount;

    @Column(name = "narrative")
    private String narrative;

    @Column(name = "reverse")
    private boolean reverse;

    @Column(name = "reversal_reference")
    private String reversalReference;

    @Column(name = "multi_offset")
    private boolean multiOffset;

    @Column(name = "multi_offset_reference")
    private String multiOffsetReference;

    @Column(name = "rule_name")
    private String ruleName;

    @Column(name = "posting_description")
    private String postingDescription;

    @Column(name = "event_date")
    private Date eventDate;

    @Column(name = "debit_is_gl")
    private boolean debitIsGl;

    @Column(name = "credit_is_gl")
    private boolean creditIsGl;

    @Override
    public String toString() {
        return new java.util.StringJoiner(", ", "{", "}")
                .add("\"id\":\"" + id + "\"")
                .add("\"debitValueDate\":\"" + debitValueDate + "\"")
                .add("\"debitAccount\":\"" + debitAccount + "\"")
                .add("\"debitAmount\":\"" + debitAmount+"\"")
                .add("\"creditValueDate\":\"" + creditValueDate + "\"")
                .add("\"creditAccount\":\"" + creditAccount + "\"")
                .add("\"creditAmount\":\"" + creditAmount+"\"")
                .toString();
    }
}