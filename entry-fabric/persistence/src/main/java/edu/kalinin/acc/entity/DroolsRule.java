package edu.kalinin.acc.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "acc_drools_rule")
public class DroolsRule {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, length = 32)
    private String id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "body", length = 4096)
    private String body;

    @Column(name = "pattern", length = 4096)
    private String pattern;

    @Column(name = "result", length = 4096)
    private String result;

    @Column(name = "deleted")
    private Boolean deleted;

}
