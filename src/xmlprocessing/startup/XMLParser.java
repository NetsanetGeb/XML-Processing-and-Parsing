package xmlprocessing.startup;

import java.io.File;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import xmlprocessing.companyInfo.Companies;
import xmlprocessing.employmentInfo.EmploymentInfo;
import xmlprocessing.employmentInfo.EmploymentInfo.Employment;
import xmlprocessing.employmentInfo.EmploymentInfo.Employment.Company;
import xmlprocessing.employmentRecord.Employee;

public class XMLParser
{

	public static void main(String[] args)
	{
		ShortCVDOMParsing();
		TranscriptSAXParsing();
		EmploymentRecordJAXBParsing();
		mergingPartsWithXSL();
		parseAllUsingXSL();
	}


	// Parses ShortCv.xml using DOM and Generating a middle document using Dom
	private static void ShortCVDOMParsing()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			factory.setValidating(true);
			factory.setNamespaceAware(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema");
			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", "ShortCvSchema.xsd");

			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.parse(new File("xml/ShortCv.xml"));

			Node root = document.getFirstChild();

			String firstName = getNodeValue(root.getChildNodes(), "cv:FirstName");
			String lastName = getNodeValue(root.getChildNodes(), "cv:LastName");
			String personNumber = getNodeValue(root.getChildNodes(), "cv:PersonNumber");

			Document output = builder.newDocument();

			Element rootNode = output.createElementNS("http://www.ApplicantProfile.se", "ap:PersonalInfo");

			Element firstNameElement = output.createElement("ap:FirstName");
			firstNameElement.appendChild(output.createTextNode(firstName));

			Element lastNameElement = output.createElement("ap:LastName");
			lastNameElement.appendChild(output.createTextNode(lastName));

			Element personNumberElement = output.createElement("ap:PersonNumber");
			personNumberElement.appendChild(output.createTextNode(personNumber));

			rootNode.appendChild(firstNameElement);
			rootNode.appendChild(lastNameElement);
			rootNode.appendChild(personNumberElement);
			output.appendChild(rootNode);

			DOMSource source = new DOMSource(output);

			StreamResult result = new StreamResult(new File("xml/result/DomParsedShortCVResult.xml"));

			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	//  DOM parsing method
	private static Node findNode(NodeList nodes, String nodeName)
	{
		for (int i = 0; i < nodes.getLength(); i++)
		{
			if (nodes.item(i).getNodeName().equals(nodeName))
			{
				return nodes.item(i);
			}
		}
		return null;
	}


	//  DOM parsing method
	private static String getNodeValue(NodeList nodes, String nodeName)
	{
		Node result = findNode(nodes, nodeName);
		if (result != null)
		{
			return result.getFirstChild().getNodeValue();
		}
		return null;
	}


	// Parsing TranscriptXMLDoc.xml using SAX and Generating a middle  document using DOM.
	 
	private static void TranscriptSAXParsing()
	{
		try
		{
			SAXParserFactory saxpf = SAXParserFactory.newInstance();
			SAXParser saxp = saxpf.newSAXParser();

			// saxp.setProperty("http://xml.org/sax/features/validation", true);
			// saxp.setProperty("http://xml.org/sax/features/namespaces", true);

			saxp.parse("src/xmlprocessing/xmlDoc/TranscriptXMLDoc.xml", new SAXTranscriptParser());

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// SAX parsing class for Parsing TranscriptXMLDoc.xml.
	
	static class SAXTranscriptParser extends DefaultHandler
	{
		HashMap<String, String> generalInfo = new HashMap<>();

		HashMap<String, HashMap<String, String>> courseMap = new HashMap<>();

		String currentKey;

		int courseCount;
		int gradeCount;

		boolean isCourse;

		HashMap<String, String> currentCourse = new HashMap<>();


		public SAXTranscriptParser()
		{
		}


		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if (qName.equals("t:University"))
			{
				currentKey = qName;
			}
			else if (qName.equals("t:Degree"))
			{
				currentKey = qName;
			}
			else if (qName.equals("t:Year"))
			{
				currentKey = qName;
			}
			else if (qName.equals("t:Course"))
			{
				isCourse = true;
			}
			else if (isCourse)
			{
				currentKey = qName;
			}
		}


		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			String content = new String(ch, start, length);
			if (currentKey != null && currentKey.isEmpty() == false && isCourse == false)
			{
				generalInfo.put(currentKey, content);
			}
			else if (currentKey != null && currentKey.isEmpty() == false && isCourse)
			{
				currentCourse.put(currentKey, content);
			}
			currentKey = null;
		}


		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			if (qName.equals("t:Course"))
			{
				courseMap.put(currentCourse.get("t:Name"), new HashMap<>(currentCourse));
				currentCourse.clear();
				isCourse = false;
			}
		}


		@Override
		public void startDocument() throws SAXException
		{
		}


		@Override
		public void endDocument() throws SAXException
		{
			try
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

				DocumentBuilder builder = factory.newDocumentBuilder();

				Document output = builder.newDocument();

				Element rootNode = output.createElementNS("http://www.ApplicantProfile.se", "ap:EducationInfo");

				Element universityElement = output.createElement("ap:University");
				universityElement.appendChild(output.createTextNode(generalInfo.get("t:University")));
				rootNode.appendChild(universityElement);

				Element degreeElement = output.createElement("ap:Degree");
				degreeElement.appendChild(output.createTextNode(generalInfo.get("t:Degree")));
				rootNode.appendChild(degreeElement);

				Element yearElement = output.createElement("ap:Year");
				yearElement.appendChild(output.createTextNode(generalInfo.get("t:Year")));
				rootNode.appendChild(yearElement);

				int gradeSum = 0;
				for (HashMap<String, String> course : courseMap.values())
				{
					String grade = course.get("t:Grade");
					int gradeAsInteger = Integer.parseInt(grade);
					gradeSum += gradeAsInteger;
				}

				int GPA = gradeSum / courseMap.size();

				Element GPAElement = output.createElement("ap:GPA");
				GPAElement.appendChild(output.createTextNode(String.valueOf(GPA)));
				rootNode.appendChild(GPAElement);

				Element coursesElement = output.createElement("ap:Courses");

				for (HashMap<String, String> course : courseMap.values())
				{
					Element courseElement = output.createElement("ap:Course");

					Element nameElement = output.createElement("ap:Name");
					nameElement.appendChild(output.createTextNode(course.get("t:Name")));
					courseElement.appendChild(nameElement);

					Element codeElement = output.createElement("ap:Code");
					codeElement.appendChild(output.createTextNode(course.get("t:Code")));
					courseElement.appendChild(codeElement);

					Element gradeElement = output.createElement("ap:Grade");
					gradeElement.appendChild(output.createTextNode(course.get("t:Grade")));
					courseElement.appendChild(gradeElement);

					coursesElement.appendChild(courseElement);
				}

				rootNode.appendChild(coursesElement);

				output.appendChild(rootNode);

				DOMSource source = new DOMSource(output);

				StreamResult result = new StreamResult(new File("xml/result/SAXParsedTranscriptResult.xml"));

				TransformerFactory transformerFactory = TransformerFactory.newInstance();

				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				transformer.transform(source, result);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	//JAXB Parsing of EmploymentRecordXMLDoc.xml and Generatig an middle document.
	
	private static void EmploymentRecordJAXBParsing()
	{
		try
		{
			JAXBContext jc = JAXBContext.newInstance("xmlprocessing.companyInfo");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			Companies companies = (Companies) unmarshaller.unmarshal(new File("src/xmlprocessing/xmlDoc/CompanyInfoXMLDoc.xml"));

			JAXBContext jc2 = JAXBContext.newInstance("xmlprocessing.employmentRecord");
			Unmarshaller unmarshaller2 = jc2.createUnmarshaller();
			Employee employee = (Employee) unmarshaller2.unmarshal(new File("src/xmlprocessing/xmlDoc/EmploymentRecordXMLDoc.xml"));

			JAXBContext outputJC = JAXBContext.newInstance("xmlprocessing.employmentInfo");

			EmploymentInfo employmentInfo = new EmploymentInfo();

			for (xmlprocessing.employmentRecord.Employee.Employment employment : employee.getEmployment())
			{
				for (xmlprocessing.companyInfo.Companies.Company company : companies.getCompany())
				{
					if (employment.getCompanyName().equals(company.getName()))
					{
						Employment outputEmployment = new Employment();
						outputEmployment.setRole(employment.getRole());
						outputEmployment.setStartDate(employment.getStartDate());
						outputEmployment.setEndDate(employment.getEndDate());

						Company outputCompany = new Company();
						outputCompany.setName(company.getName());
						outputCompany.setCompanyNumber(company.getCompanyNumber());
						outputCompany.setPhoneNumber(company.getPhoneNumber());

						outputEmployment.setCompany(outputCompany);

						employmentInfo.getEmployment().add(outputEmployment);
					}
				}
			}

			Marshaller m = outputJC.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(employmentInfo, new File("xml/result/JAXBEmploymentRecord.xml"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	//  XSL format is used to merge all parsed docuemnts of  the DOM, SAX, JAXB.
	
	private static void mergingPartsWithXSL()
	{
		try
		{
			StreamSource xsl = new StreamSource(new File("xml/ApplicantProfileMerger.xsl"));
			StreamResult output = new StreamResult(new File("xml/result/MergedApplicantProfileResult.xml"));

			Transformer transformer = TransformerFactory.newInstance().newTransformer(xsl);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new StreamSource(), output);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	// Applicant profile is generated by using XSL to parse all merged parts of the application to XML.

	private static void parseAllUsingXSL()
	{
		try
		{
			StreamSource xsl = new StreamSource(new File("xml/ApplicantProfileBuilder.xsl"));
			StreamResult output = new StreamResult(new File("xml/result/ApplicantProfileResult.xml"));

			Transformer transformer = TransformerFactory.newInstance().newTransformer(xsl);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new StreamSource(), output);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
