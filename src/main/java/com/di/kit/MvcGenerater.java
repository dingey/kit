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
import com.di.kit.XmlBuilder.Node;
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
	private boolean swaggerEntity=false;
	private String tablePrefix;

	public MvcGenerater setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
		return this;
	}

	String replacePrefix(String s) {
		if (tablePrefix != null && !tablePrefix.isEmpty()) {
			return s.replaceFirst(tablePrefix, "");
		}
		return s;
	}

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
	
	public MvcGenerater setSwaggerEntity(boolean swaggerEntity) {
		this.swaggerEntity = swaggerEntity;
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
	private boolean jpaAnnotation=false;
	public MvcGenerater createEntity(String entityPackage) {
		return this.createEntity(entityPackage, false);
	}

	public MvcGenerater createEntity(String entityPackage, boolean replace) {
		this.entityPackage = entityPackage;
		for (Table t : tables) {
			Str s = new Str();
			String className = StringUtil.underlineToLowerCamelCase(replacePrefix(t.getName()));
			className = StringUtil.firstCharUpper(className);
			if (licenses != null && !licenses.isEmpty() && entityLicenses) {
				s.line(licenses);
			}
			s.add("package ").add(entityPackage).line(";").newLine();
			if (lombok) {
				s.line("import lombok.Data;");
			}
			if(swaggerEntity) {
				s.line("import io.swagger.annotations.ApiModel;");
				s.line("import io.swagger.annotations.ApiModelProperty;");
			}
			if(jpaAnnotation) {
				s.line("import javax.persistence.Table;");
			}
			if (entityBaseClass == null) {
				s.line("import java.io.Serializable;");
				s.line("/**").add(" * ").line(t.getComment()).line(" * @author " + author);
				s.add(" * @date ").line(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
				s.line(" */");
				if (lombok) {
					s.line("@Data");
				}
				if(swaggerEntity) {
					s.add("@ApiModel(\"").add(t.getComment()).add("\")");
				}
				if(jpaAnnotation) {
					s.add("@Table(name = \"").add(t.getName()).add("\")");
				}
				s.add("public class ").add(className).line(" implements Serializable {");
				s.add("	private static final long serialVersionUID = ").add(IdWorker.nextId()).line("L;");
				for (Column c1 : t.getAllColumns()) {
					String lower=StringUtil.underlineToLowerCamelCase(c1.getName());
					if (contain(entityBaseClass, lower))
						continue;
					if (!c1.getRemark().isEmpty()) {
						if(swaggerEntity) {
							s.add("    @ApiModelProperty(\"").add(c1.getRemark()).line("\")");
						}else {
							s.line("    /**").add("	 * ").line(c1.getRemark()).line("	 */");
						}
					}
					if(c1.isPrimaryKey()&&jpaAnnotation) {
						s.line("	@Id");
					}
					s.add("    private ").add(c1.getType().getJava()).add(" ");
					s.add(lower).line(";");
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
					s.line("@Data");
				}
				if(swaggerEntity) {
					s.add("@ApiModel(\"").add(t.getComment()).line("\")");
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
							if(swaggerEntity) {
								s.add("    @ApiModelProperty(\"").add(c1.getRemark()).line("\")");
							}else {
								s.line("    /**").add("	 * ").line(c1.getRemark()).line("	 */");
							}
						}
						if(c1.isPrimaryKey()&&jpaAnnotation) {
							s.line("	@Id");
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
			out(epath + "/" + className + ".java", s.toString(), replace);
		}
		return this;
	}

	private boolean contain(Class<?> o, String n) {
		boolean b = false;
		try {
			if (o != null && o.getSuperclass() != Object.class) {
				b = contain(o.getSuperclass(), n);
			}
			if (!b && o != null)
				b = o.getDeclaredField(n) != null;
		} catch (NoSuchFieldException | SecurityException e) {
		}
		return b;
	}

	private boolean hasParametersType(Class<?> a) {
		return a.getTypeParameters().length > 0;
	}

	public MvcGenerater createXml(String xmlPath) {
		return this.createXml(xmlPath, false);
	}

	public MvcGenerater createXml(String xmlPath, boolean replace) {
		for (Table t : tables) {
			Str s = new Str();
			String className = StringUtil.underlineToLowerCamelCase(replacePrefix(t.getName()));
			className = StringUtil.firstCharUpper(className);
			String keyCol = (t.getPrimaryKeys() != null && t.getPrimaryKeys().size() > 0)
					? t.getPrimaryKeys().get(0).getName() : "";
			String keyProp = StringUtil.underlineToLowerCamelCase(keyCol);
			
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
			
			s.add("	<insert id=\"insertSelective\"");
			if (persistence.isUseGeneratedKeys()) {
				s.add(" useGeneratedKeys=\"true\" keyProperty=\"").add(StringUtil
						.firstCharLower(StringUtil.underlineToLowerCamelCase(t.getPrimaryKeys().get(0).getName())))
						.add("\"");
			}
			s.line(">");
			s.add("		insert into `").add(t.getName()).line("` (");
			s.line("		<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >");
			for (Column c : t.getAllColumns()) {
				String pro = StringUtil.underlineToLowerCamelCase(c.getName());
				s.add("			<if test=\"" + pro + " != null\" >").add(c.getName()).line(",</if>");
			}
			s.line("		</trim>");
			s.line("		<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\" >");
			for (Column c : t.getAllColumns()) {
				String pro = StringUtil.underlineToLowerCamelCase(c.getName());
				s.add("			<if test=\"" + pro + " != null\" >#{").add(pro).line("},</if>");
			}
			s.line("		</trim>").line("	</insert>");
			
			s.line("    <update id=\"update\">");
			s.add("		update `").add(t.getName()).line("` set ");
			for (Column c : t.getColumns()) {
				String s0 = StringUtil.firstCharLower(StringUtil.underlineToLowerCamelCase(c.getName()));
				s.add("		`").add(c.getName()).add("` = #{").add(s0).add("},").newLine();
			}
			s.deleteLastChar();
			s.line("		where "+keyCol+" = #{"+keyProp+"}");
			s.line("	</update>");
			
			s.line("    <update id=\"updateSelective\">");
			s.add("		update ").line(t.getName());
			s.line("		<set>");
			for (Column c : t.getColumns()) {
				String pro = StringUtil.underlineToLowerCamelCase(c.getName());
				s.add("			<if test=\"" + pro + " != null\" >").add(c.getName()).add("=#{").add(pro).line("},</if>");
			}
			s.line("		</set>").line("		where " + keyCol + "=#{" + keyProp + "}");
			s.line("	</update>");
			
			s.line("    <delete id=\"delete\">");
			s.add("		update `").add(t.getName()).line("` set");
			s.line("		`del_flag` =0");
			s.line("		where "+keyCol+" = #{"+keyProp+"}").line("	</delete>");
			s.add("    <select id=\"get\" resultType=\"").add(entityPackage).add(".").add(className).line("\">");
			s.add("        select * from `").add(t.getName());
			s.line("` where `del_flag` = 0 and "+keyCol+" = #{"+keyProp+"}").line("    </select>");
			s.add("    <select id=\"findList\" resultType=\"").add(entityPackage).add(".").add(className).line("\">");
			s.add("        select * from `").add(t.getName());
			s.line("` where `del_flag` = 0 order by `created_at` desc");
			s.line("    </select>").line("</mapper>");
			out(path.replaceFirst("java", "resources") + xmlPath + className + "Mapper.xml", s.toString(), replace);
		}
		return this;
	}

	public MvcGenerater createMapper(String mapperPackage) {
		this.mapperPackage = mapperPackage;
		for (Table t : tables) {
			Str s = new Str();
			String className = StringUtil.underlineToLowerCamelCase(replacePrefix(t.getName()));
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
			String className = StringUtil.underlineToLowerCamelCase(replacePrefix(t.getName()));
			className = StringUtil.firstCharUpper(className);
			if (licenses != null && !licenses.isEmpty() && serviceLicenses) {
				s.line(licenses);
			}
			s.add("package ").add(servicePackage).line(";").newLine();
			if (serviceBaseClass != null) {
				s.add("import ").add(serviceBaseClass.getName()).line(";");
			}
			s.add("import ").add(entityPackage).add(".").add(className).add(";").newLine();
			if (hasParametersType(serviceBaseClass) && serviceBaseClass.getTypeParameters().length == 2)
				s.add("import ").add(mapperPackage).add(".").add(className).add("Mapper;").newLine().newLine();
			s.line("/**").add(" * ").line(t.getComment() + "service").line(" * @author " + author);
			s.add(" * @date ").line(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
			s.line(" */");
			if(serviceBaseClass.isInterface()){
				s.add("public interface ");
			}else{
				s.add("public class ");
			}
			s.add(className).add("Service");
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
				}else if (hasParametersType(serviceBaseClass) && serviceBaseClass.getTypeParameters().length == 1) {
					s.add("<").add(className).add(">");
				}
			}
			s.line(" {").newLine().add("}");
			out(path + servicePackage.replace(".", "/") + "/" + className + "Service.java", s.toString());
		}
		return this;
	}
	
	private Class<?> serviceInterface;
	private String serviceInterfacePackage;
	
	public MvcGenerater setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface=serviceInterface;
		return this;
	}
	
	public MvcGenerater createServiceInterface(String serviceInterfacePackage) {
		this.serviceInterfacePackage=serviceInterfacePackage;
		for (Table t : tables) {
			Str s = new Str();
			String className = StringUtil.underlineToLowerCamelCase(replacePrefix(t.getName()));
			className = StringUtil.firstCharUpper(className);
			if (licenses != null && !licenses.isEmpty() && serviceLicenses) {
				s.line(licenses);
			}
			s.add("package ").add(serviceInterfacePackage).line(";").newLine();
			if (serviceInterface != null) {
				s.add("import ").add(serviceInterface.getName()).line(";");
			}
			s.add("import ").add(entityPackage).add(".").add(className).add(";").newLine();
			s.line("/**").add(" * ").line(t.getComment() + "service").line(" * @author " + author);
			s.add(" * @date ").line(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
			s.line(" */");
			s.add("public interface ").add(className).add("Service");
			if (serviceInterface != null) {
				s.add(" extends ").add(serviceInterface.getSimpleName());
				if (hasParametersType(serviceInterface) && serviceInterface.getTypeParameters().length == 2) {
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
				}else if (hasParametersType(serviceInterface) && serviceInterface.getTypeParameters().length == 1) {
					s.add("<").add(className).add(">");
				}
			}
			s.line(" {").newLine().add("}");
			out(path + serviceInterfacePackage.replace(".", "/") + "/" + className + "Service.java", s.toString());
		}
		return this;
	}
	
	private Class<?> serviceImpl;
	private String serviceImplPackage;
	
	public MvcGenerater setServiceImpl(Class<?> serviceImpl) {
		this.serviceImpl=serviceImpl;
		return this;
	}
	
	public MvcGenerater createServiceImpl(String serviceImplPackage) {
		this.serviceImplPackage=serviceImplPackage;
		for (Table t : tables) {
			Str s = new Str();
			String className = StringUtil.underlineToLowerCamelCase(replacePrefix(t.getName()));
			className = StringUtil.firstCharUpper(className);
			if (licenses != null && !licenses.isEmpty() && serviceLicenses) {
				s.line(licenses);
			}
			s.add("package ").add(serviceImplPackage).line(";").newLine();
			if (serviceInterface != null) {
				s.add("import ").add(serviceInterfacePackage).add(".").add(className).line("Service;");
			}
			if (serviceImpl != null) {
				s.add("import ").add(serviceImpl.getName()).line(";");
				if (hasParametersType(serviceImpl) && serviceImpl.getTypeParameters().length == 2)
					s.add("import ").add(mapperPackage).add(".").add(className).line("Mapper;");
			}
			s.add("import ").add(entityPackage).add(".").add(className).add(";").newLine();
			s.line("import org.springframework.stereotype.Service;");
			s.line("/**").add(" * ").line(t.getComment() + "service").line(" * @author " + author);
			s.add(" * @date ").line(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
			s.line(" */");
			s.add("@Service(\"").add(StringUtil.firstCharLower(className)).line("Service\")");
			s.add("public class ").add(className).add("ServiceImpl");
			if (serviceImpl != null) {
				s.add(" extends ").add(serviceImpl.getSimpleName());
				if (hasParametersType(serviceImpl) && serviceImpl.getTypeParameters().length == 2) {
					String name2 = serviceImpl.getTypeParameters()[0].getName();
					String name = entityBaseClass.getTypeParameters()[0].getName();
					if (name.equals(name2)) {
						if (serviceImpl.getTypeParameters().length == 2) {
							s.add("<").add(className).add(",").add(className).add("Mapper>");
						}
					} else {
						if (serviceImpl.getTypeParameters().length == 2) {
							s.add("<").add(className).add("Mapper,").add(className).add(">");
						}
					}
				}else if (hasParametersType(serviceImpl) && serviceImpl.getTypeParameters().length == 1) {
					s.add("<").add(className).add(">");
				}
			}
			if (serviceInterface != null) {
				s.add(" implements ").add(className).add("Service");
				if (hasParametersType(serviceInterface) && serviceInterface.getTypeParameters().length == 2) {
					String name2 = serviceInterface.getTypeParameters()[0].getName();
					String name = entityBaseClass.getTypeParameters()[0].getName();
					if (name.equals(name2)) {
						if (serviceInterface.getTypeParameters().length == 2) {
							s.add("<").add(className).add(",").add(className).add("Mapper>");
						}
					} else {
						if (serviceInterface.getTypeParameters().length == 2) {
							s.add("<").add(className).add("Mapper,").add(className).add(">");
						}
					}
				}else if (hasParametersType(serviceInterface) && serviceInterface.getTypeParameters().length == 1) {
					//s.add("<").add(className).add(">");
				}
			}
			s.line(" {").newLine().add("}");
			out(path + serviceImplPackage.replace(".", "/") + "/" + className + "ServiceImpl.java", s.toString());
		}
		return this;
	}
	
	public MvcGenerater createControl(String controlPackage) {
		this.controlPackage = controlPackage;
		for (Table t : tables) {
			Str s = new Str();
			String className = StringUtil.underlineToLowerCamelCase(replacePrefix(t.getName()));
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
			String className = StringUtil.underlineToLowerCamelCase(replacePrefix(t.getName()));
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
		this.out(path, content, false);
	}

	private void out(String path, String content, boolean replace) {
		File f = new File(path);
		if (f.exists() && !replace) {
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
		return PathUtil.getMavenSrcPath() + "main/java/";
	}

	public MvcGenerater createXmlReplaceable(String xmlPath, boolean selective) {
		for (Table t : tables) {
			String className = StringUtil.underlineToLowerCamelCase(replacePrefix(t.getName()));
			className = StringUtil.firstCharUpper(className);
			String pathname = path.replaceFirst("java", "resources") + xmlPath + className + "Mapper.xml";
			File f = new File(pathname);
			if (f.exists()) {
				String xml = FileUtil.readString(pathname, null);
				XmlBuilder builder = new XmlBuilder(xml);
				String keyCol = (t.getPrimaryKeys() != null && t.getPrimaryKeys().size() > 0)
						? t.getPrimaryKeys().get(0).getName() : "";
				String keyProp = StringUtil.underlineToLowerCamelCase(keyCol);
				Str s = new Str();

				Node insert = builder.getById("insert");
				if (insert == null) {
					insert = builder.rootNode().createNode().name("insert");
					insert.addAttribute("id", "insert");
					if (persistence.isUseGeneratedKeys()) {
						insert.addAttribute("useGeneratedKeys", "true");
					}
				} else {
					builder.rootNode().children().remove(insert);
				}
				s.add("insert into ").add(t.getName()).add(" (");
				for (Column c : t.getAllColumns()) {
					s.add(c.getName()).add(",");
				}
				s.delLastChar().add(") values (");
				for (Column c : t.getAllColumns()) {
					s.add("#{").add(StringUtil.underlineToLowerCamelCase(c.getName())).add("},");
				}
				s.delLastChar().add(")");
				insert.text(s.toString());
				builder.rootNode().children().add(insert);
				if (selective) {
					Node is = builder.getById("insertSelective");
					if (is == null) {
						is = builder.rootNode().createNode().name("insert");
						is.addAttribute("id", "insertSelective");
						if (persistence.isUseGeneratedKeys()) {
							is.addAttribute("useGeneratedKeys", "true");
						}
					} else {
						builder.rootNode().children().remove(is);
					}
					s.empty("insert into ").add(t.getName());
					s.add("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >");
					for (Column c : t.getAllColumns()) {
						String pro = StringUtil.underlineToLowerCamelCase(c.getName());
						s.add("<if test=\"" + pro + " != null\" >").add(c.getName()).add(",</if>");
					}
					s.add("</trim>");
					s.add("<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\" >");
					for (Column c : t.getAllColumns()) {
						String pro = StringUtil.underlineToLowerCamelCase(c.getName());
						s.add("<if test=\"" + pro + " != null\" >#{").add(pro).add("},</if>");
					}
					s.add("</trim>");
					is.text(s.toString());
					builder.rootNode().children().add(is);
				}

				Node update = builder.getById("update");
				if (update == null) {
					update = builder.rootNode().createNode().name("update");
					update.addAttribute("id", "update");
				} else {
					builder.rootNode().children().remove(update);
				}
				s.empty("update ").add(t.getName()).add(" set ");
				for (Column c : t.getColumns()) {
					s.add(c.getName()).add("=#{").add(StringUtil.underlineToLowerCamelCase(c.getName())).add("},");
				}
				s.delLastChar().add(" where " + keyCol + "=#{" + keyProp + "}");
				update.text(s.toString());
				builder.rootNode().children().add(update);
				if (selective) {
					Node us = builder.getById("updateSelective");
					if (us == null) {
						us = builder.rootNode().createNode().name("update");
						us.addAttribute("id", "updateSelective");
					} else {
						builder.rootNode().children().remove(us);
					}
					s.empty("update ").add(t.getName());
					s.add("<set>");
					for (Column c : t.getColumns()) {
						String pro = StringUtil.underlineToLowerCamelCase(c.getName());
						s.add("<if test=\"" + pro + " != null\" >").add(c.getName()).add("=#{").add(pro).add("},</if>");
					}
					s.add("</set>").add(" where " + keyCol + "=#{" + keyProp + "}");
					us.text(s.toString());
					builder.rootNode().children().add(us);
				}

				Node del = builder.getById("delete");
				if (del == null) {
					del = builder.rootNode().createNode().name("select");
					del.addAttribute("id", "delete");
				} else {
					builder.rootNode().children().remove(del);
				}
				del.text("update " + t.getName() + " set del=1 where " + keyCol + "=#{" + keyProp + "}");
				builder.rootNode().children().add(del);

				Node get = builder.getById("get");
				if (get == null) {
					get = builder.rootNode().createNode().name("select");
					get.addAttribute("id", "get").addAttribute("resultType", className);
				} else {
					builder.rootNode().children().remove(get);
				}
				get.text("select * from " + t.getName() + " where " + keyCol + "=#{" + keyProp + "}");
				builder.rootNode().children().add(get);

				Node list = builder.getById("list");
				if (list == null) {
					list = builder.rootNode().createNode().name("select");
					list.addAttribute("id", "list").addAttribute("resultType", className);
				} else {
					builder.rootNode().children().remove(list);
				}
				list.text("select * from " + t.getName());
				builder.rootNode().children().add(list);

				s.empty("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
				s.line("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >");
				s.add(builder.rootNode().toString());
				out(pathname, s.toString(), true);
			} else {
				createXml(xmlPath);
			}
		}
		return this;
	}

	public static interface ViewStyle {
		String edit(Table t);

		String list(Table t);
	}
}
