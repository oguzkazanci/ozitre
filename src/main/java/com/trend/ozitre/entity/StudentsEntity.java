package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Students")
@Data
public class StudentsEntity extends BaseEntity {

    @Id
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_gen", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "username", length = 20)
    private String username;

    @Column(name = "mail", length = 100)
    private String mail;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "registry_state", length = 2)
    private Integer regState;

    @Column(name = "registry_type", length = 1)
    private Integer regType;

    @Column(name = "name", length = 40)
    private String name;

    @Column(name = "surname", length = 40)
    private String surname;

    @Column(name = "address", length = 150)
    private String address;

    @Column(name = "school", length = 150)
    private String school;

    @Column(name = "grade_id", length = 10)
    private Long gradeId;

    @Column(name = "parent", length = 50)
    private String parent;

    @Column(name = "par_phone_number", length = 20)
    private String parentPhoNo;

    @OneToMany(mappedBy = "student")
    private Set<EnrollmentEntity> enrollments = new HashSet<>();

    @Column(name = "explanation", length = 200)
    private String explanation;

    @Column(name = "package_id", length = 5)
    private Long packageId;

    @Column(name = "total_price", length = 12)
    private Integer totalPrice;

    @Column(name = "start_month", length = 12)
    private Integer startMonth;

    @Column(name = "installment", length = 2)
    private Integer installment;

    @Column(name = "company_id")
    private Long companyId;
}
