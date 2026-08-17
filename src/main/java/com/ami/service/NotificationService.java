package com.ami.service;

public interface NotificationService {

    void issueAssigned(
            Long issueId,
            String engineerName);

    void issueAccepted(
            Long issueId);

    void issueRejected(
            Long issueId,
            String reason);

    void issueEscalated(
            Long issueId);

    void issueResolved(
            Long issueId);

    void slaBreached(
            Long issueId);

    void fieldVisitCreated(
            Long issueId);
    
    void workStarted(
            Long issueId);
    
    void alertCreated(
            Long alertId,
            String alertName,
            String severity,
            String message);
    
    void materialAdded(
            Long issueId,
            String materialName);
    
    void progressUpdated(
            Long issueId,
            Integer progress);
    
    
    
    void issueClosed(
            Long issueId);
    
    
}