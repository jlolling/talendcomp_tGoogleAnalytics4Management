package de.jlo.talendcomp.google.analytics.ga4.manage;

import com.google.analytics.data.v1beta.MetricMetadata;

public class MetricWrapper {
	
	private MetricMetadata metric;
	private long propertyId = 0;
	
	public MetricWrapper(MetricMetadata m) {
		if (m == null) {
			throw new IllegalArgumentException("MetricMetadata cannot be null");
		}
		this.metric = m;
	}
	
	public String getApiName() {
		return metric.getApiName();
	}

	public String getUiName() {
		return metric.getUiName();
	}
	
	public String getCategory() {
		return metric.getCategory();
	}
	
	public String getDescription() {
		return metric.getDescription();
	}
	
	public String getExpression() {
		return metric.getExpression();
	}
	
	public String getMeasurementUnit() {
		switch (metric.getTypeValue()) {
		case 0: return "UNSPECIFIED";
		case 1: return "INTEGER";
		case 2: return "FLOAT";
		case 4: return "SECONDS";
		case 5: return "MILLISECONDS";
		case 6: return "MINUTES";
		case 7: return "HOURS";
		case 8: return "STANDARD";
		case 9: return "CURRENCY";
		case 10: return "FEET";
		case 11: return "MILES";
		case 12: return "METERS";
		case 13: return "KILOMETERS";
		default: return "UNRECOGNIZED";
		}
	}
	
	public String getDeprecatedApiNames() {
		return Util.getListValues(metric.getDeprecatedApiNamesList());
	}

	public long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(long propertyId) {
		this.propertyId = propertyId;
	}
	
	public boolean isCustom() {
		return metric.getCustomDefinition();
	}
	
	@Override
	public String toString() {
		return getApiName() + " [" + getMeasurementUnit() + "]" + " (" + getUiName() + ")" + (propertyId > 0 ? " belongs to propertyId=" + propertyId : "");
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MetricWrapper) {
			return ((MetricWrapper) o).getPropertyId() == getPropertyId() && ((MetricWrapper) o).getApiName().equals(getApiName());
		}
		return false;
	}

}
