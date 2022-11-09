package de.jlo.talendcomp.google.analytics.ga4.manage;

import com.google.analytics.admin.v1beta.CustomMetric;

public class CustomMetricWrapper {
	
	private CustomMetric metric;
	private long propertyId;
	
	public CustomMetricWrapper(CustomMetric m) {
		if (m == null) {
			throw new IllegalArgumentException("Custom metric cannot be null");
		}
		this.metric = m;
	}
	
	public String getApiName() {
		return metric.getName();
	}

	public String getUiName() {
		return metric.getDisplayName();
	}
 
	public String getDescription() {
		return metric.getDescription();
	}
	
	public boolean isCustomMetric() {
		return true;
	}

	public String getMeasurementUnit() {
		switch (metric.getMeasurementUnitValue()) {
		case 0: return "UNSPECIFIED";
		case 1: return "STANDARD";
		case 2: return "CURRENCY";
		case 3: return "FEET";
		case 4: return "METERS";
		case 5: return "KILOMETERS";
		case 6: return "MILES";
		case 7: return "MILLISECONDS";
		case 8: return "SECONDS";
		case 9: return "MINUTES";
		case 10: return "HOURS";
		default: return "UNRECOGNIZED";
		}
	}
	
	public String getScope() {
		switch (metric.getScopeValue()) {
		case 0: return "UNSPECIFIED";
		case 1: return "EVENT";
		default: return "UNRECOGNIZED";
		}
	}
	
	public String getParameterName() {
		return metric.getParameterName();
	}

	public long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(long propertyId) {
		this.propertyId = propertyId;
	}
	
}
