package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "mhn_permission_roles")
public class PermissionRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_type", unique = true)
    @Enumerated(EnumType.STRING)
    private UserType roleType = UserType.Doctor;

    @Lob
    private String permissions;
}
