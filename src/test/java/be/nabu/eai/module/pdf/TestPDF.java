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

package be.nabu.eai.module.pdf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;

import be.nabu.libs.scraper.Scraper;

// working footers: https://gist.github.com/mping/626264
// 			the style: running(footer) is crucial
public class TestPDF {
	@SuppressWarnings("deprecation")
	public static void main(String...args) throws IOException, SAXException, DocumentException {
		File file = new File("/home/alex/tmp/my.html");
		ITextRenderer renderer = new ITextRenderer();
        //renderer.setDocumentFromString(content);
		//renderer.setDocument(file);
        Document document = Scraper.toDocument(file.toURL(), null);
        renderer.setDocument(document, "http://localhost:10001/test3/");
        renderer.layout();
        
//        new SVGReplacedElementFactory();
        
        File pdf = new File("/home/alex/tmp/my.pdf");
        OutputStream output = new BufferedOutputStream(new FileOutputStream(pdf));
        try {
        	renderer.createPDF(output);
        	//System.out.println(new String(baos.toByteArray()));
        }
        finally {
        	output.close();
        }
	}
}
