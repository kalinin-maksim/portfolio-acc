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
@Table(name = "acc_gl_balance")
public class GLBalance {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, length = 32)
    private String id;

    @Column(name = "branch")
    private String branch;

    @Column(name = "gl_code")
    private String glCode;

    @Column(name = "booking_date")
    private Date bookingDate;

    @Column(name = "dr_movement")
    private BigDecimal drMovement;

    @Column(name = "dr_movement_local")
    private BigDecimal drMovementLocal;

    @Column(name = "cr_movement")
    private BigDecimal crMovement;

    @Column(name = "cr_movement_local")
    private BigDecimal crMovementLocal;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "period")
    private String period;

    @Column(name = "financial_year")
    private String financialYear;

    @Column(name = "cr_balance")
    private BigDecimal crBalance;

    @Column(name = "cr_balance_local")
    private BigDecimal crBalanceLocal;

    @Column(name = "dr_balance")
    private BigDecimal drBalance;

    @Column(name = "dr_balance_local")
    private BigDecimal drBalanceLocal;


    @Override
    public String toString() {
        return new java.util.StringJoiner(", ", "{", "}")
                .add("\"id\":\"" + id + "\"")
                .add("\"glCode\":\"" + glCode + "\"")
                .add("\"bookingDate\":\"" + bookingDate + "\"")
                .add("\"crMovement\":\"" + crMovement + "\"")
                .add("\"drMovement\":\"" + drMovement + "\"")
                .toString();
    }
}
