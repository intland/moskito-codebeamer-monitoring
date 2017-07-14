package net.anotheria.moskito.extensions.codebeamer;

import net.anotheria.anoplass.api.APIFactory;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoprise.metafactory.ServiceFactory;
import net.anotheria.moskito.webui.accumulators.api.AccumulatorAPI;

public class CBAccumulatorApiFactory implements APIFactory<AccumulatorAPI> , ServiceFactory<AccumulatorAPI> {
	
	private Integer sizeOfHistory = 4140;
	
	public CBAccumulatorApiFactory(Integer sizeOfHistory) {
		super();
		this.sizeOfHistory = sizeOfHistory;
	}

	@Override
	public AccumulatorAPI createAPI() {
		CBAccumulatorApiImpl cbAccumulatorApiImpl = new CBAccumulatorApiImpl();
		cbAccumulatorApiImpl.setMaxValues(sizeOfHistory);
		return cbAccumulatorApiImpl;
	}

	@Override
	public AccumulatorAPI create() {
		APIFinder.addAPIFactory(AccumulatorAPI.class, this);
		return APIFinder.findAPI(AccumulatorAPI.class);
	}
}