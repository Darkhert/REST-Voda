package cz.obergruber;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.testng.ITestNGListener;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.collections.Lists;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws URISyntaxException {

        Reflections reflections = new Reflections("cz.obergruber", new ResourcesScanner());
        Set<String> resourceList = reflections.getResources(x -> true);

        List<String> classes = new ArrayList<String>();
        List<String> exclude = new ArrayList<String>() {
            {
                add("gettest");
                add("main");
                add("main$1");
                add("account");
            }
        };
        for (String r : resourceList) {
            String c = FilenameUtils.removeExtension(r.split("/")[2]);
            if (!exclude.contains(c.toLowerCase())) {
                classes.add(c);
            }
        }

        System.out.println();

        TestListenerAdapter tla = new TestListenerAdapter();
        TestNG testng = new TestNG();
        List<String> suites = Lists.newArrayList();
        URI resource = Objects.requireNonNull(Main.class.getClassLoader().getResource("E2E.xml")).toURI();
        setTestSuites(testng, resource);
        testng.setTestSuites(suites);
        testng.addListener((ITestNGListener) tla);
        testng.run();
    }

    private static void setTestSuites(TestNG driver, URI ets) {
        if (ets.getScheme().equalsIgnoreCase("jar")) {
            String[] jarPath = ets.getSchemeSpecificPart().split("!");
            File jarFile = new File(URI.create(jarPath[0]));
            driver.setTestJar(jarFile.getAbsolutePath());
            driver.setXmlPathInJar(jarPath[1].substring(1));
        } else {
            List<String> testSuites = new ArrayList<String>();
            File tngFile = new File(ets);
            if (tngFile.exists()) {
                System.out.printf("Using TestNG config file %s", tngFile.getAbsolutePath());
                testSuites.add(tngFile.getAbsolutePath());
            } else {
                throw new IllegalArgumentException("A valid TestNG config file reference is required.");
            }
            driver.setTestSuites(testSuites);
        }
    }
}
