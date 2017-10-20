package com.di.kit;

/**
 * @author di
 */
public class MybatisGenerator extends MvcGenerater {
	public MvcGenerater createReplace(String xmlPath) {
		return this;
	}

	public static void main(String[] args) {
		String s = "<mapper namespace=\"com.jiehun.cms.mapper.UserMapper\"><insert id=\"insert\">insert into `user` (`id`,`uid`,`cities`,`del_flag`,`updated_at`,`updated_by`,`created_at`,`created_by`)values (#{id},#{uid},#{cities},#{delFlag},#{updatedAt},#{updatedBy},#{createdAt},#{createdBy})</insert><update id=\"update\">update `user` set `uid` = #{uid},`cities` = #{cities},`del_flag` = #{delFlag},`updated_at` = #{updatedAt},`updated_by` = #{updatedBy},`created_at` = #{createdAt},`created_by` = #{createdBy}WHERE id = #{id}</update><delete id=\"delete\">update `user` set`del_flag` =#{DEL_FLAG_DELETE}WHERE id = #{id}</delete><select id=\"get\" resultType=\"User\">        SELECT * FROM `user` WHERE `del_flag` = 0 AND `id` = #{id}    </select><select id=\"findList\" resultType=\"User\">        SELECT * FROM `user` WHERE `del_flag` = #{DEL_FLAG_NORMAL} ORDER BY `updated_at` DESC    </select></mapper>";
		XmlParser parser = new XmlParser(s);
	}

}
