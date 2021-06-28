package cz.obergruber;

import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

import static io.restassured.RestAssured.given;

public class Calculator {
    String endpoint;

    public Calculator() {
        RestAssured.baseURI="http://www.dneonline.com";
        endpoint = "calculator.asmx";
    }

    @DataProvider
    public Object[][] values() {
        return new Object[][]{
                {10, 2, 200},
                {5, 0, 500},
                {-3, 1, 200}
        };
    }

    @Test(groups = "calculator", dataProvider = "values")
    public void add(int[] values) throws Exception {

        String element = "Add";
        String xml = buildXml(element, values);

        Response response=
        given().header("Content-Type", "text/xml").body(xml).
        when().post(endpoint).
        then().extract().response();

        String result = XmlPath.from(response.asString()).getString(element + "Result");
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(String.valueOf(values[0]+values[1]), result);
    }

    @Test(groups = "calculator", dataProvider = "values")
    public void subtract(int[] values) throws Exception {

        String element = "Subtract";
        String xml = buildXml(element, values);

        Response response=
        given().header("Content-Type", "text/xml").body(xml).
        when().post(endpoint).
        then().extract().response();

        String result = XmlPath.from(response.asString()).getString(element + "Result");
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(String.valueOf(values[0]-values[1]), result);
    }

    @Test(groups = "calculator", dataProvider = "values")
    public void multiply(int[] values) throws Exception {

        String element = "Multiply";
        String xml = buildXml(element, values);

        Response response=
        given().header("Content-Type", "text/xml").body(xml).
        when().post(endpoint).
        then().extract().response();

        String result = XmlPath.from(response.asString()).getString(element + "Result");
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(String.valueOf(values[0]*values[1]), result);
    }

    @Test(groups = "calculator", dataProvider = "values")
    public void divide(int[] values) throws Exception {

        String element = "Divide";
        String xml = buildXml(element, values);

        Response response=
        given().header("Content-Type", "text/xml").body(xml).
        when().post(endpoint).
        then().extract().response();

        String result = XmlPath.from(response.asString()).getString(element + "Result");
        Assertions.assertEquals(values[2], response.statusCode());
        if (response.statusCode() != 500) {
            Assertions.assertEquals(String.valueOf(values[0]/values[1]), result);
        }

    }

    private String buildXml(String operation, int[] values) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        // root element
        Element root = document.createElementNS("", "soap:Envelope");
        document.appendChild(root);

        // set an attribute to root element
        Attr attr = document.createAttribute("xmlns:soap");
        attr.setValue("http://schemas.xmlsoap.org/soap/envelope/");
        root.setAttributeNode(attr);

        // body element
        Element body = document.createElementNS("", "soap:Body");
        root.appendChild(body);

        // operation element
        Element operation_ele = document.createElement(operation);
        body.appendChild(operation_ele);

        // set an attribute to NumberToWords element
        attr = document.createAttribute("xmlns");
        attr.setValue("http://tempuri.org/");
        operation_ele.setAttributeNode(attr);

        // value 1 element
        Element value1 = document.createElement("intA");
        value1.appendChild(document.createTextNode(String.valueOf(values[0])));
        operation_ele.appendChild(value1);

        // value 2 element
        Element value2 = document.createElement("intB");
        value2.appendChild(document.createTextNode(String.valueOf(values[1])));
        operation_ele.appendChild(value2);

        // create the xml file
        //transform the DOM Object to an XML File
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(domSource,result);

        return writer.toString();
    }
}
