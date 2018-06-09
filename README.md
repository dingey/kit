#  kit
toolkit simple:xml,json,httpconnection etc.
开始使用，引用
```
<dependency>
  <groupId>com.github.dingey</groupId>
  <artifactId>kit</artifactId>
  <version>1.4</version>
</dependency>
```

# json
json字符串转Object
```
class Man{
  int id;
  String n;
}

String json = "{\"id\":1,\"n\":\"alice\"}";
//方法1
Man m1 = Json.fromJson(json , Man.class);
//方法2
Man m2 = new Json().toObject(json , Man.class);
```
转换成json
```
Man m=new Man();
//方法1
String json1 = Json.toJsonString(m);
//方法2
String json2 = new Json().toJson(m);
```
设置时间格式和是否驼峰转下划线
```
Json j=new Json();
j.setDateFormat("yyyy-MM-dd HH:mm:ss");
j.setCamelCaseToUnderscores(true);
```
转换成map和list结构的对象
```
Json.getJson().toObject(json);
```
# xml
```
Xml.toObject(String xml,Class<T> cl);
Xml.toXml(T o);

XmlBuilder.parse(String xml);

JAXB
XmlUtil.toXml(T o);
XmlUtil.fromXml(String xml, Class<T> target);
```
# httpclient
http工具类
```
postForm(String, Map<Object, Object>);
postMultipartForm(String, Map<Object, Object>);
postJson(String, String);
postXml(String, String);
get(String);
```
