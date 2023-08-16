package org.example.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "registration_date")
    private Timestamp registrationDate;

    @Column(name = "last_login_date")
    private Timestamp lastLoginDate;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "role")
    private String role;

    // 생성자, getter, setter, 기타 메서드들
}
