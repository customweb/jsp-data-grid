package org.jdgrid.filter.builder;

import java.util.Map.Entry;

import javax.servlet.ServletRequest;

import org.jdgrid.Grid;
import org.jdgrid.filter.ResultFilter;


public class RequestFilterBuilder implements FilterBuilder{
	
	private ServletRequest request;
	private String gridId;
	private ResultFilter filter;
	private String defaultOrderByField;
	private String defaultOrderBy = "ASC";
	
	public RequestFilterBuilder(ServletRequest request) {
		this.request = request;
	}

	@Override
	public synchronized ResultFilter getFilter(String gridId) {
		this.gridId = gridId;
		filter = new ResultFilter();
		getPageData();
		appendOrderBys();
		appendFieldFilters();
		return filter;
	}

	protected synchronized void getPageData() {
		filter.resetPagesToDefault();
		String pageNumber = request.getParameter(Grid.getPageNumberParameter(gridId));
		if (pageNumber != null) {
			filter.setPageNumber(Integer.parseInt(pageNumber));
		}
		
		String resultsPerPage = request.getParameter(Grid.getResultsPerPageParameter(gridId));
		if (resultsPerPage != null && !resultsPerPage.isEmpty()) {
			filter.setResultsPerPage(Integer.parseInt(resultsPerPage));
		}
	}
	
	protected synchronized void appendOrderBys() {
		String identifier = Grid.getOrderByParameter(gridId);
		
		for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			if (entry.getKey().startsWith(identifier)) {
				String fieldName = entry.getKey().substring(identifier.length());
				boolean ascending = true;
				if (entry.getValue().length > 0) {
					String sort = entry.getValue()[0];
					if (sort.equalsIgnoreCase("DESC")) {
						ascending = false;
					}
				}
				filter.addOrderBy(fieldName, ascending);
			}
		}
		
		if (filter.getOrderBys().size() <= 0 && defaultOrderByField != null && defaultOrderByField.length() > 0) {
			boolean ascending = true;
			if (defaultOrderBy.equalsIgnoreCase("DESC")) {
				ascending = false;
			}
			filter.addOrderBy(defaultOrderByField, ascending);
		}
	}
	
	protected synchronized void appendFieldFilters() {
		String identifier = Grid.getFilterByParameter(gridId);
		String operatorIdentifierPrefix = Grid.getFilterOpPrefixParameter(gridId);
		
		for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			if (entry.getKey().startsWith(identifier)) {
				
				// TODO: Read in the filters with multiple values
				String fieldName = entry.getKey().substring(identifier.length());
				String value = "";
				if (entry.getValue().length > 0) {
					value = entry.getValue()[0];
				}
				
				String operatorIdentifier = operatorIdentifierPrefix + fieldName;
				String operator = request.getParameter(operatorIdentifier);
				
				if (!value.isEmpty()) {
					filter.addFieldFilter(fieldName, operator, value);
				}
				
			}
		}
	}

	public String getDefaultOrderByField() {
		return defaultOrderByField;
	}

	public void setDefaultOrderByField(String defaultOrderByField) {
		this.defaultOrderByField = defaultOrderByField;
	}

	public String getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public void setDefaultOrderBy(String defaultOrderBy) {
		this.defaultOrderBy = defaultOrderBy;
	}
	
	
}
