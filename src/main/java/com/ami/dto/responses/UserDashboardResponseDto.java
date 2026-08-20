package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor 
public class UserDashboardResponseDto {

    private long totalUsers;
    private double totalUsersPercentage;

    private long activeUsers;
    private double activeUsersPercentage;

    private long inactiveUsers;
    private double inactiveUsersPercentage;

    private long admins;
    private double adminsPercentage;

    private long engineers;
    private double engineersPercentage;

    private long normalUsers;
    private double normalUsersPercentage;

    private long superAdmins;
    private double superAdminsPercentage;

    private long assignedUsers;
    private double assignedUsersPercentage;
}