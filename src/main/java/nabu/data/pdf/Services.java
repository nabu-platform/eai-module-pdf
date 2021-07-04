package nabu.data.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;

import be.nabu.libs.scraper.Scraper;

@WebService
public class Services {
	
	@WebResult(name = "pdf")
	public InputStream render(@WebParam(name = "html") InputStream html, @WebParam(name = "url") String url) throws SAXException, IOException, ParserConfigurationException, DocumentException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		render(html, url, output);
		return new ByteArrayInputStream(output.toByteArray());
	}

	private void render(InputStream html, String url, OutputStream output) throws SAXException, IOException, ParserConfigurationException, DocumentException {
		ITextRenderer renderer = new ITextRenderer();
		Document document = Scraper.toDocument(Scraper.html2xml(html));
		renderer.setDocument(document, url);
		renderer.layout();
		renderer.createPDF(output);
	}
}
