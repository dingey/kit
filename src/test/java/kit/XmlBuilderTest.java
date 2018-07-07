package kit;

import com.di.kit.XmlBuilder;
import com.di.kit.XmlBuilder.Node;

/**
 * @author di
 */
public class XmlBuilderTest {

	public void create() {
		XmlBuilder p = new XmlBuilder();
		Node r = p.createRootNode("root");
		r.text("aaa");
		Node n1 = r.createNode().name("n1");
		n1.addAttribute("a", "1");
		n1.text("n1text");
		r.append(n1);
		Node n2 = r.createNode().name("n1");
		Node n3 = n2.createNode().name("n3").text("n2-n1");
		n2.append(n3);
		r.append(n2);
		System.out.println(r.toString());
		System.out.println(r.toFormatString());
	}

	public void parse() {
		String s = "<mapper namespace=\"com.jiehun.cms.mapper.UserMapper\"><insert id=\"insert\">insert into `user` (`id`,`uid`,`cities`,`del_flag`,`updated_at`,`updated_by`,`created_at`,`created_by`)values (#{id},#{uid},#{cities},#{delFlag},#{updatedAt},#{updatedBy},#{createdAt},#{createdBy})</insert><update id=\"update\">update `user` set `uid` = #{uid},`cities` = #{cities},`del_flag` = #{delFlag},`updated_at` = #{updatedAt},`updated_by` = #{updatedBy},`created_at` = #{createdAt},`created_by` = #{createdBy}WHERE id = #{id}</update><delete id=\"delete\">update `user` set`del_flag` =#{DEL_FLAG_DELETE}WHERE id = #{id}</delete><select id=\"get\" resultType=\"User\">        SELECT * FROM `user` WHERE `del_flag` = 0 AND `id` = #{id}    </select><select id=\"findList\" resultType=\"User\">        SELECT * FROM `user` WHERE `del_flag` = #{DEL_FLAG_NORMAL} ORDER BY `updated_at` DESC    </select></mapper>";
		XmlBuilder parser = new XmlBuilder(s);
		Node get = parser.getById("get");
		System.out.println(get.text().trim());
	}
}
