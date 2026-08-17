package com.ami.dto.responses;

import com.ami.enums.RoleType;
import com.ami.enums.SourceType;

import java.util.Set;

public class CreationOptionsResponse {

    private Set<RoleType> allowedRoles;

    private Set<SourceType> allowedSources;

    public CreationOptionsResponse() {
    }

    public Set<RoleType> getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(
            Set<RoleType> allowedRoles
    ) {
        this.allowedRoles = allowedRoles;
    }

    public Set<SourceType> getAllowedSources() {
        return allowedSources;
    }

    public void setAllowedSources(
            Set<SourceType> allowedSources
    ) {
        this.allowedSources = allowedSources;
    }
}