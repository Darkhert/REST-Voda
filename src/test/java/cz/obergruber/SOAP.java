package cz.obergruber;

import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
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

public class SOAP {
    public SOAP() {
        //RestAssured.baseURI="http://www.dneonline.com";
        RestAssured.baseURI = "https://www.dataaccess.com/";
    }

    @Test
    public void add() throws Exception {

        String endpoint = "calculator.asmx";
        String xml = buildXml();

        Response response=
        given().header("Content-Type", "text/xml").body(xml).
        when().post("webservicesserver/NumberConversion.wso").
        then().statusCode(200).log().all().extract().response();

        XmlPath jsXpath= new XmlPath(response.asString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("five hundred", jsXpath.getString("NumberToWordsResult").trim());

    }

    private String buildXml() throws ParserConfigurationException, TransformerException {
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

        // NumberToWords element
        Element ntw = document.createElement("NumberToWords");
        body.appendChild(ntw);

        // set an attribute to NumberToWords element
        attr = document.createAttribute("xmlns");
        attr.setValue("http://www.dataaccess.com/webservicesserver/");
        ntw.setAttributeNode(attr);

        // ubiNum element
        Element ubiNum = document.createElement("ubiNum");
        ubiNum.appendChild(document.createTextNode("500"));
        ntw.appendChild(ubiNum);

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
