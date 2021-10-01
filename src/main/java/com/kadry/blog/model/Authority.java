package com.kadry.blog.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Authority {

    @Id
    @Column(length = 50)
    String name;

    @ManyToMany
    Set<User> users = new HashSet<>();

    public Authority(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Authority authority = (Authority) o;

        return Objects.equals(name, authority.name);
    }

    @Override
    public int hashCode() {
        return 280102488;
    }
}
