package com.ami.service;

import com.ami.entity.PrepaidBalance;

public interface PrepaidLowBalanceNotificationService {

    void checkLowBalances();
    
    void checkAndNotify(PrepaidBalance balance);
}