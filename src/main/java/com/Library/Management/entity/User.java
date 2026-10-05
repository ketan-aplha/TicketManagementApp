package com.Library.Management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email_organisation", columnNames = {"email", "organisation_id"}),
                @UniqueConstraint(name = "uk_users_username_organisation", columnNames = {"username", "organisation_id"})
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString
public class User extends TenantAware {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    private List<Ticket> createdTickets;

    @OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY)
    private List<Ticket> assignedTickets;
}
