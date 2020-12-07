import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.*;

public class PDFExtractionTest {
    PDFExtractionImpl PDFExtraction = new PDFExtractionImpl();

    @Test
    public void testPDFWithBookmark() throws Exception {
        PDFExtraction.readFile("NoBookmarkB.pdf");
    }

    @Test
    public void testPDFWithNoBookmark() throws Exception {
        PDFExtraction.readFile("NoBookmark.pdf");
    }
}
