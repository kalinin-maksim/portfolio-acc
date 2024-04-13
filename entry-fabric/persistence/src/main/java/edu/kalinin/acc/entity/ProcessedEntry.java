package edu.kalinin.acc.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "acc_processed_entry")
public class ProcessedEntry {
    @Id
    @Column(name = "id", nullable = false, length = 32)
    private String id;

    @Column(name = "date")
    private Timestamp date;
}