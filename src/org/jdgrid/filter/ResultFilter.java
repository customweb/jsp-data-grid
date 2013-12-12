package org.jdgrid.filter;

import java.util.ArrayList;
import java.util.List;

public class ResultFilter {

	private int rangeStart = 0;
	private int rangeEnd = 20;
	private int defaultResultsPerPage = 20;
	private int defaultPageNumber = 0;
	private int resultsPerPage = 10;
	private int pageNumber = 0;
	
	private List<FieldFilter> fieldFilters = new ArrayList<FieldFilter>();
	private List<OrderBy> orderBys = new ArrayList<OrderBy>();
	
	public int getRangeStart() {
		return rangeStart;
	}
	
	public int getRangeEnd() {
		return rangeEnd;
	}
	
	public ResultFilter addFieldFilter(String fieldName, String operator, String value) {
		fieldFilters.add(new FieldFilter(fieldName, operator, value));
		return this;
	}
	
	public ResultFilter addOrderBy(String fieldName, boolean ascending) {
		orderBys.add(new OrderBy(fieldName, ascending));
		return this;
	}

	public List<FieldFilter> getFieldFilters() {
		return fieldFilters;
	}

	public List<OrderBy> getOrderBys() {
		return orderBys;
	}

	public int getDefaultResultsPerPage() {
		return defaultResultsPerPage;
	}

	public void setDefaultResultsPerPage(int defaultResultsPerPage) {
		this.defaultResultsPerPage = defaultResultsPerPage;
	}

	public int getDefaultPageNumber() {
		return defaultPageNumber;
	}

	public void setDefaultPageNumber(int defaultPageNumber) {
		this.defaultPageNumber = defaultPageNumber;
	}

	public int getResultsPerPage() {
		return resultsPerPage;
	}

	public void setResultsPerPage(int resultsPerPage) {
		this.resultsPerPage = resultsPerPage;
		setRanges();
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
		setRanges();
	}
	
	private void setRanges() {
		this.rangeEnd = (this.pageNumber + 1) * this.resultsPerPage;
		this.rangeStart = this.pageNumber * this.resultsPerPage;
	}
	
	public void resetPagesToDefault() {
		this.setPageNumber(this.getDefaultPageNumber());
		this.setResultsPerPage(this.getDefaultResultsPerPage());
	}
	
	public String getOrderBy(String fieldName) {
		
		for (OrderBy orderBy : orderBys) {
			if (orderBy.getFieldName().equals(fieldName)) {
				if (orderBy.isSortAscending()) {
					return "ASC";
				}
				else {
					return "DESC";
				}
			}
		}
		
		return null;
		
	}
}
