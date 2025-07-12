package com.iavtar.gfj_be.entity;

import com.iavtar.gfj_be.entity.enums.DashboardTypes;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dashboard")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Dashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private DashboardTypes name;

}
