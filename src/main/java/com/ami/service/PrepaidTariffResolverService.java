package com.ami.service;

import com.ami.entity.Device;
import com.ami.entity.Tariff;

public interface PrepaidTariffResolverService {

	Tariff resolveTariffForPrepaidDevice(Device device);
}