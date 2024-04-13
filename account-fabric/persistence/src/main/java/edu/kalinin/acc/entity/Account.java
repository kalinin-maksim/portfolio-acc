package edu.kalinin.acc.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "acc_account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @GenericGenerator(
            name = "account_seq",
            strategy = "edu.kalinin.acc.generator.AccountNumberGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "10")}
    )
    @Column(name = "id", unique = true, nullable = false)
    private String number;

    @Column(name = "class_code")
    private String classCode;

    @Column(name = "branch", length = 3)
    private String branch;

    @Column(name = "deal_id")
    private String dealId;

    @Column(name = "flexcube_customer_id")
    private String flexcubeCustomerId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "customer_type")
    private String customerType;

    @Column(name = "currency_code")
    private String currencyCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 8)
    private STATE state;

    @Column(name = "opening_date")
    @Temporal(TemporalType.DATE)
    private Date openingDate;

    @Column(name = "closing_date")
    @Temporal(TemporalType.DATE)
    private Date closingDate;

    @Column(name = "maker")
    private String maker;

    @Column(name = "checker")
    private String checker;

    @Column(name = "country")
    private String country;

    public enum STATE {
        OPEN,
        CLOSED
    }
}
