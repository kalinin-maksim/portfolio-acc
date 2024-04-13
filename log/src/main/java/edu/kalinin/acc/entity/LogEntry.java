package edu.kalinin.acc.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "acc_log")
public class LogEntry {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, length = 32)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String id;

    @Column(name = "message")
    private String message;

    @Column(name = "level")
    private String level;
}
