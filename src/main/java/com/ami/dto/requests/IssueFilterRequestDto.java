package com.ami.dto.requests;

import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;
import com.ami.enums.IssueStatus;
import com.ami.enums.SourceType;

import lombok.Data;

@Data
public class IssueFilterRequestDto {

    private String search;

    private IssueStatus status;

    private IssuePriority priority;

    private IssueCategory category;

    private SourceType sourceType;

    private Integer page = 0;

    private Integer size = 10;

    private String sortBy = "createdAt";

    private String direction = "desc";

}