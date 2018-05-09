package com.di.kit;

import java.util.LinkedHashMap;

import com.di.kit.JdbcMeta.Column;
import com.di.kit.JdbcMeta.Table;
import com.di.kit.JdbcMeta.Type;

/**
 * @author di
 */
public class ViewStyleLayui extends ViewStyleHelper {
	/**
	 * #http://www.layui.com/doc/element/form.html
	 */
	@Override
	public String edit(Table t) {
		Str s = new Str();
		s.line("<form class=\"layui-form\" action=\"\">");
		for (Column c : t.getColumns()) {
			s.line("  <div class=\"layui-form-item\">");
			s.add("    <label class=\"layui-form-label\">").add(c.getRemark()).add("</label>").newLine();
			s.line("    <div class=\"layui-input-block\">");
			if (c.getType() == Type.VARCHAR || c.getType() == Type.CHAR) {
				String n = StringUtil.underlineToLowerCamelCase(c.getName());
				s.line("<input type=\"text\" name=\"" + n
						+ "\" required  lay-verify=\"required\" placeholder=\"\" autocomplete=\"off\" class=\"layui-input\">");
			} else if (c.getType() == Type.BYTE) {
				LinkedHashMap<String, String> enums = numEnum(c.getRemark());
				if (enums.size() == 2) {
					
				}
			}
			s.line("    </div>");
			s.line("  </div>");
		}
		s.line("</form>");
		return s.toString();
	}

	@Override
	public String list(Table t) {
		return null;
	}

}
