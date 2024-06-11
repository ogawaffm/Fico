package multithreading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ogawa.fico.multithreading.ExtendedThreadFactory;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Order(1) // run this test first, to have predictable pool names
public class ExtendedThreadFactoryTests {

    static ExtendedThreadFactory myFactory = new ExtendedThreadFactory("from myFactory", "myThread");
    static ExtendedThreadFactory myFactoryB = new ExtendedThreadFactory("from myFactoryB", "myThreadB");
    static ExtendedThreadFactory myFactoryC = new ExtendedThreadFactory("from myFactoryC", "myThreadC");
    static ExtendedThreadFactory myFactoryC2 = new ExtendedThreadFactory("from myFactoryC", "myThreadC");
    static ExtendedThreadFactory myFactoryB2 = new ExtendedThreadFactory("from myFactoryB", "myThreadB");

    @Test
    void poolNameTest() {

        assertEquals("from myFactory-0", myFactory.getPoolName());
        assertEquals("from myFactoryB-0", myFactoryB.getPoolName());
        assertEquals("from myFactoryB-1", myFactoryB2.getPoolName());
        assertEquals("from myFactoryC-0", myFactoryC.getPoolName());
        assertEquals("from myFactoryC-1", myFactoryC2.getPoolName());

        // unchanged
        assertEquals("from myFactory-0", myFactory.getPoolName());
        assertEquals("from myFactoryB-0", myFactoryB.getPoolName());
        assertEquals("from myFactoryC-0", myFactoryC.getPoolName());

    }

    @Test
    void threadNameTest() {
        Thread thread;

        thread = myFactory.newThread(TestTaskFactory.createWorkingRunnable(0));
        assertEquals("from myFactory-0:myThread-0", thread.getName());

        thread = myFactory.newThread(TestTaskFactory.createWorkingRunnable(0));
        assertEquals("from myFactory-0:myThread-1", thread.getName());

        thread = myFactoryB.newThread(TestTaskFactory.createWorkingRunnable(0));
        assertEquals("from myFactoryB-0:myThreadB-0", thread.getName());

        thread = myFactoryC.newThread(TestTaskFactory.createWorkingRunnable(0));
        assertEquals("from myFactoryC-0:myThreadC-0", thread.getName());

        thread = myFactory.newThread(TestTaskFactory.createWorkingRunnable(0));
        assertEquals("from myFactory-0:myThread-2", thread.getName());

        thread = myFactoryB.newThread(TestTaskFactory.createWorkingRunnable(0));
        assertEquals("from myFactoryB-0:myThreadB-1", thread.getName());

        thread = myFactoryC.newThread(TestTaskFactory.createWorkingRunnable(0));
        assertEquals("from myFactoryC-0:myThreadC-1", thread.getName());
    }

}
