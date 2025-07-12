package com.iavtar.gfj_be.entity;

import com.iavtar.gfj_be.entity.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private RoleType name;

}
