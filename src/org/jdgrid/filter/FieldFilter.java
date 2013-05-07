package org.jdgrid.filter;

public class FieldFilter {

	private String value;
	
	private String fieldName;
	
	private String operator;
	
	public FieldFilter(String fieldName, String operator, String value) {
		this.fieldName = fieldName;
		this.operator = operator;
		this.value = value;
	}
	

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
}
