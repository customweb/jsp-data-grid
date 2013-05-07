package org.jdgrid.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.jdgrid.Grid;
import org.jdgrid.util.UrlEncodedQueryString;

public class FilterTag extends AbstractTag {

	private String defaultOperator = "";
	private String fieldName = "";
	private boolean showOperator = false;
	private Grid<?> grid;
	
	public void doTag() throws JspException, IOException {
		StringBuilder builder = new StringBuilder();
		builder.append("<input ").append(getAdditionalAttributes()).append(" ");
		
		String inputFieldName = getGrid().getFieldFilterParameterName(this.getFieldName());
		builder.append("name=\"").append(inputFieldName).append("\" ");
		builder.append("value=\"").append(getCurrentFilterValue()).append("\" ");
		
		builder.append(" /> ");
		
		if (showOperator == true || !getDefaultOperator().isEmpty()) {
			builder.append("<input ").append(getAdditionalAttributes()).append(" ");
			if (showOperator == false) {
				builder.append(" type=\"hidden\" ");
			}
			String operatorName = grid.getFilterOperatorName(fieldName);
			builder.append("name=\"").append(operatorName).append("\" ");
			builder.append("value=\"").append(getOperatorValue()).append("\" ");
			builder.append(" />");
		}
		
		getJspContext().getOut().print(builder.toString());
	}

	public String getDefaultOperator() {
		return defaultOperator;
	}

	public void setDefaultOperator(String defaultOperator) {
		this.defaultOperator = defaultOperator;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	private Grid<?> getGrid() {
		if (grid == null) {
			GridTag gridTag = (GridTag) findAncestorWithClass(this, GridTag.class);
			grid = gridTag.getGrid();
		}
		return grid;
	}
	
	protected String getCurrentFilterValue() {
		Grid<?> grid = getGrid();
		String inputFieldName = grid.getFieldFilterParameterName(this.getFieldName());
		UrlEncodedQueryString query = UrlEncodedQueryString.parse(grid.getCurrentUrl().getQuery());
		if (query.contains(inputFieldName)) {
			return query.get(inputFieldName);
		}
		return "";
	}
	
	protected String getOperatorValue() {
		Grid<?> grid = getGrid();
		String operatorName = grid.getFilterOperatorName(fieldName);		
		UrlEncodedQueryString query = UrlEncodedQueryString.parse(grid.getCurrentUrl().getQuery());
		if (query.contains(operatorName)) {
			return query.get(operatorName);
		}
		return getDefaultOperator();
	}

	public boolean isShowOperator() {
		return showOperator;
	}

	public void setShowOperator(boolean showOperator) {
		this.showOperator = showOperator;
	}
	
}
