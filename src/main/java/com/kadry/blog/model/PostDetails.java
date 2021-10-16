package com.kadry.blog.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PostDetails {

    @Id
    @Column(name = "post_details_id")
    private long id;

    @OneToOne
    @MapsId
    @ToString.Exclude
    private Post post;

    @NotNull
    @NotEmpty
    private String body;

    @Column(name = "up_vote")
    private int upVote = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PostDetails that = (PostDetails) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
