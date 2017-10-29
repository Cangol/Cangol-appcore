package mobi.cangol.mobile;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.parser.Element;
import mobi.cangol.mobile.parser.JSONParserException;
import mobi.cangol.mobile.parser.JsonUtils;
import mobi.cangol.mobile.parser.XMLParserException;
import mobi.cangol.mobile.parser.XmlUtils;

import static junit.framework.Assert.assertEquals;


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String[] columns=null;
        Set<String> set = new HashSet<>(Arrays.asList(columns));
        System.out.print("set="+set.isEmpty());
    }
}



