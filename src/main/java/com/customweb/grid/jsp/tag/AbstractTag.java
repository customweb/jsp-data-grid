package com.customweb.grid.jsp.tag;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

abstract public class AbstractTag extends SimpleTagSupport implements DynamicAttributes {
	private Map<String, Object> tagAttributes = new HashMap<String, Object>();

	public String getAdditionalAttributes() {
		return getAdditionalAttributes(tagAttributes);
	}
	
	public String getAdditionalAttributes(Map<String, Object> attributes) {
		StringWriter builder = new StringWriter();

		builder.append(" ");
		for (String attrName : attributes.keySet()) {
			builder.append(attrName);
			builder.append("=\"");
			builder.append(attributes.get(attrName).toString());
			builder.append("\" ");
		}

		return builder.toString();
	}
	
	@Override
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		tagAttributes.put(localName, value);
	}
	
	public Map<String, Object> getDynamicAttribute() {
		return this.tagAttributes;
	}

}
