package org.jdgrid.filter.builder;

import org.jdgrid.filter.ResultFilter;

public interface FilterBuilder {
	public ResultFilter getFilter(String gridId);
}
