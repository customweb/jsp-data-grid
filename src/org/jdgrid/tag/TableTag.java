package org.jdgrid.tag;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import org.jdgrid.Grid;


public class TableTag extends AbstractTag {

	private String var;
	private boolean colGroupProcessing = false;
	private boolean headerRowProcessing = false;
	private boolean filterRowProcessing = false;
	private boolean showFilterRow = false;

	public void doTag() throws JspException, IOException {

		JspFragment body = getJspBody();
		if (body == null) {
			throw new IllegalStateException("You need to place column tags inside the table tag.");
		}

		StringWriter value = new StringWriter();

		value.append("<table ");

		value.append(this.getAdditionalAttributes());

		value.append(">");
		value.append(this.getColGroup());
		value.append(this.getTableHeader());
		value.append(this.getTableBody());
		value.append("</table>");

		getJspContext().getOut().print(value);
	}
	
	public String getColGroup() throws JspException, IOException {
		StringWriter builder = new StringWriter();
		builder.append("<colgroup>");
		
		JspFragment body = getJspBody();
		
		this.colGroupProcessing = true;
		body.invoke(builder);
		this.colGroupProcessing = false;
		
		builder.append("</colgroup>");
		
		return builder.toString();
	}

	public String getTableBody() throws JspException, IOException {
		JspFragment body = getJspBody();

		StringWriter builder = new StringWriter();
		builder.append("<tbody>");
		List<?> list = getGrid().getResultList();

		for (int rowNumber = 0; rowNumber < list.size(); rowNumber++) {
			Object item = list.get(rowNumber);
			getJspContext().setAttribute(getVar(), item);
			getJspContext().setAttribute("row-number", rowNumber);

			builder.append("<tr>");
			body.invoke(builder);
			builder.append("</tr>");
		}

		builder.append("</tbody>");

		return builder.toString();
	}

	public synchronized String getTableHeader() throws JspException, IOException {
		StringWriter builder = new StringWriter();
		builder.append("<thead>");

		JspFragment body = getJspBody();

		this.headerRowProcessing = true;
		builder.append("<tr class=\"grid-header-row\">");
		body.invoke(builder);
		builder.append("</tr>");
		this.headerRowProcessing = false;

		if (isShowFilterRow()) {
			this.filterRowProcessing = true;
			builder.append("<tr class=\"grid-filter-row\">");
			body.invoke(builder);
			builder.append("</tr>");
			this.filterRowProcessing = false;
		}

		builder.append("</thead>");
		return builder.toString();
	}

	private Grid<?> getGrid() {
		GridTag gridTag = (GridTag) findAncestorWithClass(this, GridTag.class);
		return gridTag.getGrid();
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}
	
	public boolean isColGroupProcessing() {
		return colGroupProcessing;
	}

	public boolean isHeaderRowProcessing() {
		return headerRowProcessing;
	}

	public boolean isFilterRowProcessing() {
		return filterRowProcessing;
	}

	public boolean isShowFilterRow() {
		return showFilterRow;
	}

	public void setShowFilterRow(boolean showFilterRow) {
		this.showFilterRow = showFilterRow;
	}

}
