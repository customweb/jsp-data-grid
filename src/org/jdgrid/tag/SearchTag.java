package org.jdgrid.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.jdgrid.Grid;


public class SearchTag extends AbstractTag  {

	private String columnName;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public void doTag() throws JspException, IOException {
		GridTag gridTag = (GridTag) findAncestorWithClass(this, GridTag.class);
		
		Grid<?> grid = gridTag.getGrid();
		
		StringBuilder builder = new StringBuilder();
		builder.append("<input type=\"text\" ").append(getAdditionalAttributes());
		
		builder.append(" name=\"");
		grid.getFieldFilterParameterName(this.getColumnName());
		builder.append("\" />");
		
		
		getJspContext().getOut().print(builder.toString());
	}
	
}
