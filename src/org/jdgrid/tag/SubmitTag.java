package org.jdgrid.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class SubmitTag extends AbstractTag {
	
	public void doTag() throws JspException, IOException {
		StringBuilder builder = new StringBuilder();
		builder.append("<input type=\"submit\" ").append(getAdditionalAttributes()).append(" />");
		getJspContext().getOut().print(builder.toString());
	}

}
