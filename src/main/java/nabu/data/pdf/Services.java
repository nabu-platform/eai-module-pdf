/*
* Copyright (C) 2018 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package nabu.data.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;

import be.nabu.libs.scraper.Scraper;
import be.nabu.utils.io.IOUtils;
import nabu.data.pdf.types.PDFDocument;

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
	
	@WebResult(name = "pdf")
	public InputStream merge(@WebParam(name = "documents") List<PDFDocument> documents) throws IOException {
		if (documents != null && !documents.isEmpty()) {
			PDDocument doc = new PDDocument();
			for (PDFDocument document : documents) {
				if ("application/pdf".equalsIgnoreCase(document.getContentType())) {
					PDDocument expected = PDDocument.load(document.getContent());
					for (PDPage page : expected.getDocumentCatalog().getPages()) {
						doc.addPage(page);
					}
				}
				else if (document.getContentType().matches("image/.*")) {
					PDRectangle pageSize = PDRectangle.A4;
					PDPage page = new PDPage(pageSize);
					doc.addPage(page);
					try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
						byte[] bytes = IOUtils.toBytes(IOUtils.wrap(document.getContent()));
						// the last parameter is the filename, this is mostly useful for error logging
						PDImageXObject image = PDImageXObject.createFromByteArray(doc, bytes, null);
						
						int originalWidth = image.getWidth();
						int originalHeight = image.getHeight();
						float pageWidth = pageSize.getWidth();
						float pageHeight = pageSize.getHeight();
						float ratio = Math.min(pageWidth / originalWidth, pageHeight / originalHeight);
						float scaledWidth = originalWidth * ratio;
						float scaledHeight = originalHeight * ratio;
						float x = (pageWidth - scaledWidth) / 2;
						float y = (pageHeight - scaledHeight) / 2;
						
						contents.drawImage(image, x, y, scaledWidth, scaledHeight);
					}
				}
			}
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			doc.save(output);
			return new ByteArrayInputStream(output.toByteArray());
		}
		return null;
	}
}
