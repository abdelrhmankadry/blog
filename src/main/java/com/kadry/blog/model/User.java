package com.kadry.blog.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @NotNull
    @Column(nullable = false)
    String username;

    @NotNull
    @Column(name = "password_hash", nullable = false)
    String password;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(unique = true)
    String email;

    @Column(nullable = false)
    boolean activated = false;

    @Column(name = "activation_key", length = 20)
    String activationKey;

    @Column(name = "reset_key", length = 20)
    String restKey;

    @Column(name = "reset_date")
    Instant resetDate = null;

    @ManyToMany
    @JoinTable(
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")}
    )
    @BatchSize(size = 20)
    Set<Authority> authorities = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;

        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return 562048007;
    }
}
