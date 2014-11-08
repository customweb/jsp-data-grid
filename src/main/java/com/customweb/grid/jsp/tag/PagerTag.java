package com.customweb.grid.jsp.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import com.customweb.grid.Grid;


public class PagerTag extends AbstractTag {
	
	private boolean previousNextButton;
	private boolean startEndButton;
	private Grid<?> grid;
	private int maxPageItems = 9;

	public void doTag() throws JspException, IOException {
		
		StringBuilder builder = new StringBuilder();
		builder.append("<ul ").append(getAdditionalAttributes()).append(">");
		
		if (isStartEndButton()) {
			builder.append("<li class=\"pager-start");
			if (getGrid().getCurrentPage() == 0) {
				builder.append(" active disabled");
			}
			
			builder.append("\">");
			builder.append(buildLink(0, "&laquo;"));
			builder.append("</li>");
		}
		
		if (isStartEndButton()) {
			builder.append("<li class=\"pager-previous");
			if (getGrid().getCurrentPage() == 0) {
				builder.append(" active disabled");
			}
			
			builder.append("\">");
			builder.append(buildLink(Math.max(getGrid().getCurrentPage() - 1, 0), "&lsaquo;"));
			builder.append("</li>");
		}
		
		if (this.getMaxPageItems() >= getGrid().getNumberOfPages()) {
			for (int i = 0; i < getGrid().getNumberOfPages(); i++) {
				builder.append(renderPagerItem(i));
			}
		}
		else {
			int start = Math.max(getGrid().getCurrentPage() - this.getMaxPageItems()/2, 0);
			int end = getGrid().getCurrentPage() + this.getMaxPageItems()/2;
			
			if (end >= getGrid().getNumberOfPages()) {
				start = start - (end - getGrid().getNumberOfPages() + 1);
			}
			for (int i = start; i < start + this.getMaxPageItems(); i++) {
				builder.append(renderPagerItem(i));
			}
		}
		
		
		if (isStartEndButton()) {
			int highestPage = getGrid().getNumberOfPages() - 1;
			builder.append("<li class=\"pager-next");
			if (getGrid().getCurrentPage() == highestPage) {
				builder.append(" active disabled");
			}
			
			builder.append("\">");
			builder.append(buildLink(Math.min(getGrid().getCurrentPage() + 1, highestPage), "&rsaquo;"));
			builder.append("</li>");
		}

		
		if (isStartEndButton()) {
			builder.append("<li class=\"pager-end");
			int highestPage = getGrid().getNumberOfPages() - 1;
			if (getGrid().getCurrentPage() == highestPage) {
				builder.append(" active disabled");
			}
			builder.append("\">");
			builder.append(buildLink(highestPage, "&raquo;"));
			builder.append("</li>");
		}
		
		builder.append("</ul>");
		
		getJspContext().getOut().print(builder.toString());
		grid = null;
	}

	private String renderPagerItem(int i) {
		StringBuilder builder = new StringBuilder();
		builder.append("<li");
		if (getGrid().getCurrentPage() == i) {
			builder.append(" class=\"active\"");
		}
		builder.append(">");
		builder.append(buildLink(i, new Integer(i + 1).toString()));
		builder.append("</li>");
		return builder.toString();
	}
	
	public boolean isPreviousNextButton() {
		return previousNextButton;
	}

	public void setPreviousNextButton(boolean previousNextButton) {
		this.previousNextButton = previousNextButton;
	}

	public boolean isStartEndButton() {
		return startEndButton;
	}

	public void setStartEndButton(boolean startEndButton) {
		this.startEndButton = startEndButton;
	}
	
	private Grid<?> getGrid() {
		if (grid == null) {
			GridTag gridTag = (GridTag) findAncestorWithClass(this, GridTag.class);
			grid = gridTag.getGrid();
		}
		return grid;
	}
	
	private String buildLink(int pageNumber, String label) {
		StringBuilder builder = new StringBuilder();
		builder.append("<a class=\"ajax-event\" href=\"").append(getGrid().getPageUrl(pageNumber)).append("\">").append(label).append("</a>");
		return builder.toString();
	}

	public int getMaxPageItems() {
		return maxPageItems;
	}

	public void setMaxPageItems(int maxPageItems) {
		this.maxPageItems = maxPageItems;
	}

}
