package mobi.cangol.mobile;

import org.junit.Test;

import java.io.Serializable;

import mobi.cangol.mobile.utils.UrlUtils;

import static junit.framework.Assert.assertEquals;


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        Mode mode1=new Mode(1,"a");
        Mode mode2=new Mode(1,"a");
        Mode mode3=new Mode(2,"c");
        System.out.println(mode1.compareTo(mode2));
        System.out.println(mode1.compareTo(mode3));
    }
    class Mode implements Serializable,Comparable<Mode>{
        long num;
        String time;

        public Mode(long num, String time) {
            this.num = num;
            this.time = time;
        }

        @Override
        public int compareTo(Mode another) {
            if(num!=another.num){
                return (int) (num-another.num);
            }else{
                return time.compareTo(another.time);
            }
        }
    }
}