package io.carlease.sales.adapter.out.serialization;

import com.google.gson.Gson;
import io.carlease.sales.application.port.out.ContractPersistencePort;
import io.carlease.sales.domain.VehicleContract;
import io.carlease.sales.domain.ContractNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContractSerializationAdapter implements ContractPersistencePort {

    private static final Logger logger = LoggerFactory.getLogger(ContractSerializationAdapter.class);

	@Override
	public void save(VehicleContract contract) {
		String serializedContract = new Gson().toJson(contract);
		logger.debug("Contract as JSON: " + serializedContract);
	}

	@Override
	public boolean exists(ContractNumber number) {
		// TODO implement
		return false;
	}

	@Override
	public VehicleContract with(ContractNumber number) {
		// TODO hier weitermachen mit: https://vaughnvernon.co/?p=942
		return null;
	}

}
