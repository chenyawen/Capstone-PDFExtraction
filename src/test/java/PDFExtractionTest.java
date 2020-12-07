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

    @Test
    public void testPDFWithUnknownBookmark1() throws Exception {
        PDFExtraction.readFile("Final Draft EIR EIS Document.pdf");
    }

    @Test (expected = java.lang.NoClassDefFoundError.class)
    public void testPDFWithUnknownBookmark2() throws Exception {
        PDFExtraction.readFile("207909main_Cx_PEIS_final.pdf");

        // For the detail info, please refer: http://stackoverflow.com/questions/10391271/itext-bouncycastle-classnotfound-org-bouncycastle-asn1-derencodable-and-org-boun
        /*
        * BouncyCastle libs are undergoing heavy API changes that broke the compatibility with other libs like iText.
        *
        * Either
        *
        * use a previous version of BouncyCastle libs. Old versions can be found here. However, you'll have to find the right version of iText that was compatible with this particular version of BC.
        *
        * make your own build of iText (the SVN trunk has been fixed). iText can be build with Maven (there's a short readme file at the root of the SVN). Please note that it's at your own risk, there may be bugs in trunk.
        *
        * wait for the next version of iText. From my experience, iText releases come every couple of months, sometime more often, sometimes less. I'm not an iText committer though, so I can't give you any ETA.
        *
        * Suggest: Add the following dependency to Maven, but actually casue more issues:
        *
        * <dependency>
        * <groupId>org.bouncycastle</groupId>
        * <artifactId>bcprov-jdk15</artifactId>
        * <version>1.44</version>
        * </dependency>
        *
        * Therefore, since we are using the iText 5.5.13.2 and pdfBox 2.0.21. The support of our API really depends on the service depends on those two libraries.
        *
        * To do:
        * To fix this issue, we may need to try to use other methods (libraries) to load the document.
        */
    }
}
