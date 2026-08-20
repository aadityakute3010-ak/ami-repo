package com.ami.service;

import com.ami.dto.responses.PrepaidUnitCalculationResponseDto;
import com.ami.entity.Device;
import com.ami.entity.PrepaidRechargePlan;
import com.ami.entity.Tariff;

public interface PrepaidUnitCalculationService {

	PrepaidUnitCalculationResponseDto calculateUnits(Device device, PrepaidRechargePlan plan, Tariff tariff);
}