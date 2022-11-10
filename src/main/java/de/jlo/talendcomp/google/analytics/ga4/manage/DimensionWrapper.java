package de.jlo.talendcomp.google.analytics.ga4.manage;

import com.google.analytics.data.v1beta.DimensionMetadata;

public class DimensionWrapper {
	
	private DimensionMetadata dimension;
	private long propertyId = 0;
	
	public DimensionWrapper(DimensionMetadata d) {
		if (d == null) {
			throw new IllegalArgumentException("Dimension cannot be null");
		}
		this.dimension = d;
	}

	public String getApiName() {
		return dimension.getApiName();
	}
	
	public String getUiName() {
		return dimension.getUiName();
	}
	
	public String getCategory() {
		return dimension.getCategory();
	}
	
	public String getDescription() {
		return dimension.getDescription();
	}
	
	public boolean isCustom() {
		return dimension.getCustomDefinition();
	}
	
	public String getDeprecatedApiNames() {
		return Util.getListValues(dimension.getDeprecatedApiNamesList());
	}

	public long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(long propertyId) {
		this.propertyId = propertyId;
	}

	@Override
	public String toString() {
		return getApiName() + " (" + getUiName() + ")" + (propertyId > 0 ? " belongs to propertyId=" + propertyId : "");
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DimensionWrapper) {
			return ((DimensionWrapper) o).getPropertyId() == getPropertyId() && ((DimensionWrapper) o).getApiName().equals(getApiName());
		}
		return false;
	}

}
