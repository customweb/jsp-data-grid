package org.jdgrid;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jdgrid.executor.Executor;
import org.jdgrid.filter.ResultFilter;
import org.jdgrid.filter.builder.FilterBuilder;
import org.jdgrid.util.UrlEncodedQueryString;


public class Grid<T> {

	private FilterBuilder filterBuilder;
	private Executor<T> executor;
	private List<T> resultList;
	private String gridId;
	private int resultsPerPage = 20;
	private int currentPage = -1;
	private URL currentUrl;
	private ResultFilter filter;
	private Class<?> domainClass;
	
	private long numberOfResults = -1;
	
	public Grid(String gridId, Executor<T> executor, FilterBuilder filterBuilder) {
		this.gridId = gridId;
		this.executor = executor;
		this.filterBuilder = filterBuilder;
		this.domainClass = executor.getDomainClass();
	}
	
	public void prepare() {
		filter = filterBuilder.getFilter(this.gridId);
		filter.setDefaultResultsPerPage(resultsPerPage);
		resultsPerPage = filter.getResultsPerPage();
		currentPage = filter.getPageNumber();
		resultList = executor.getResultSet(filter);
		numberOfResults = executor.getNumberOfItems(filter);
	}

	public FilterBuilder getFilterBuilder() {
		return filterBuilder;
	}

	public void setFilterBuilder(FilterBuilder filterBuilder) {
		this.filterBuilder = filterBuilder;
	}
	
	public List<T> getResultList() {
		if (resultList == null) {
			prepare();
		}
		return resultList;
	}
	
	public int getNumberOfPages() {
		long numberOfResults = getNumberOfResults();
		return (int)Math.floor(numberOfResults / resultsPerPage) + 1;
	}
	
	/**
	 * Returns the first item number of the current page.
	 * 
	 * @return
	 */
	public int getStartPageResult() {
		if (numberOfResults == 0) {
			return 0;
		}
		return getCurrentPage() * getFilter().getResultsPerPage() + 1;
	}
	
	/**
	 * Returns the last item number of the page.
	 * @return
	 */
	public int getEndPageResult() {
		return Math.min((getCurrentPage() + 1) * getFilter().getResultsPerPage(), (int)getNumberOfResults());
	}
	
	public long getNumberOfResults() {
		if (numberOfResults < 0) {
			prepare();
		}
		return numberOfResults;
	}
	
	public void setCurrentUrl(String url) throws MalformedURLException {
		currentUrl = new URL(url);
	}
	
	public void setCurrentUrl(URL url) throws MalformedURLException {
		currentUrl = url;
	}
	
	public String getPageUrl(int pageNumber) {
		String originalUrl = currentUrl.toString();
		
		if (originalUrl.contains("?")) {
			originalUrl = originalUrl.substring(0, originalUrl.indexOf("?"));
		}
		
		UrlEncodedQueryString query = UrlEncodedQueryString.parse(currentUrl.getQuery());
		query.set(getPageNumberParameter(this.gridId), pageNumber);
		
		return originalUrl + "?" + query.toString();
	}
	
	public String getSortingUrl(String fieldName, boolean ascending, boolean descending) {
		String originalUrl = currentUrl.toString();
		
		if (originalUrl.contains("?")) {
			originalUrl = originalUrl.substring(0, originalUrl.indexOf("?"));
		}
		
		UrlEncodedQueryString query = UrlEncodedQueryString.parse(currentUrl.getQuery());
		
		if (ascending) {
			query.set(getOrderByParameter(this.gridId)+fieldName, "ASC");
		}
		else if(descending) {
			query.set(getOrderByParameter(this.gridId)+fieldName, "DESC");
		}
		else {
			query.remove(getOrderByParameter(this.gridId)+fieldName);
		}
		
		return originalUrl + "?" + query.toString();
	}
	
	public String getOrderByParameterName(String fieldName) {
		return getOrderByParameter(this.gridId)+fieldName;
	}
	
	public String getFieldFilterParameterName(String fieldName) {
		return getFilterByParameter(this.gridId) + fieldName;
	}
	
	public String getFilterOperatorName(String fieldName) {
		return getFilterOpPrefixParameter(this.gridId) + fieldName;
	}

	
	public String getResultsPerPageParameterName() {
		return getResultsPerPageParameter(gridId);
	}

	public int getCurrentPage() {
		if (currentPage < 0) {
			prepare();
		}
		return currentPage;
	}
	
	public static String getPageNumberParameter(String gridId) {
		return gridId + "__page_number";
	}
	
	public static String getResultsPerPageParameter(String gridId) {
		return gridId + "__results_per_page";
	}
	
	public static String getOrderByParameter(String gridId) {
		return gridId + "__order_by__";
	}
	
	public static String getFilterByParameter(String gridId) {
		return gridId + "__filter_by__";
	}	
	
	public static String getFilterOpPrefixParameter(String gridId) {
		return gridId + "__filter_op__";
	}

	public void setUrl(HttpServletRequest request) throws MalformedURLException {
		
		String forwardedProtocol = request.getHeader("X-FORWARDED-PROTO");
		
		String urlAsString = request.getRequestURL().toString();
		if (request.getQueryString() != null) {
			urlAsString += "?" + request.getQueryString();
		}
		
		if (forwardedProtocol != null && !forwardedProtocol.isEmpty()) {
			URL url = new URL(urlAsString);
			url = new URL(forwardedProtocol, url.getHost(), url.getPort(), url.getFile());
			setCurrentUrl(url);
		}
		else {
			setCurrentUrl(urlAsString);
		}
		
		
		
	}

	public ResultFilter getFilter() {
		if (filter == null) {
			prepare();
		}
		return filter;
	}

	public String getGridId() {
		return gridId;
	}

	public void setGridId(String gridId) {
		this.gridId = gridId;
	}
	
	
	public URL getCurrentUrl() {
		return this.currentUrl;
	}

	public Class<?> getDomainClass() {
		return domainClass;
	}
	
}
