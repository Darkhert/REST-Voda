package cz.obergruber;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;

public class App
{
    public static void main( String[] args )
    {
        TestListenerAdapter tla = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] { Auth.class, User.class, Content.class, Manipulation.class });
        testng.addListener(tla);
        testng.run();
    }
}
