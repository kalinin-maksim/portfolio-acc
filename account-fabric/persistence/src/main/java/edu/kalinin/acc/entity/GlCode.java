package edu.kalinin.acc.entity;


import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;

@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Cacheable
@org.hibernate.annotations.Immutable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "acc_gl_code")
public class GlCode {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, length = 32)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String id;

    @ToString.Include
    @EqualsAndHashCode.Include
    private String name;

    @ManyToOne(fetch = EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_gl_code_parent_id__gl_code_id"))
    private GlCode parent;

    @OneToMany(mappedBy = "parent", fetch = EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GlCode> children = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        var glCode = (GlCode) o;
        return id != null && Objects.equals(id, glCode.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
