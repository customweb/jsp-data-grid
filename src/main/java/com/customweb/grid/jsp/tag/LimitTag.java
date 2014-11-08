package com.customweb.grid.jsp.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import com.customweb.grid.Grid;


public class LimitTag extends AbstractTag  {

	private String steps;

	public String getSteps() {
		return steps;
	}

	public void setSteps(String steps) {
		this.steps = steps;
	}
	
	public void doTag() throws JspException, IOException {
		GridTag gridTag = (GridTag) findAncestorWithClass(this, GridTag.class);
		
		Grid<?> grid = gridTag.getGrid();
		
		// Append class
		String classes = "";
		if (this.getDynamicAttribute().containsKey("class")) {
			classes = (String)this.getDynamicAttribute().get("class");
		}
		classes = "ajax-event " + classes;
		this.setDynamicAttribute("", "class", classes);
		
		StringBuilder builder = new StringBuilder();
		builder.append("<select name=\"").append(grid.getResultsPerPageParameterName()).append("\" ").append(getAdditionalAttributes()).append(">");
		
		String[] itemsStrings = getSteps().split(",");
		int numberOfResultsPerPage = grid.getFilter().getResultsPerPage();
		for (String item : itemsStrings) {
			int itemInInt = Integer.valueOf(item);
			
			builder.append("<option value=\"").append(item).append("\"");
			if (numberOfResultsPerPage == itemInInt) {
				builder.append(" selected=\"selected\"");
			}
			builder.append(">").append(item).append("</option>");
		}
		
		builder.append("</select>");
		getJspContext().getOut().print(builder.toString());
	}
	
}
