package org.jdgrid.filter;

public class OrderBy {

	private String fieldName;
	
	private boolean sortAscending = true;
	
	public OrderBy(String fieldName, boolean ascending) {
		this.fieldName = fieldName;
		this.sortAscending = ascending;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public boolean isSortAscending() {
		return sortAscending;
	}

	public void setSortAscending(boolean sortAscending) {
		this.sortAscending = sortAscending;
	}
	
	public String getSorting() {
		if (sortAscending) {
			return "ASC";
		}
		else return "DESC";
	}
}
