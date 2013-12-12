package org.jdgrid.tag;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import org.jdgrid.Grid;
import org.jdgrid.util.Html;
import org.jdgrid.util.Property;
import org.jdgrid.util.UrlEncodedQueryString;

public class ColumnTag extends AbstractTag {

	private boolean filterable;
	private boolean sortable;
	private String fieldName;
	private String title;
	private String labelForFalse = "False";
	private String labelForTrue = "True";
	private String colWidth;
	private String align = "left";

	public void doTag() throws JspException, IOException {
		TableTag table = (TableTag) findAncestorWithClass(this, TableTag.class);
		try {

			if (table.isColGroupProcessing()) {
				getJspContext().getOut().print(getColContent());
			} else if (table.isHeaderRowProcessing()) {
				getJspContext().getOut().print(getCellHeader());
			} else if (table.isFilterRowProcessing()) {
				getJspContext().getOut().print(getFilterContent());
			} else {
				getJspContext().getOut().print(getCellContent());
			}
		} catch (Exception e) {
			JspException exception = new JspException();
			exception.initCause(e);
			throw exception;
		}
	}

	private String getCellHeader() {
		StringBuilder builder = new StringBuilder();

		builder.append("<th ").append(getAdditionalAttributes()).append(">");

		if (this.getTitle() != null && !this.getTitle().isEmpty()) {
			builder.append(this.getTitle());
		} else if (this.getFieldName() != null && !this.getFieldName().isEmpty()) {
			builder.append(this.getFieldName());
		}

		if (isSortable()) {
			builder.append(getSortableControls());
		}

		builder.append("</th>");

		return builder.toString();
	}

	private String getFilterContent() throws SecurityException, NoSuchFieldException {
		StringBuilder builder = new StringBuilder();

		builder.append("<th ").append(getAdditionalAttributes()).append(">");

		if (isFilterable()) {
			builder.append(getFilterableControls());
		}

		builder.append("</th>");

		return builder.toString();
	}

	private StringWriter getCellContent() throws JspException, IOException {
		StringWriter value = new StringWriter();
		
		Map<String, Object> attributes = new HashMap<String, Object>(getDynamicAttribute());
		if (attributes.containsKey("style")) {
			attributes.put("style", "text-align: " + getAlign() + "; " + attributes.get("style"));
		} else {
			attributes.put("style", "text-align: " + getAlign() + ";");
		}
		value.append("<td ").append(getAdditionalAttributes(attributes)).append(">");

		JspFragment body = getJspBody();
		if (body == null) {
			TableTag table = (TableTag) findAncestorWithClass(this, TableTag.class);
			String var = table.getVar();
			Object item = getJspContext().getAttribute(var);

			value.append(getItemData(item));

		} else {
			body.invoke(value);
		}
		value.append("</td>");
		return value;
	}

	private String getSortableControls() {
		Grid<?> grid = getGrid();
		StringBuilder builder = new StringBuilder();
		String orderingClass = "no-sorting";
		String order = grid.getFilter().getOrderBy(this.getFieldName());
		if (order != null && order.equalsIgnoreCase("ASC")) {
			orderingClass = "ascending-sorting";
		} else if (order != null && order.equalsIgnoreCase("DESC")) {
			orderingClass = "descending-sorting";
		}

		builder.append("<div class=\"sorting ").append(orderingClass).append("\">");
		builder.append("<a class=\"ajax-event\" href=\"").append(grid.getSortingUrl(getFieldName(), true, false))
				.append("\"><span class=\"ascending\">&nbsp;</span></a>");
		builder.append("<a class=\"ajax-event\" href=\"").append(grid.getSortingUrl(getFieldName(), false, true))
				.append("\"><span class=\"descending\">&nbsp;</span></a>");
		builder.append("<a class=\"ajax-event\" href=\"").append(grid.getSortingUrl(getFieldName(), false, false))
				.append("\"><span class=\"reset-sorting\">&nbsp;</span></a>");
		builder.append("</div>");

		return builder.toString();
	}

	private Object getFilterableControls() throws SecurityException, NoSuchFieldException {
		Grid<?> grid = getGrid();
		StringBuilder builder = new StringBuilder();

		String inputFieldName = grid.getFieldFilterParameterName(this.getFieldName());

		Class<?> columnType = Property.getPropertyDataType(grid.getDomainClass(), this.getFieldName());

		if (boolean.class.isAssignableFrom(columnType)) {
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("", "");
			values.put("true", getLabelForTrue());
			values.put("false", getLabelForFalse());

			String dropDown = Html.getDropDown(inputFieldName, values, getCurrentFilterValue(), " class=\"ajax-event form-control\"");

			builder.append(dropDown);
		} else {
			builder.append("<div><input class=\"form-control\" type=\"text\" name=\"").append(inputFieldName).append("\" ");
			builder.append("value=\"");
			UrlEncodedQueryString query = UrlEncodedQueryString.parse(grid.getCurrentUrl().getQuery());
			if (query.contains(inputFieldName)) {
				builder.append(query.get(inputFieldName));
			}

			builder.append("\" /></div>");
		}

		// TODO: Add operator dropdown
		return builder.toString();
	}

	public boolean isFilterable() {
		return filterable;
	}

	public void setFilterable(boolean filterable) {
		this.filterable = filterable;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String field) {
		this.fieldName = field;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private Grid<?> getGrid() {
		GridTag gridTag = (GridTag) findAncestorWithClass(this, GridTag.class);
		return gridTag.getGrid();
	}

	private String getItemData(Object item) {
		String[] parts = getFieldName().split("\\.");
		Object data = item;

		for (String currentProperty : parts) {
			data = getProperty(data, currentProperty);
		}
		
		if (data == null) {
			return "";
		}

		return data.toString();
	}

	private Object getProperty(Object item, String property) {
		if (item == null) {
			return "";
		}
		
		char first = Character.toUpperCase(property.charAt(0));
		String methodName = "get" + first + property.substring(1);
		try {
			Method method = Property.getMethodByMethodName(item.getClass(), methodName);
			if (method != null) {
				return method.invoke(item);
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		
		Field field = Property.getFieldByFieldName(item.getClass(), property);
		if (field != null) {
			Property.makeAccessible(field);
			try {
				return field.get(item);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		throw new RuntimeException(String.format(
				"Could not resolve the property '%1s' (no method found with name '%1s' found and no property found with this name).", property,
				methodName));

	}

	public String getLabelForFalse() {
		return labelForFalse;
	}

	public void setLabelForFalse(String labelForFalse) {
		this.labelForFalse = labelForFalse;
	}

	public String getLabelForTrue() {
		return labelForTrue;
	}

	public void setLabelForTrue(String labelForTrue) {
		this.labelForTrue = labelForTrue;
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
	
	public String getColContent() {
		StringBuilder builder = new StringBuilder();

		builder.append("<col ");
		
		if (colWidth != null) {
			builder.append("width = \"").append(colWidth).append("\" ");
		}
		
		builder.append("/>");
		
		return builder.toString();
	}

	public String getColWidth() {
		return colWidth;
	}

	public void setColWidth(String colWidth) {
		this.colWidth = colWidth;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		if (align.matches("left|right|center")) {
			this.align = align;
		}
	}

}
