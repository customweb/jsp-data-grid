package org.jdgrid.executor;

import java.util.List;

import org.jdgrid.filter.ResultFilter;


public interface Executor<T> {
	public List<T> getResultSet(ResultFilter filter);
	public long getNumberOfItems(ResultFilter filter);
	public Class<?> getDomainClass();
}
