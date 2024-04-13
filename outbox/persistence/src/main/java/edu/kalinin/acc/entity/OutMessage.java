package edu.kalinin.acc.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import edu.kalinin.acc.selector.Channel;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "acc_out_message")
public class OutMessage {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, length = 32)
    private String id;

    @Column(name = "process_id")
    private String processId;

    @Column(name = "message")
    private String message;

    @Column(name = "response")
    private String response;

    @Column(name = "channel")
    @Enumerated(EnumType.STRING)
    private Channel channel;

    @Column(name = "address")
    private String address;

    @Column(name = "level")
    private String level;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdMoment;

}
