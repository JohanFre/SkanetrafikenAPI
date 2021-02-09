package Skane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Servlet implementation class main
 */
@WebServlet("")
public class main extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// public String URL =
	// "http://www.labs.skanetrafiken.se/v2.2/querypage.asp?inpPointFr=lund&inpPointTo=ystad";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public main() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String fromString = request.getParameter("from");

		out.println("<p>Från: " + fromString + "</p>");

		String URLtoSend = "http://www.labs.skanetrafiken.se/v2.2/stationresults.asp?selPointFrKey=" + fromString;

		request.getRequestDispatcher("/index.jsp").include(request, response);

		
		URL line_api_url = new URL(URLtoSend);

		HttpURLConnection linec = (HttpURLConnection) line_api_url.openConnection();

		linec.setDoInput(true);
		linec.setDoOutput(true);
		linec.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(linec.getInputStream()));

		String inputLine;
		String ApiResponse = "";

		while ((inputLine = in.readLine()) != null) {

			ApiResponse += inputLine;

		}

		in.close();

		System.out.println(ApiResponse);

		Document doc = convertStringToXMLDocument(ApiResponse);

		doc.getDocumentElement().normalize();
		System.out.println("Root ele:" + doc.getDocumentElement().getNodeName());
		Node nodeBody = doc.getElementsByTagName("soap:Body").item(0);

		NodeList nodeResult = (NodeList) nodeBody.getFirstChild().getFirstChild();

		Node nodelines = nodeResult.item(2);

		NodeList listOflines = nodelines.getChildNodes();

		if (request.getParameter("buttonPressed") != null) {

			for (int i = 0; i < listOflines.getLength(); i++) {

				// System.out.println(listOflines.item(i).getFirstChild().getTextContent());

				NodeList allLine = listOflines.item(i).getChildNodes();

				for (int y = 0; y < allLine.getLength(); y++) {
					// System.out.println(allLine.item(y).getTextContent());

					if (allLine.item(y).getNodeName().equals("JourneyDateTime")) {

						out.print("<h4>" + allLine.item(y).getTextContent() + ": </h4>");

						// add xml result to list
						// names.add(allLine.item(y).getTextContent());
					}
					if (allLine.item(y).getNodeName().equals("Towards")) {

						out.print("<h5>Mot: " + allLine.item(y).getTextContent() + "</h5>");
						out.print("<br>");

						// add xml result to list
						// names.add(allLine.item(y).getTextContent());
					}

				}
			}
		}
		
		request.getRequestDispatcher("indexList.jsp").include(request, response);

		
	}

	private Document convertStringToXMLDocument(String xmlString) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = null;

		try {

			builder = factory.newDocumentBuilder();

			Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

			return doc;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
