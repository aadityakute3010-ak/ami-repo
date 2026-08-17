package com.ami.service;

public interface InstallationNotificationService {

    void sendInstallationNotification(
            Long installationId,
            String installationNumber,
            String title,
            String message,
            String event,
            String performedBy);

}