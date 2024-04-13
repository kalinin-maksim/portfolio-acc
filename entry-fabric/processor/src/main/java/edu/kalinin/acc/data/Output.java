package edu.kalinin.acc.data;

import java.math.BigDecimal;
import java.sql.Date;

public class Output {
    private String debitAccount;
    private String creditAccount;
    private String debitCurrencyCode;
    private String creditCurrencyCode;
    private Date debitValueDate;
    private Date creditValueDate;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private BigDecimal debitLocalAmount;
    private BigDecimal creditLocalAmount;
    private String narrative;
    private String reversalReference;
    private String multiOffsetReference;
    private String postingDescription;
    private String ruleName;
    private boolean debitIsGl;
    private boolean creditIsGl;
    private String documentId;
    private String channelName;

    private String error;

    public Output(String debitAccount, String creditAccount, String debitAccountKey, String creditAccountKey) {
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;
    }

    public Output(String error) {
        this.error = error;
    }

    public Output() {
    }

    public String getDebitAccount() {
        return debitAccount;
    }

    public void setDebitAccount(String debitAccount) {
        this.debitAccount = debitAccount;
    }

    public String getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(String creditAccount) {
        this.creditAccount = creditAccount;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDebitCurrencyCode() {
        return debitCurrencyCode;
    }

    public void setDebitCurrencyCode(String debitCurrencyCode) {
        this.debitCurrencyCode = debitCurrencyCode;
    }

    public String getCreditCurrencyCode() {
        return creditCurrencyCode;
    }

    public void setCreditCurrencyCode(String creditCurrencyCode) {
        this.creditCurrencyCode = creditCurrencyCode;
    }

    public Date getDebitValueDate() {
        return debitValueDate;
    }

    public void setDebitValueDate(Date debitValueDate) {
        this.debitValueDate = debitValueDate;
    }

    public Date getCreditValueDate() {
        return creditValueDate;
    }

    public void setCreditValueDate(Date creditValueDate) {
        this.creditValueDate = creditValueDate;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public BigDecimal getDebitLocalAmount() {
        return debitLocalAmount;
    }

    public void setDebitLocalAmount(BigDecimal debitLocalAmount) {
        this.debitLocalAmount = debitLocalAmount;
    }

    public BigDecimal getCreditLocalAmount() {
        return creditLocalAmount;
    }

    public void setCreditLocalAmount(BigDecimal creditLocalAmount) {
        this.creditLocalAmount = creditLocalAmount;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public String getReversalReference() {
        return reversalReference;
    }

    public void setReversalReference(String reversalReference) {
        this.reversalReference = reversalReference;
    }

    public String getPostingDescription() {
        return postingDescription;
    }

    public void setPostingDescription(String postingDescription) {
        this.postingDescription = postingDescription;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getMultiOffsetReference() {
        return multiOffsetReference;
    }

    public void setMultiOffsetReference(String multiOffsetReference) {
        this.multiOffsetReference = multiOffsetReference;
    }

    public boolean isDebitIsGl() {
        return debitIsGl;
    }

    public void setDebitIsGl(boolean debitIsGl) {
        this.debitIsGl = debitIsGl;
    }

    public boolean isCreditIsGl() {
        return creditIsGl;
    }

    public void setCreditIsGl(boolean creditIsGl) {
        this.creditIsGl = creditIsGl;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}