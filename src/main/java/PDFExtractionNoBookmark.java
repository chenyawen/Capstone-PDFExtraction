import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.util.ArrayList;

public class PDFExtractionNoBookmark {
    public static void main(String[] args) throws Exception {
        PdfReader reader = new PdfReader("NoBookmark.pdf");
        String pages = "5 6";
        /* One way to do this is get the information from the pages and adjustment accordingly. Which is not very smart.*/
//        for (int i = 5; i <= 7; i++) { //reader.getNumberOfPages(); i++) {
//            System.out.println(PdfTextExtractor.getTextFromPage(reader, i));
//        }


        /* The other way is to use the getLinks method in the Class PdfReader. This method will only require the page number of
        |* table of contents, which I think is reasonable. The outcomes with the provided page numbers are good, I will be able
        |* to find the correct page numbers of the links in the table of contents. However, it seems that I cannot find the method
        |* to retrieve the text itself, which are the titles and subtitles.*/

        String[] pageArray = pages.split("\\s+");
        for (String str: pageArray) {
            int pageNum = Integer.parseInt(str);
            ArrayList<PdfAnnotation.PdfImportedLink> list = reader.getLinks(pageNum);
            String myLine = PdfTextExtractor.getTextFromPage(reader, pageNum);

            System.out.println(myLine);

            for (PdfAnnotation.PdfImportedLink link: list) {
                //System.out.print(link.toString() + " ");
                System.out.println(link.getDestinationPage());
            }
        }
    }
}
