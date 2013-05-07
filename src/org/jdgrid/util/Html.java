package org.jdgrid.util;

import java.util.Map;
import java.util.Map.Entry;

public class Html {

	public static String getDropDown(String name, Map<String, String> values, String selected) {
		return getDropDown(name, values, selected, "");
	}
	
	public static String getDropDown(String name, Map<String, String> values, String selected, String attributes) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("<select").append(attributes).append("name=\"").append(name).append("\">");
		
		for (Entry<String, String> entry : values.entrySet()) {
			builder.append("<option value=\"").append(entry.getKey()).append("\"");
			if (entry.getKey().equals(selected)) {
				builder.append(" selected=\"selected\"");
			}
			builder.append(">").append(entry.getValue()).append("</option>");
		}
		builder.append("</select>");
		
		return builder.toString();
	}
	
}
