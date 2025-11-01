package com.CwY.WebAssignment.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User student;

    @Lob
    private String content;

    private String fileName;

    @Lob
    private byte[] fileData;

    private LocalDateTime submittedAt;

    private Double grade;

    @Column(length = 2000)
    private String feedback;

    private LocalDateTime gradedAt;
}
