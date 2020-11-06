import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PDFExtractionNoBookmark {
    public static void main(String[] args) throws Exception {
        PdfReader reader = new PdfReader("NoBookmark.pdf");

        /* One way to do this is get the information from the pages and adjustment accordingly. Which is not very smart.*/
//        for (int i = 5; i <= 7; i++) { //reader.getNumberOfPages(); i++) {
//            System.out.println(PdfTextExtractor.getTextFromPage(reader, i));
//        }


        /* The other way is to use the getLinks method in the Class PdfReader. This method will only require the page number of
        |* table of contents, which I think is reasonable. The outcomes with the provided page numbers are good, I will be able
        |* to find the correct page numbers of the links in the table of contents. However, it seems that I cannot find the method
        |* to retrieve the text itself, which are the titles and subtitles.*/

        ArrayList<PdfAnnotation.PdfImportedLink> list = reader.getLinks(5);
        System.out.println(list.size());

        PdfAnnotation.PdfImportedLink a = list.get(4);
        System.out.println(a.getParameters().toString());

//        for (PdfAnnotation.PdfImportedLink link: list) {
//            System.out.print(link.toString() + " ");
//            System.out.println(link.getDestinationPage());
//        }

        //List list = SimpleBookmark.getBookmark(reader);

        //System.out.println(list.size());
//        PDDocument doc = PDDocument.load(new File("NoBookmark.pdf"));
//
//        doc.getClass();
//
//        PDPage pdfPage = doc.getPage(5);
//
//        List list = pdfPage.getAnnotations();
//
//        for (int i = 0; i < list.size(); i++) {
//            PDAnnotation an = (PDAnnotation) list.get(i);
//
//            if (an instanceof PDAnnotationLink) {
//                System.out.println(an);
//
//                PDAnnotationLink link = (PDAnnotationLink) an;
//
//
//                PDAction action = link.getAction();
//
//                if (action instanceof PDActionURI) {
//                    PDActionURI uri = (PDActionURI) action;
//                    System.out.println(uri.getURI());
//                }
//            }
//        }
    }
}
