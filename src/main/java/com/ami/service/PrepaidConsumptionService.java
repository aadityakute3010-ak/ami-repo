package com.ami.service;

import java.math.BigDecimal;

import com.ami.entity.Device;

public interface PrepaidConsumptionService {

	void deductConsumption(Device device, BigDecimal startReading, BigDecimal endReading);
}