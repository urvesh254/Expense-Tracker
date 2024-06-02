package com.ukpatel.expense.tracker.auth.entity;


import com.ukpatel.expense.tracker.common.base.entity.TimestampedBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user_mst")
public class UserMst extends TimestampedBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(mappedBy = "userMst")
    private UserDtl userDtl;
}
