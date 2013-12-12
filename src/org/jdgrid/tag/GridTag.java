package org.jdgrid.tag;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import org.jdgrid.Grid;
import org.jdgrid.filter.OrderBy;


public class GridTag extends AbstractTag {

	private Grid<?> grid;

	@Override
	public void doTag() throws JspException, IOException {

		JspFragment body = getJspBody();
		if (body == null) {
			throw new IllegalStateException("You need to place the table tag into the grid tag.");
		}

		StringWriter value = new StringWriter();

		value.append("<div id=\"").append(this.getGrid().getGridId()).append("\"");
		value.append(this.getAdditionalAttributes());
		value.append(">");
		value.append("<form method=\"GET\" class=\"ajax-event-form\" action=\"");
		value.append(getGrid().getCurrentUrl().getPath()).append("\">");
		value.append(getHiddenFields());
		body.invoke(value);
		value.append("</form>");
		value.append("</div>");

		getJspContext().getOut().print(value);
	}

	private String getHiddenFields() {
		Grid<?> grid = getGrid();
		StringBuilder value = new StringBuilder();
		
		Map<String, String> hiddenFields = new HashMap<String, String>();
		int pageNumber = grid.getFilter().getPageNumber();
		
		hiddenFields.put(Grid.getPageNumberParameter(grid.getGridId()), new Integer(pageNumber).toString());
		
		for (OrderBy orderBy : grid.getFilter().getOrderBys()) {
			hiddenFields.put(getGrid().getOrderByParameterName(orderBy.getFieldName()), orderBy.getSorting());
		}

		for (Entry<String, String> entry : hiddenFields.entrySet()) {
			value.append("<input type=\"hidden\" name=\"").append(entry.getKey()).append("\"");
			value.append(" value=\"").append(entry.getValue()).append("\" />");
			
		}
		
		return value.toString();
	}

	public Grid<?> getGrid() {
		return grid;
	}

	public void setGrid(Grid<?> grid) {
		this.grid = grid;
	}
}
