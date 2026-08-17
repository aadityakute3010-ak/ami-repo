package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.DeviceStatus;
import com.ami.enums.ProtocolType;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "archived_devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ArchivedDevice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Original Device Id
     */
    private Long originalDeviceId;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private String deviceName;

    @Enumerated(EnumType.STRING)
    private TechnologyType TechnologyType;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    private String macAddress;

    private String serialNumber;

    private String timezone;

    private Integer sampleCount;

    private String wakeupTime;

    private String firmwareVersion;

    @Enumerated(EnumType.STRING)
    private ProtocolType protocolType;

    private String imei;

    private String meterNumber;

    private String location;

    private String zone;

    private LocalDateTime installationDate;

    private Double latitude;

    private Double longitude;

    private Boolean otaUpdatesEnabled;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    private Boolean active;

    private Boolean online;

    private LocalDateTime lastSyncTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id")
    private User assignedAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    /*
     * Archive Details
     */
    private LocalDateTime archivedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archived_by_id")
    private User archivedBy;

    private String archiveReason;
}