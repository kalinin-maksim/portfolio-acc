package edu.kalinin.acc.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Where(clause = "not deleted")
@Table(name = "acc_kafka_message", uniqueConstraints = { @UniqueConstraint(name = "uc_kafka_message_topic_msgId", columnNames = { "topic", "msg_id" }) })
public class Message {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, length = 32)
    private String id;

    @Column(name = "topic")
    private String topic;

    @Column(name = "msg_id")
    private String msgId;

    @Lob
    @Column(name = "body", length = 4096)
    private String body;

    @Column(name = "attempt_count", length = 100)
    private int attemptCount;

    @Column(name = "error_message", length = 4096)
    private String errorMessage;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdMoment;

}
