package com.di.kit;

import com.di.kit.JdbcMeta.Column;
import com.di.kit.JdbcMeta.Table;

/**
 * @author di
 */
public class LayuiUtil {
	static String creatFormString(Table t) {
		Str s=new Str();
		s.line("<form class=\"layui-form\" action=\"\">");
		for(Column c:t.getColumns()){
			s.line("  <div class=\"layui-form-item\">");
			s.add("    <label class=\"layui-form-label\">").add(c.getRemark()).add("</label>").newLine();
			s.line("    <div class=\"layui-input-block\">");
		}
		return null;
	}

	static String creatTableString(Table t) {
		return null;
	}
}
