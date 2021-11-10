package nabu.data.pdf.types;

import java.io.InputStream;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "document")
public interface PDFDocument {
	public InputStream getContent();
	public String getContentType();
}
