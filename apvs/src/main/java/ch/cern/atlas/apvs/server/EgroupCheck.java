package ch.cern.atlas.apvs.server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EgroupCheck {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private static boolean DEBUG = false;

	/**
	 * Starting point for the SAAJ - SOAP Client Testing
	 */
	public static void main(String args[]) {
		if (args.length < 2) {
			System.err.println("Usage: EgroupCheck egroup email");
			System.exit(1);
		}

		try {
			boolean result = check(args[0].toLowerCase(), args[1].toLowerCase());
			System.err.println(result);
		} catch (Exception e) {
			System.err
					.println("Error occurred while sending SOAP Request to Server");
			e.printStackTrace();
		}
	}

	public static boolean check(String egroup, String email)
			throws UnsupportedOperationException, SOAPException,
			FileNotFoundException, IOException, TransformerException {

		SOAPConnection soapConnection = null;
		try {
			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory
					.newInstance();
			soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			String url = "https://cra-ws.cern.ch/cra-ws/CraEgroupsWebService.wsdl";
			SOAPMessage soapResponse = soapConnection.call(
					createSOAPRequest(egroup), url);

			// Process the SOAP Response
			if (DEBUG) {
				printSOAPResponse(soapResponse);
			}

			SOAPBody body = soapResponse.getSOAPBody();
			for (Element member : elements(body.getElementsByTagName("Member"))) {
				for (Element keyValue : elements(member.getChildNodes())) {
					if ((keyValue.getTagName().equalsIgnoreCase("email"))
							&& (keyValue.getTextContent()
									.equalsIgnoreCase(email))) {
						return true;
					}
				}
			}
		} finally {
			if (soapConnection != null) {
				soapConnection.close();
			}
		}

		return false;
	}

	private static List<Element> elements(NodeList nodes) {
		List<Element> result = new ArrayList<Element>(nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element)
				result.add((Element) node);
		}
		return result;
	}

	private static SOAPMessage createSOAPRequest(String egroup)
			throws SOAPException, FileNotFoundException, IOException {
		String propertiesFile = "EgroupCheckConf.properties";
		Properties properties = new Properties();
		properties.load(new FileReader(propertiesFile));
		String user = properties.getProperty("user");
		String pwd = properties.getProperty("pwd");
		if (user == null || pwd == null) {
			throw new IOException(
					"properties 'user' and 'pwd' should be defined in '"
							+ propertiesFile + "'");
		}

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String serverURI = "https://cra-ws.cern.ch/cra-ws/";

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("ns1", serverURI);

		/*
		 * Constructed SOAP Request Message: <?xml version="1.0"
		 * encoding="UTF-8"?> <SOAP-ENV:Envelope
		 * xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
		 * xmlns:ns1="https://cra-ws.cern.ch/cra-ws/"> <SOAP-ENV:Body>
		 * <ns1:findEgroupByNameRequest>
		 * <ns1:p_niceUserid>user</ns1:p_niceUserid>
		 * <ns1:p_password>pwd</ns1:p_password> <ns1:p_name>e-group</ns1:p_name>
		 * </ns1:findEgroupByNameRequest> </SOAP-ENV:Body> </SOAP-ENV:Envelope>
		 */

		// SOAP Body
		SOAPBody soapBody = envelope.getBody();
		SOAPElement soapBodyElem = soapBody.addChildElement(
				"findEgroupByNameRequest", "ns1");
		SOAPElement soapBodyElem1 = soapBodyElem.addChildElement(
				"p_niceUserid", "ns1");
		soapBodyElem1.addTextNode(user);
		SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("p_password",
				"ns1");
		soapBodyElem2.addTextNode(pwd);
		SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("p_name",
				"ns1");
		soapBodyElem3.addTextNode(egroup);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", serverURI
				+ "CraEgroupsWebService/findEgroupByName");

		soapMessage.saveChanges();

		/* Print the request message */
		if (DEBUG) {
			System.out.print("Request SOAP Message = ");
			soapMessage.writeTo(System.out);
			System.out.println();
		}

		return soapMessage;
	}

	/**
	 * Method used to print the SOAP Response
	 * @throws SOAPException 
	 * @throws TransformerException 
	 */
	private static void printSOAPResponse(SOAPMessage soapResponse) throws SOAPException, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		Source sourceContent = soapResponse.getSOAPPart().getContent();
		System.out.print("\nResponse SOAP Message = ");
		StreamResult result = new StreamResult(System.out);
		transformer.transform(sourceContent, result);
		System.out.println();
	}

}
