package com.ami.dto.requests;

import com.ami.enums.DeviceStatus;
import com.ami.enums.ProtocolType;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeviceRequestDto {

    @NotBlank(message = "Device name is required")
    @Size(min = 3,max = 50)
    private String deviceName;

    @NotNull(message = "Technology type is required")
    private TechnologyType technologyType;  

    @NotNull(message = "Source type is required")
    private SourceType sourceType;

    @NotBlank(message = "MAC Address is required")
    @Pattern(
        regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$",
        message = "Invalid MAC Address format"
    ) 
    private String macAddress;

    @NotBlank(message = "Serial number is required")
    private String serialNumber;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    @NotNull(message = "Sample count is required")
    @Min(value = 1)
    @Max(value = 100)
    private Integer sampleCount;

    @NotBlank(message = "Wakeup time is required")
    private String wakeupTime;

    @NotBlank(message = "Firmware version is required")
    private String firmwareVersion;

    @NotNull(message = "Protocol type is required")
    private ProtocolType protocolType;

    @NotNull(message = "OTA Update flag required")
    private Boolean otaUpdatesEnabled;

    @NotNull(message = "Status is required")
    private DeviceStatus status; 
 
    /*
        ADMIN ASSIGNED TO DEVICE
     */
    @NotNull(message = "Assigned admin is required")
    private Long assignedAdminId;
}