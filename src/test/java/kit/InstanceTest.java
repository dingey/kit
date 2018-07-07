package kit;

import com.di.kit.ClassUtil;

import java.util.HashMap;
import java.util.List;

public class InstanceTest {
    public static class A {
        private int i;
        private int[] is;

        private A() {
        }

        public A(Integer i, List<Integer> is,String a) {
            this.i = i;
        }
    }

    public static void main(String[] args) throws Exception {
        A aa = new A(0, null,null);
        A a = (A) ClassUtil.instance(A.class);
        ClassUtil.setObjectFieldsValue(new HashMap<>(),null);
    }
}
