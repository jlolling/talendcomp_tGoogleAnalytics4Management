package de.jlo.talendcomp.google.analytics.ga4.manage;

import com.google.analytics.admin.v1beta.CustomDimension;

public class CustomDimensionWrapper {
	
	private CustomDimension dimension;
	private long propertyId;
	
	public CustomDimensionWrapper(CustomDimension d) {
		if (d == null) {
			throw new IllegalArgumentException("Custom dimension cannot be null");
		}
		this.dimension = d;
	}
	
	public CustomDimension getCustomDimension() {
		return dimension;
	}

	public String getApiName() {
		return dimension.getName();
	}
	
	public String getUiName() {
		return dimension.getDisplayName();
	}
	
	public String getDescription() {
		return dimension.getDescription();
	}
	
	public boolean isCustomDimension() {
		return true;
	}
	
	public String getScope() {
		switch (dimension.getScopeValue()) {
		case 0: return "UNSPECIFIED";
		case 1: return "EVENT";
		case 2: return "USER";
		default: return "UNRECOGNIZED";
		}
	}

	public long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(long propertyId) {
		this.propertyId = propertyId;
	}

}
