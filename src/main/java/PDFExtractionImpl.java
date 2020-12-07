import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;

public class PDFExtractionImpl implements PDFExtraction{
    @Override
    public void readFile(String fileName) throws Exception {
        //Maybe we will need to think about the size limitation or we need pause time to make sure the large file will be fully loaded.
        PdfReader reader = new PdfReader(fileName);

        if (SimpleBookmark.getBookmark(reader) != null) {
            ExtractionWithBookmark(fileName);
        } else if(new PDFExtractionNoBookmark(fileName).verify()) {
            ExtractionWithoutBookmark(fileName);
        }
    }

    private void ExtractionWithBookmark(String fileName) throws IOException {
        PDFExtractionWithBookmark out = new PDFExtractionWithBookmark(fileName);
    }

    private void ExtractionWithoutBookmark(String fileName) throws Exception {
        PDFExtractionNoBookmark out = new PDFExtractionNoBookmark(fileName);
    }

}
