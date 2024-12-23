package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Users")
@Data
public class UserEntity {

    @Id
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_gen", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "username", length = 20, nullable = false)
    private String username;
    @Column(name = "user_mail", length = 100)
    private String userMail;
    @Column(name = "firstname", length = 40, nullable = false)
    private String firstName;
    @Column(name = "lastname", length = 40, nullable = false)
    private String lastName;
    @Column(name = "user_address", length = 150)
    private String userAddress;
    @Column(name = "user_phone_number", length = 20)
    private String userPhoneNumber;
    @Column(name = "user_password", nullable = false)
    private String password;
    @Column(name = "user_last_password")
    private String userLastPassword;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(  name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(  name = "user_company",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id"))
    private Set<CompanyEntity> companies = new HashSet<>();

}
