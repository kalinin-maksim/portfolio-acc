package edu.kalinin.acc.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;
import static javax.persistence.FetchType.EAGER;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "acc_account_class")
public class AccountClass {
    @Id
    @Column(name = "id", length = 32)
    private String id;

    @Setter(AccessLevel.NONE)
    @ElementCollection (fetch = EAGER)
    @CollectionTable(name = "acc_account_search_field", joinColumns = @JoinColumn(name = "account_class_id"))
    @Column(name = "account_field", nullable = false, unique = true)
    private Set<AccountSearchField> accountSearchFields;

    @Enumerated(EnumType.STRING)
    @Column(name = "sign", length = 1)
    private Sign sign;

    @Data
    @Embeddable
    public static class AccountSearchField {
        @Enumerated(EnumType.STRING)
        private AccountField accountField;

        public AccountField getAccountField() {
            return accountField;
        }

    }

    public enum AccountField {
        flexcubeCustomerId, dealId
    }

    public enum Sign {
        D, C
    }
}