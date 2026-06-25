package com.ami.entity;

import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.StatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity 
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder  
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String userName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false) 
    private String password;

    private String phoneNo;

    private String address;

    private String state;

    private String city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RoleType role = RoleType.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusType status = StatusType.ACTIVE; 

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_source_assignments",joinColumns = @JoinColumn(name = "user_id")) 
    @Column(name = "source_type")
    @Builder.Default 
    private Set<SourceType> assignedSources = new HashSet<>(); 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer failedLoginAttempts = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean accountLocked = false;
    private LocalDateTime lockTime;
    
    @OneToMany(mappedBy = "assignedAdmin")
    @Builder.Default
    private Set<Device> managedDevices = new HashSet<>();
    
    @OneToMany(mappedBy = "assignedUser")
    @Builder.Default 
    private Set<Device> assignedDevices = new HashSet<>(); 

    
	public User(LocalDateTime createdAt, LocalDateTime updatedAt) {
		super(createdAt, updatedAt);
		// TODO Auto-generated constructor stub
	}

}