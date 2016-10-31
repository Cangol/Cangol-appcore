package mobi.cangol.mobile;

import org.junit.Test;

import java.lang.reflect.ParameterizedType;

import static junit.framework.Assert.assertEquals;


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void test1() throws Exception {
        System.out.println(Bar.getParameterClass());
        assertEquals(4, 2 + 2);
    }
}
class Bar {
    public static Class<?> getParameterClass() {
        return (Class<?>) (((ParameterizedType)Bar.class.getGenericSuperclass()).getActualTypeArguments()[0]);
    }
}
class Foo<T>{
 T t;
}



