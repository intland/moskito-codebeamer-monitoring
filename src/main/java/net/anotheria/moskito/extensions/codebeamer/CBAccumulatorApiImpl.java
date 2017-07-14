package net.anotheria.moskito.extensions.codebeamer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.moskito.webui.accumulators.api.AccumulatedSingleGraphAO;
import net.anotheria.moskito.webui.accumulators.api.AccumulatedValueAO;
import net.anotheria.moskito.webui.accumulators.api.AccumulatorAO;
import net.anotheria.moskito.webui.accumulators.api.AccumulatorAPIImpl;
import net.anotheria.moskito.webui.accumulators.api.MultilineChartAO;
import net.anotheria.moskito.webui.accumulators.bean.AccumulatedValuesBean;
import net.anotheria.util.NumberUtils;
import net.anotheria.util.sorter.DummySortType;
import net.anotheria.util.sorter.SortType;
import net.anotheria.util.sorter.StaticQuickSorter;

public class CBAccumulatorApiImpl extends AccumulatorAPIImpl {

	private int maxValues = 200;
	
	/**
	 * This constant is used to put different timestamp in context.
	 */
	private static final long MINUTE = 1000L*60;

	/**
	 * Object for caching of the sort types.
	 */
	private static final SortType SORT_TYPE = new DummySortType();
	
	@Override
	public MultilineChartAO getAccumulatorGraphData(List<String> ids, boolean normalized) throws APIException {
		int normalizeBase = 100;
		//TODO actually this limit is hardcoded, we should make it dynamic.
		int maxValues = this.maxValues;

		int numberOfIds = ids.size();
		if (numberOfIds == 0) {
			throw new APIException("No accumulators selected");
		}

		List<AccumulatedSingleGraphAO> singleGraphDataBeans = new ArrayList<>(numberOfIds);

		//prepare values
		Map<Long, AccumulatedValuesBean> values = new HashMap<>(numberOfIds);
		List<String> accNames = new ArrayList<>(numberOfIds);

		for (String id : ids){
			AccumulatorAO acc = getAccumulator(id);
			AccumulatedSingleGraphAO singleGraphDataBean = getAccumulatorGraphData(id);
			singleGraphDataBeans.add(singleGraphDataBean);

			accNames.add(acc.getName());
			List<AccumulatedValueAO> accValues = acc.getValues();
			for (AccumulatedValueAO v : accValues){
				long timestamp = v.getNumericTimestamp();
				timestamp = timestamp /  MINUTE * MINUTE;
				AccumulatedValuesBean bean = values.get(timestamp);
				if (bean==null){
					bean = new AccumulatedValuesBean(timestamp);
					values.put(timestamp, bean);
				}
				bean.setValue(acc.getName(), v.getFirstValue());

			}
		}
		List<AccumulatedValuesBean> valuesList = StaticQuickSorter.sort(values.values(), SORT_TYPE);

		//now check if the data is complete
		//Stores last known values to allow filling in of missing values (combining 1m and 5m values)
		Map<String, String> lastValue = new HashMap<>(accNames.size());

		//filling last (or first) values.
		for (String accName : accNames){
			//first put 'some' initial value.
			lastValue.put(accName, "0");
			//now search for first non-null value
			for(AccumulatedValuesBean accValueBean : valuesList){
				String aValue = accValueBean.getValue(accName);
				if (aValue!=null){
					lastValue.put(accName, aValue);
					break;
				}
			}
		}

		for(AccumulatedValuesBean accValueBean : valuesList){
			for (String accName : accNames){
				String value = accValueBean.getValue(accName);
				if (value==null){
					accValueBean.setValue(accName, lastValue.get(accName));
				}else{
					lastValue.put(accName, value);
				}
			}
		}

		if (normalized){
			normalize(valuesList, accNames, normalizeBase);
		}

		//now create final data
		List<AccumulatedValueAO> dataBeans = new ArrayList<>(valuesList.size());
		for(AccumulatedValuesBean avb : valuesList){
			AccumulatedValueAO bean = new AccumulatedValueAO(avb.getTime());
			bean.setIsoTimestamp(NumberUtils.makeISO8601TimestampString(avb.getTimestamp()));
			bean.setNumericTimestamp(avb.getTimestamp());

			for (String accName : accNames){
				bean.addValue(avb.getValue(accName));
			}
			dataBeans.add(bean);
		}

		//generally its not always a good idea to use subList, but since that list isn't reused,
		//as in subList or subList of subList, its ok.
		if (dataBeans.size()>maxValues) {
			List<AccumulatedValueAO> shorterList = new ArrayList<AccumulatedValueAO>(dataBeans.subList(dataBeans.size() - maxValues, dataBeans.size()));
			dataBeans = shorterList;
		}

		MultilineChartAO ret = new MultilineChartAO();
		ret.setData(dataBeans);
		ret.setNames(accNames);
		ret.setSingleGraphAOs(singleGraphDataBeans);
		return ret;
	}
	
	static void normalize(List<AccumulatedValuesBean> values, List<String> names, int limit){
		for (String name : names){
			//System.out.println("normalizing "+name);
			ArrayList<Float> valueCopy = new ArrayList<Float>(values.size());
			//step1 transform everything to float
			float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
			for (AccumulatedValuesBean v : values){
				float val = Float.parseFloat(v.getValue(name));
				if (val>max)
					max = val;
				if (val<min)
					min = val;
				valueCopy.add(val);
			}
			//System.out.println("1: "+valueCopy);
			float range = max - min;
			float multiplier = limit / range;
			//System.out.println("range "+range+", multiplier "+multiplier);

			//step2 recalculate
			for (int i=0; i<values.size(); i++){
				float newValue = (valueCopy.get(i)-min)*multiplier;
				values.get(i).setValue(name, String.valueOf(newValue));
			}
		}
	}

	/**
	 * @param maxValues the maxValues to set
	 */
	public void setMaxValues(int maxValues) {
		this.maxValues = maxValues;
	}
}
