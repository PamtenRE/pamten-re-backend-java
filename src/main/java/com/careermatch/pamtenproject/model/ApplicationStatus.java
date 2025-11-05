package com.careermatch.pamtenproject.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ApplicationStatus", schema = "db_owner")
@Data
public class ApplicationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long statusId;

    @Column(name = "status_name")
    private String statusName;

    @Column(name = "description")
    private String description;
}
