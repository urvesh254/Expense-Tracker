package com.ukpatel.expense.tracker.auth.entity;

import com.ukpatel.expense.tracker.common.base.entity.TimestampedBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user_dtl")
public class UserDtl extends TimestampedBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userDtlId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserMst userMst;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "dob")
    private Date dob;

}
