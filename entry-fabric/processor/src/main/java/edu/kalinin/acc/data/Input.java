package edu.kalinin.acc.data;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

@Data
public class Input {
    //Transactions
    private String accountingId;
    private String eventId;
    private String parentAccountingId;
    private String systemId;
    private String moduleId;
    private String accountingEventType;
    private Date eventDate;
    private String dealType;
    private String dealId;
    private String subDealId;
    private String operationTimeStamp;
    private String reverse;
    private String multiOffset;
    private String amountBlockSystemCode;
    private String amountBlockId;
    private String operationCountryCode;
    private String operationBranchCode;
    private String maker;
    private String checker;
    private String documentId;
    private String channelName;
    private String payerClientId;
    private String payerClientType;
    private String payerBankCode;
    private String payerAccount;
    private String payerCorrespondentAccount;
    private String beneficiaryClientId;
    private String beneficiaryClientType;
    private String beneficiaryBankCode;
    private String beneficiaryAccount;
    private String beneficiaryCorrespondentAccount;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private String debitCurrencyCode;
    private String creditCurrencyCode;
    private Date debitValueDate;
    private Date creditValueDate;
    private String paymentDetails;
    private String purposeCode;
    private String returnCode;
    private String externalId;
    private String commissionCode;
    private String commissionKind;
    private BigDecimal commissionAmount;
    private String commissionCurrencyCode;
    private String suspenseReasonCode;
    private String paymentDirection;
    private String methodType;

    //Loans
    private String loanAccountNumber;
    private String customerId;
    private String currencyCode;
    private BigDecimal eventAmount;
    private Date valueDate;
    private String startInterestAccrual;
    private String endInterestAccrual;
    private String loanContractNumber;
}