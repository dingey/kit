package kit;

import com.di.kit.Request;

public class RequestTest {
    public static void main(String[] args) {
        Request get = Request.get("http://localhost:8090/hi").add("name","爱丽丝").execute();
        System.out.println(get.getContentType());
        System.out.println(get.returnContent());

        Request post = Request.post("http://localhost:8090/hi").add("name", "爱丽丝</a>").execute();
        System.out.println(post.getContentType());
        System.out.println(post.returnContent());

        Request json = Request.post("http://localhost:8090/json").json("{\"id\":1,\"content\":\"爱丽丝\"}").execute();
        System.out.println(json.getContentType());
        System.out.println(json.returnContent());

        Request xml = Request.post("http://localhost:8090/xml").xml("<log><content>爱丽丝</content></log>").accept("text/xml").execute();
        System.out.println(xml.getContentType());
        System.out.println(xml.returnContent());
    }
}
