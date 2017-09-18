package com.di.kit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.TypeVariable;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.di.kit.JdbcMeta.Column;
import com.di.kit.JdbcMeta.Table;
import com.di.kit.StringUtil;

/**
 * @author d
 */
@SuppressWarnings("unused")
public class MvcGenerater {
	/**
	 * 持久层
	 */
	public static enum PersistenceEnum {
		HIBERNATE("hibernate"), JDBC("jdbc"), JDBC_MAPPER("jdbc_mapper"), JPA("jpa"), IBATIS("ibatis"), MYBATIS(
				"mybatis");
		PersistenceEnum(String name) {
			this.name = name;
		}

		private String name;
		private boolean useGeneratedKeys;

		public boolean isUseGeneratedKeys() {
			return useGeneratedKeys;
		}

		public PersistenceEnum setUseGeneratedKeys(boolean useGeneratedKeys) {
			this.useGeneratedKeys = useGeneratedKeys;
			return this;
		}

		public String getName() {
			return name;
		}

		public static PersistenceEnum getByName(String name) {
			for (PersistenceEnum p : PersistenceEnum.values()) {
				if (p.getName().equalsIgnoreCase(name)) {
					return p;
				}
			}
			return PersistenceEnum.JDBC;
		}
	}

	/**
	 * 控制层
	 */
	public static enum ControlEnum {
		SPRING_MVC("spring_mvc"), STRUTS("struts");
		private String name;

		public String getName() {
			return name;
		}

		ControlEnum(String name) {
			this.name = name;
		}

		public static ControlEnum getByName(String name) {
			for (ControlEnum p : ControlEnum.values()) {
				if (p.getName().equalsIgnoreCase(name)) {
					return p;
				}
			}
			return ControlEnum.SPRING_MVC;
		}
	}

	/**
	 * 视图层
	 */
	public static enum ViewEnum {
		FREEMARKER("freemarker"), JSP("jsp"), VELOCITY("velocity");
		private String name;

		public String getName() {
			return name;
		}

		ViewEnum(String name) {
			this.name = name;
		}

		public static ViewEnum getByName(String name) {
			for (ViewEnum p : ViewEnum.values()) {
				if (p.getName().equalsIgnoreCase(name)) {
					return p;
				}
			}
			return ViewEnum.JSP;
		}
	}

	public MvcGenerater() {
		super();
		path = getPath();
	}

	public MvcGenerater(String url, String username, String password) {
		this.jdbcMeta = new JdbcMeta().setConfig(url, username, password);
		path = getPath();
	}

	private JdbcMeta jdbcMeta;
	private PersistenceEnum persistence;
	private ControlEnum control;
	private ViewEnum view;

	private Class<?> entityBaseClass;
	private Class<?> mapperBaseClass;
	private Class<?> serviceBaseClass;
	private Class<?> controlBaseClass;
	private String licenses;
	private String author = "MvcGenerator by d";
	private String viewHeader;
	private String viewFooter;
	private boolean entityLicenses = false;
	private boolean serviceLicenses = false;
	private boolean controlLicenses = false;
	private boolean mapperLicenses = false;
	private boolean lombok = false;
	private boolean war = false;

	public MvcGenerater setPersistence(PersistenceEnum persistence) {
		this.persistence = persistence;
		return this;
	}

	public MvcGenerater setWar(boolean war) {
		this.war = war;
		return this;
	}

	public MvcGenerater setControl(ControlEnum control) {
		this.control = control;
		return this;
	}

	public MvcGenerater setView(ViewEnum view) {
		this.view = view;
		return this;
	}

	public <T> MvcGenerater setEntityBaseClass(Class<T> entityBaseClass) {
		this.entityBaseClass = entityBaseClass;
		return this;
	}

	public <T> MvcGenerater setMapperBaseClass(Class<T> mapperBaseClass) {
		this.mapperBaseClass = mapperBaseClass;
		return this;
	}

	public <T> MvcGenerater setServiceBaseClass(Class<T> serviceBaseClass) {
		this.serviceBaseClass = serviceBaseClass;
		return this;
	}

	public <T> MvcGenerater setControlBaseClass(Class<T> controlBaseClass) {
		this.controlBaseClass = controlBaseClass;
		return this;
	}

	public MvcGenerater setLicenses(String licenses) {
		this.licenses = licenses;
		return this;
	}

	public MvcGenerater setAuthor(String author) {
		this.author = author;
		return this;
	}

	public MvcGenerater setViewHeader(String viewHeader) {
		this.viewHeader = viewHeader;
		return this;
	}

	public MvcGenerater setViewFooter(String viewFooter) {
		this.viewFooter = viewFooter;
		return this;
	}

	public MvcGenerater setEntityLicenses(boolean entityLicenses) {
		this.entityLicenses = entityLicenses;
		return this;
	}

	public MvcGenerater setServiceLicenses(boolean serviceLicenses) {
		this.serviceLicenses = serviceLicenses;
		return this;
	}

	public MvcGenerater setControlLicenses(boolean controlLicenses) {
		this.controlLicenses = controlLicenses;
		return this;
	}

	public MvcGenerater setMapperLicenses(boolean mapperLicenses) {
		this.mapperLicenses = mapperLicenses;
		return this;
	}

	public MvcGenerater setLombok(boolean lombok) {
		this.lombok = lombok;
		return this;
	}

	private List<Table> tables = new ArrayList<>();

	public MvcGenerater setTables(String... tableNames) {
		try {
			if (tableNames == null || tableNames.length == 0) {
				tables = this.jdbcMeta.getAllTables();
			} else {
				for (String n : tableNames) {
					tables.add(this.jdbcMeta.getTable(n));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.jdbcMeta.close();
		}
		return this;
	}

	private String entityPackage;
	private String mapperPackage;
	private String servicePackage;
	private String controlPackage;

	public MvcGenerater createEntity(String entityPackage) {
		this.entityPackage = entityPackage;
		for (Table t : tables) {
			Str s = new Str();
			String className = StringUtil.underlineToLowerCamelCase(t.getName());
			className = StringUtil.firstCharUpper(className);
			if (licenses != null && !licenses.isEmpty() && entityLicenses) {
				s.line(licenses);
			}
			s.add("package ").add(entityPackage).line(";").newLine();
			if (lombok) {
				s.line("import lombok.Getter;");
				s.line("import lombok.Setter;");
			}
			if (entityBaseClass == null) {
				s.line("/**").add(" * ").line(t.getComment()).line(" * @author " + author);
				s.add(" * @date ").line(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
				s.line(" */");
				if (lombok) {
					s.line("@Getter");
					s.line("@Setter");
				}
				s.add("public class ").add(className).line(" implements Serializable {");
				s.add("	private static final long serialVersionUID = ").add(IdWorker.nextId()).line("L;");
				for (Column c1 : t.getAllColumns()) {
					if (!c1.getRemark().isEmpty()) {
						s.line("    /**").add("	 * ").line(c1.getRemark()).line("	 */");
					}
					s.add("    private ").add(c1.getType().getJava()).add(" ");
					s.add(StringUtil.firstCharLower(StringUtil.underlineToLowerCamelCase(c1.getName()))).line(";");
				}
				if (!lombok) {
					for (Column c1 : t.getAllColumns()) {
						String lower = StringUtil.underlineToLowerCamelCase(c1.getName());
						String upper = StringUtil.underlineToUpperCamelCase(c1.getName());
						if (!contain(entityBaseClass, lower)) {
							s.newLine().add("    public ").add(c1.getType().getJava()).add(" get").add(upper)
									.line("() {");
							s.add("        return ").add(lower).line(";");
							s.line("    }").newLine();
							s.add("    public void set").add(upper).add("(").add(c1.getType().getJava()).add(" ")
									.add(lower).line(") {");
							s.add("        this.").add(lower).add(" = ").add(lower).line(";").line("    }");
						}
					}
				}
			} else {
				s.add("import ").add(entityBaseClass.getName()).line(";");
				s.line("/**").add(" * ").line(t.getComment()).line(" * @author " + author);
				s.add(" * @date ").line(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
				s.line(" */");
				if (lombok) {
					s.line("@Getter");
					s.line("@Setter");
				}
				s.add("public class ").add(className).add(" extends ").add(entityBaseClass.getSimpleName());
				if (hasParametersType(entityBaseClass)) {
					s.add("<").add(className).add(">");
				}
				s.line(" {");
				s.add("	private static final long serialVersionUID = ").add(IdWorker.nextId()).line("L;");
				for (Column c1 : t.getAllColumns()) {
					String fn = StringUtil.firstCharLower(StringUtil.underlineToLowerCamelCase(c1.getName()));
					if (!contain(entityBaseClass, fn)) {
						if (!c1.getRemark().isEmpty()) {
							s.line("    /**").add("	 * ").line(c1.getRemark()).line("	 */");
						}
						s.add("    private ").add(c1.getType().getJava()).add(" ");
						s.add(fn).line(";");
					}
				}
				if (!lombok) {
					for (Column c1 : t.getAllColumns()) {
						String lower = StringUtil.underlineToLowerCamelCase(c1.getName());
						String upper = StringUtil.underlineToUpperCamelCase(c1.getName());
						if (!contain(entityBaseClass, lower)) {
							s.newLine().add("    public ").add(c1.getType().getJava()).add(" get").add(upper)
									.line("() {");
							s.add("        return ").add(lower).line(";");
							s.line("    }").newLine();
							s.add("    public void set").add(upper).add("(").add(c1.getType().getJava()).add(" ")
									.add(lower).line(") {");
							s.add("        this.").add(lower).add(" = ").add(lower).line(";").line("    }");
						}
					}
				}
			}
			s.add("}");
			String epath = getPath() + entityPackage.replace(".", "/");
			out(epath + "/" + className + ".java", s.toString());
		}
		return this;
	}

	private boolean contain(Class<?> o, String n) {
		boolean b = false;
		try {
			if (o.getSuperclass() != Object.class) {
				b = contain(o.getSuperclass(), n);
			}
			if (!b)
				b = o.getDeclaredField(n) != null;
		} catch (NoSuchFieldException | SecurityException e) {
		}
		return b;
	}

	private boolean hasParametersType(Class<?> a) {
		return a.getTypeParameters().length > 0;
	}

	public MvcGenerater createXml(String xmlPath) {
		for (Table t : tables) {
			Str s = new Str();
			String className = StringUtil.underlineToLowerCamelCase(t.getName());
			className = StringUtil.firstCharUpper(className);
			s.line("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			s.line("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >");
			s.add("<mapper namespace=\"").add(mapperPackage).add(".").add(className).line("Mapper\">");
			s.add("	<insert id=\"insert\"");
			if (persistence.isUseGeneratedKeys()) {
				s.add(" useGeneratedKeys=\"true\" keyProperty=\"").add(StringUtil
						.firstCharLower(StringUtil.underlineToLowerCamelCase(t.getPrimaryKeys().get(0).getName())))
						.add("\"");
			}
			s.line(">");
			s.add("		insert into `").add(t.getName()).line("` (");
			for (Column c : t.getAllColumns()) {
				s.add("		`").add(c.getName()).add("`,").newLine();
			}
			s.deleteLastChar();
			s.line("		)values (");
			for (Column c : t.getAllColumns()) {
				String s0 = StringUtil.firstCharLower(StringUtil.underlineToLowerCamelCase(c.getName()));
				s.add("		#{").add(s0).add("},").newLine();
			}
			s.deleteLastChar();
			s.line("		)").line("	</insert>");
			s.line("    <update id=\"update\">");
			s.add("		update `").add(t.getName()).line("` set ");
			for (Column c : t.getColumns()) {
				String s0 = StringUtil.firstCharLower(StringUtil.underlineToLowerCamelCase(c.getName()));
				s.add("		`").add(c.getName()).add("` = #{").add(s0).add("},").newLine();
			}
			s.deleteLastChar();
			s.line("		WHERE id = #{id}");
			s.line("	</update>");
			s.line("    <delete id=\"delete\">");
			s.add("		update `").add(t.getName()).line("` set");
			s.line("		`del_flag` =#{DEL_FLAG_DELETE}");
			s.line("		WHERE id = #{id}").line("	</delete>");
			s.add("    <select id=\"get\" resultType=\"").add(className).line("\">");
			s.add("        SELECT * FROM `").add(t.getName());
			s.line("` WHERE `del_flag` = 0 AND `id` = #{id}").line("    </select>");
			s.add("    <select id=\"findList\" resultType=\"").add(className).line("\">");
			s.add("        SELECT * FROM `").add(t.getName());
			s.line("` WHERE `del_flag` = #{DEL_FLAG_NORMAL} ORDER BY `updated_at` DESC");
			s.line("    </select>").line("</mapper>");
			out(path.replaceFirst("java", "resources") + xmlPath + className + "Mapper.xml", s.toString());
		}
		return this;
	}

	public MvcGenerater createMapper(String mapperPackage) {
		this.mapperPackage = mapperPackage;
		for (Table t : tables) {
			Str s = new Str();
			String className = StringUtil.underlineToLowerCamelCase(t.getName());
			className = StringUtil.firstCharUpper(className);
			if (licenses != null && !licenses.isEmpty() && mapperLicenses) {
				s.line(licenses);
			}
			s.add("package ").add(mapperPackage).line(";").newLine();
			if (mapperBaseClass != null) {
				s.add("import ").add(mapperBaseClass.getName()).line(";");
			}
			s.add("import ").add(entityPackage).add(".").add(className).add(";").newLine().newLine();
			s.line("/**").add(" * ").line(t.getComment() + "Mapper接口").line(" * @author " + author);
			s.add(" * @date ").line(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
			s.line(" */");
			s.add("public interface ").add(className).add("Mapper");
			if (mapperBaseClass != null) {
				s.add(" extends ").add(mapperBaseClass.getSimpleName());
				if (hasParametersType(mapperBaseClass)) {
					s.add("<").add(className).add(">");
				}
			}
			s.line(" {").newLine().add("}");
			out(path + mapperPackage.replace(".", "/") + "/" + className + "Mapper.java", s.toString());
		}
		return this;
	}

	public MvcGenerater createService(String servicePackage) {
		this.servicePackage = servicePackage;
		for (Table t : tables) {
			Str s = new Str();
			String className = StringUtil.underlineToLowerCamelCase(t.getName());
			className = StringUtil.firstCharUpper(className);
			if (licenses != null && !licenses.isEmpty() && serviceLicenses) {
				s.line(licenses);
			}
			s.add("package ").add(servicePackage).line(";").newLine();
			if (serviceBaseClass != null) {
				s.add("import ").add(serviceBaseClass.getName()).line(";");
			}
			s.add("import ").add(entityPackage).add(".").add(className).add(";").newLine();
			s.add("import ").add(mapperPackage).add(".").add(className).add("Mapper;").newLine().newLine();
			s.line("/**").add(" * ").line(t.getComment() + "service").line(" * @author " + author);
			s.add(" * @date ").line(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
			s.line(" */");
			s.add("public class ").add(className).add("Service");
			if (serviceBaseClass != null) {
				s.add(" extends ").add(serviceBaseClass.getSimpleName());
				if (hasParametersType(serviceBaseClass) && serviceBaseClass.getTypeParameters().length == 2) {
					String name2 = serviceBaseClass.getTypeParameters()[0].getName();
					String name = entityBaseClass.getTypeParameters()[0].getName();
					if (name.equals(name2)) {
						if (serviceBaseClass.getTypeParameters().length == 2) {
							s.add("<").add(className).add(",").add(className).add("Mapper>");
						}
					} else {
						if (serviceBaseClass.getTypeParameters().length == 2) {
							s.add("<").add(className).add("Mapper,").add(className).add(">");
						}
					}
				}
			}
			s.line(" {").newLine().add("}");
			out(path + servicePackage.replace(".", "/") + "/" + className + "Service.java", s.toString());
		}
		return this;
	}

	public MvcGenerater createControl(String controlPackage) {
		this.controlPackage = controlPackage;
		for (Table t : tables) {
			Str s = new Str();
			String className = StringUtil.underlineToLowerCamelCase(t.getName());
			className = StringUtil.firstCharUpper(className);
			String lowClassName = StringUtil.firstCharLower(className);
			if (licenses != null && !licenses.isEmpty() && controlLicenses) {
				s.line(licenses);
			}
			s.add("package ").add(controlPackage).line(";").newLine();
			if (controlBaseClass != null) {
				s.add("import ").add(controlBaseClass.getName()).line(";");
			}
			s.line("import org.springframework.beans.factory.annotation.Autowired;");
			s.line("import org.springframework.stereotype.Controller;");
			s.line("import org.springframework.ui.Model;");
			s.line("import org.springframework.web.bind.annotation.RequestMapping;");
			s.line("import org.springframework.web.bind.annotation.RequestParam;");
			s.line("import org.springframework.web.bind.annotation.ResponseBody;").newLine();
			s.line("import com.github.pagehelper.PageInfo;");
			s.add("import ").add(entityPackage).add(".").add(className).add(";").newLine();
			s.add("import ").add(servicePackage).add(".").add(className).add("Service;").newLine().newLine();
			s.line("/**").add(" * ").line(t.getComment() + "controller").line(" * @author " + author);
			s.add(" * @date ").line(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
			s.line(" */");
			s.line("@Controller");
			s.add("public class ").add(className).add("Controller");
			if (controlBaseClass != null) {
				s.add(" extends ").add(controlBaseClass.getSimpleName());
			}
			s.line(" {");
			s.line("	@Autowired");
			s.add("	").add(className).add("Service ").add(StringUtil.firstCharLower(className)).line("Service;")
					.newLine();
			s.add("	@RequestMapping(path = \"/").add(StringUtil.firstCharLower(className)).line("/list\")");
			s.line("	public String list(@RequestParam(defaultValue = \"1\") int pageNum, @RequestParam(defaultValue = \"10\") int pageSize, Model model) {");
			s.add("		PageInfo<").add(className).add("> pageInfo = ").add(lowClassName).add("Service.findPage(new ")
					.add(className).line("(), pageNum, pageSize);");
			s.line("		model.addAttribute(\"pageInfo\", pageInfo);");
			s.add("		return \"/").add(lowClassName).line("/list\";");
			s.line("	}").newLine();
			s.add("	@RequestMapping(path = \"/").add(lowClassName).line("/edit\")");
			s.line("	public String edit(long id, Model model) {");
			s.add("		").add(className).add(" ").add(lowClassName).add(" = ").add(lowClassName)
					.line("Service.get(id);");
			s.add("		model.addAttribute(\"").add(lowClassName).add("\", ").add(lowClassName).line(");");
			s.add("		return \"/").add(lowClassName).add("/edit\";").newLine();
			s.line("	}").newLine();
			s.line("	@ResponseBody");
			s.add("	@RequestMapping(path = \"/").add(lowClassName).line("/save\")");
			s.add("	public String save(").add(className).add(" ").add(lowClassName).line(") {");
			s.add("		").add(lowClassName).add("Service.save(").add(lowClassName).line(");");
			s.line("		return \"{\\\"message\\\":\\\"success\\\"}\";");
			s.line("	}");
			s.add("}");
			out(path + controlPackage.replace(".", "/") + "/" + className + "Controller.java", s.toString());
		}
		return this;
	}

	public MvcGenerater createView(String viewPath) {
		for (Table t : tables) {
			Str s = new Str();
			String className = StringUtil.underlineToLowerCamelCase(t.getName());
			// list
			if (viewHeader != null && !viewHeader.isEmpty()) {
				s.line(viewHeader);
			}
			s.line("<div class=\"container-fluid\">");
			s.line("    <div class=\"row\">");
			s.line("        <div class=\"col-md-12\">");
			s.line("            <form class=\"form-inline\" role=\"form\">");
			s.line("                <input type=\"hidden\" id=\"pageNum\" name=\"pageNum\" value=\"${pageInfo.pageNum}\">");
			s.line("                <input type=\"hidden\" id=\"pageSize\" name=\"pageSize\" value=\"${pageInfo.pageSize}\">");
			s.line("                <div class=\"form-group\">");
			s.line("                    <a type=\"button\" class=\"btn btn-primary\" onclick=\"edit(0)\">新建</a>");
			s.line("                </div>");
			s.line("            </form>");
			s.line("        </div>");
			s.line("    </div>");
			s.line("    <br/>");
			s.line("    <div class=\"row\" id=\"_ba\">");
			s.line("        <div class=\"col-md-12\" id=\"ba\">");
			s.line("            <table class=\"table table-bordered table-hover\">");
			s.line("                <thead>");
			s.line("                <tr>");
			for (Column c : t.getAllColumns()) {
				s.add("                    <th>")
						.add((c.getRemark() == null || c.getRemark().isEmpty()) ? c.getName() : c.getRemark())
						.line("</th>");
			}
			s.line("                    <th>操作</th>");
			s.line("                </tr>");
			s.line("                </thead>");
			s.line("                <tbody>");
			s.line("                    <#list pageInfo.list as p>");
			s.line("                    <tr>");
			for (Column c : t.getAllColumns()) {
				String cn = StringUtil.firstCharLower(StringUtil.underlineToLowerCamelCase(c.getName()));
				if (c.getType().getJava().equals("java.util.Date")) {
					s.add("                        <td>${p.").add(cn).line("?string('yyyy-MM-dd HH:mm:ss')}</td>");
				} else {
					s.add("                        <td>${p.").add(cn).line("!}</td>");
				}
			}
			Column key = t.getPrimaryKeys().get(0);
			String kn = StringUtil.firstCharLower(StringUtil.underlineToLowerCamelCase(key.getName()));
			s.add("                        <td><a href=\"#\" class=\"btn btn-default btn-xs\" onclick=\"edit('${p.")
					.add(kn).line("}')\">编辑</a></td>");
			s.line("                    </tr>");
			s.line("                    </#list>");
			s.line("                </tbody>");
			s.line("            </table>");
			s.line("            <div class=\"col-xs-12\">");
			s.line("                <#if (pageInfo.pageNum>1)>");
			s.line("                    <a href=\"#\" onclick=\"pageTo('${pageInfo.prePage}')\">上一页</a>");
			s.line("                <#else>");
			s.line("                    上一页");
			s.line("                </#if>");
			s.line("                <#if (pageInfo.pageNum<pageInfo.pages)>");
			s.line("                    <a href=\"#\" onclick=\"pageTo('${pageInfo.nextPage}')\">下一页</a>");
			s.line("                <#else>");
			s.line("                    下一页");
			s.line("                </#if>");
			s.line("                ,当前第${pageInfo.pageNum}页 共 ${pageInfo.total} 项,${pageInfo.pages} 页,每页");
			s.line("                <select style=\"height: 32px;\" id=\"pgSize\" onchange=\"changePgSize()\">");
			s.line("                    <option value=\"10\" <#if pageInfo.pageSize==10>selected</#if>>10</option>");
			s.line("                    <option value=\"20\" <#if pageInfo.pageSize==20>selected</#if>>20</option>");
			s.line("                    <option value=\"50\" <#if pageInfo.pageSize==50>selected</#if>>50</option>");
			s.line("                </select> 项 到第 <input type=\"text\"");
			s.line("                                      style=\"width:48px;height: 32px;\" id=\"pgNum\"> 页");
			s.line("                <button class=\"btn btn-default\" onclick=\"pageJunp()\">GO</button>");
			s.line("            </div>");
			s.line("        </div>");
			s.line("    </div>");
			s.line("</div>");
			s.line("<script>");
			s.line("    function changePgSize() {");
			s.line("        $(\"#pageSize\").val($(\"#pgSize\").val());");
			s.line("        pageTo(1);");
			s.line("    }");
			s.line("    function pageJunp() {");
			s.line("        pageTo($(\"#pgNum\").val());");
			s.line("    }");
			s.line("    function pageTo(num) {");
			s.line("        $(\"#pageNum\").val(num);");
			s.line("        $(\"#_ba\").load(\"list #ba\", $(\"form\").eq(0).serialize());");
			s.line("    }");
			s.line("    function edit(id) {");
			s.line("        window.location.href = \"edit?id=\" + id;");
			s.line("    }");
			s.line("</script>");
			if (viewFooter != null && !viewFooter.isEmpty()) {
				s.line(viewFooter);
			}
			createFile(path.replaceFirst("java", "webapp") + viewPath + className);
			out(path.replaceFirst("java", "webapp") + viewPath + className + "/list.ftl", s.toString());
			// edit
			s = new Str();
			if (viewHeader != null && !viewHeader.isEmpty()) {
				s.line(viewHeader);
			}
			s.line("<div class=\"container-fluid\">");
			s.line("    <div class=\"row\">");
			s.line("        <div class=\"col-md-12\">");
			s.line("            <form class=\"form-inline\">");
			s.line("                <div class=\"form-group\">");
			s.line("                    <a href=\"#\" class=\"btn btn-info\" onclick=\"history.go(-1)\">返回</a>");
			s.line("                </div>");
			s.line("            </form>");
			s.line("        </div>");
			s.line("        <div class=\"col-md-12\">");
			s.line("            <form class=\"form-horizontal\">");
			s.add("                <input type=\"hidden\" name=\"").add(kn).add("\" value=\"${").add(className).add(".")
					.add(kn).line("!}\">");
			for (Column c : t.getColumns()) {
				s.line("                <div class=\"form-group\">");
				String cn = StringUtil.firstCharLower(StringUtil.underlineToLowerCamelCase(c.getName()));
				s.add("                    <label class=\"col-sm-2 control-label\" for=\"").add(cn).add("\">");
				s.add((c.getRemark() == null || c.getRemark().isEmpty()) ? kn : c.getRemark()).line("</label>");
				s.line("                    <div class=\"col-sm-6\">");
				s.add("                        <input id=\"").add(cn).add("\" name=\"").add(cn)
						.add("\" class=\"form-control\" type=\"text\"");
				s.add(" value=\"${");
				if (c.getType().getJava().equals("java.util.Date")) {
					s.add("(").add(className).add(".").add(cn).add("?string(\"yyyy-MM-dd HH:mm:ss\"))!");
				} else {
					s.add("(").add(className).add(".").add(cn).add(")!");
				}
				s.line("}\">");
				s.line("                    </div>");
				s.line("                </div>");
			}
			s.line("                <div class=\"form-group\">");
			s.line("                    <div class=\"col-sm-2\"></div>");
			s.line("                    <a href=\"#\" class=\"btn btn-primary\" onclick=\"save();\">保存</a>");
			s.line("                </div>");
			s.line("            </form>");
			s.line("        </div>");
			s.line("    </div>");
			s.line("</div>");
			s.line("<script>");
			s.line("    function save() {");
			s.line("        var b = true;");
			s.line("        var msg = \"错误：\";");
			for (Column c : t.getColumns()) {
				String cn = StringUtil.firstCharLower(StringUtil.underlineToLowerCamelCase(c.getName()));
				s.add("        if ($(\"#").add(cn).line("\").val() == \"\") {");
				s.line("            b = false;");
				s.add("            msg += \"")
						.add((c.getRemark() == null || c.getRemark().isEmpty()) ? kn : c.getRemark()).line("不能为空；\";");
				s.line("        }");
			}
			s.line("        if (b) {");
			s.line("            $.post(\"save\", $(\"form\").eq(0).serialize(), function (data) {");
			s.line("                if (data.message == \"success\") {");
			s.line("                    window.location.href = \"list?\";");
			s.line("                }");
			s.line("            }, 'json')");
			s.line("        } else {");
			s.line("            alert(msg);");
			s.line("        }");
			s.line("    }");
			s.line("</script>");
			if (viewFooter != null && !viewFooter.isEmpty()) {
				s.line(viewFooter);
			}
			if (war) {
				out(path.replaceFirst("java", "webapp") + viewPath + className + "/edit.ftl", s.toString());
			} else {
				out(path.replaceFirst("java", "resources") + viewPath + className + "/edit.ftl", s.toString());
			}
		}
		return this;
	}

	private void out(String path, String content) {
		File f = new File(path);
		if (f.exists()) {
			System.err.println("mvc generator error (exists path): " + path);
			return;
		}
		System.out.println(path);
		FileUtil.writeString(path, content);
	}

	private void createFile(String path) {
		File f = new File(path);
		if (!f.exists() || !f.isDirectory()) {
			f.mkdir();
		}
	}

	String path = null;

	private String getPath() {		
		return PathUtil.getMavenSrcPath()+"main/java/";
	}
}
