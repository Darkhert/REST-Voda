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

        List<String> exclude = new ArrayList<>() {
            {
                add("SpotifySuper");
                add("Main");
                add("Main$1");
                add("Account");
            }
        };

        List<String> include = new ArrayList<>();
        for (String r : resourceList) {
            String cls = FilenameUtils.removeExtension(r.split("/")[2]);
            if (!exclude.contains(cls)) {
                include.add(cls.toLowerCase());
            }
        }

        String xml = "";
        if (args.length == 0) {
            xml = "spotify";
        } else if (args.length == 1 && include.contains(args[0].toLowerCase())) {
            xml = args[0].toLowerCase();
        } else if (args.length > 1) {
            System.out.println("Too much parameters. You can run either E2E (without param), or select one group (defined in param).");
            System.exit(0);
        }

        TestListenerAdapter tla = new TestListenerAdapter();
        TestNG testng = new TestNG();
        URI resource = Objects.requireNonNull(Main.class.getClassLoader().getResource(String.format("%s.xml", xml))).toURI();
        setTestSuites(testng, resource);
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
