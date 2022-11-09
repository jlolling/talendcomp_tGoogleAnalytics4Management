package de.jlo.talendcomp.google.analytics.ga4.manage;

import com.google.analytics.data.v1beta.DimensionMetadata;

public class DimensionWrapper {
	
	private DimensionMetadata dimension;
	
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
	
	public boolean isCustomDimension() {
		return dimension.getCustomDefinition();
	}
	
	public String getDeprecatedApiNames() {
		return Util.getListValues(dimension.getDeprecatedApiNamesList());
	}

}
