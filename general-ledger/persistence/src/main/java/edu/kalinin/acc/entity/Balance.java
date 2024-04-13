package edu.kalinin.acc.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "acc_account_balance")
public class Balance {
    @Id
    @Column(name = "id", nullable = false, length = 32)
    private String id;

    @Column(name = "branch")
    private String branch;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "booking_date")
    private Date bookingDate;

    @Column(name = "opening_balance")
    private BigDecimal openingBalance;

    @Column(name = "opening_balance_local")
    private BigDecimal openingBalanceLocal;

    @Column(name = "closing_balance")
    private BigDecimal closingBalance;

    @Column(name = "closing_balance_local")
    private BigDecimal closingBalanceLocal;

    @Column(name = "dr_turnover")
    private BigDecimal drTurnover;

    @Column(name = "dr_turnover_local")
    private BigDecimal drTurnoverLocal;

    @Column(name = "cr_turnover")
    private BigDecimal crTurnover;

    @Column(name = "cr_turnover_local")
    private BigDecimal crTurnoverLocal;

    @Column(name = "currency_code")
    private String currencyCode;

    @Override
    public String toString() {
        return new java.util.StringJoiner(", ", "{", "}")
                .add("\"id\":\"" + id + "\"")
                .add("\"accountNumber\":\"" + accountNumber + "\"")
                .add("\"bookingDate\":\"" + bookingDate + "\"")
                .add("\"openingBalance\":\"" + openingBalance + "\"")
                .add("\"crTurnover\":\"" + crTurnover + "\"")
                .add("\"drTurnover\":\"" + drTurnover + "\"")
                .add("\"closingBalance\":\"" + closingBalance + "\"")
                .toString();
    }
}
