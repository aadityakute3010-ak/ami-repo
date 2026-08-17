package com.ami.dto.responses;

import java.util.List;

public class PagedUserResponseDto {

    private List<UserListResponseDto> users;

    private int currentPage;

    private int totalPages;

    private long totalElements;

    public PagedUserResponseDto() {
    }

    public List<UserListResponseDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserListResponseDto> users) {
        this.users = users;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}