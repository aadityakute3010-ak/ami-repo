package com.ami.mapper;

import java.time.LocalDateTime;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.ami.entity.ArchivedDeviceOperation;
import com.ami.entity.DeviceOperation;
import com.ami.entity.User;

@Component
public class DeviceOperationMapper {
	
	 public DeviceOperation mapToOperation(
	            ArchivedDeviceOperation archivedOperation) {

	        DeviceOperation operation =
	                new DeviceOperation();

	        BeanUtils.copyProperties(
	                archivedOperation,
	                operation,
	                "id",
	                "originalOperationId",
	                "archivedAt",
	                "archivedBy",
	                "archiveReason");

	        operation.setId(null);

	        return operation;
	    }
	 

	    public ArchivedDeviceOperation mapToArchivedOperation(
	            DeviceOperation operation,
	            User archivedBy,
	            String archiveReason) {

	        ArchivedDeviceOperation archivedOperation =
	                new ArchivedDeviceOperation();

	        BeanUtils.copyProperties(
	                operation,
	                archivedOperation);

	        archivedOperation.setOriginalOperationId(
	                operation.getId());

	        archivedOperation.setArchivedAt(
	                LocalDateTime.now());

	        archivedOperation.setArchivedBy(
	                archivedBy);

	        archivedOperation.setArchiveReason(
	                archiveReason);

	        return archivedOperation;
	    }

}
