import com.itextpdf.text.pdf.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PDFExtraction {
    private static String output = "";
    private static String maxLenStr = "";

    public static void main(String[] args) throws Exception {
        PdfReader reader = new PdfReader("test.pdf");
        List list = SimpleBookmark.getBookmark(reader);

        int maxLenOfTitles = -1;
        for (Iterator i = list.iterator(); i.hasNext(); ) {
            maxLenOfTitles = maxLen((Map) i.next(), maxLenOfTitles);
        }
        maxLenStr += (maxLenOfTitles + 5);
        //System.out.println(maxLenStr);
        //System.out.println("4.13.2.5 Threatened and Endangered Species and Other Special Status Species".length());

        for (Iterator i = list.iterator(); i.hasNext(); ) {
            showBookmark((Map) i.next());
        }

        try {
            FileWriter myWriter = new FileWriter("test_output.txt");
            myWriter.write(output.toCharArray());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static int maxLen(Map bookmark, int max) {
        Object titleObj = bookmark.get("Title");
        String title = titleObj.toString();
        int len = title.length();
        if (len > max) max = len;

        ArrayList kids = (ArrayList) bookmark.get("Kids");
        if (kids == null) return max;

        for (Iterator j = kids.iterator(); j.hasNext();) {
            max = maxLen((Map) j.next(), max);
        }

        return max;
    }

    private static void showBookmark(Map bookmark) {
        Object titleObj = bookmark.get("Title");
        String title = titleObj.toString();

        Object pageInfoObj = bookmark.get("Page");
        String pageInfo = "";
        if (pageInfoObj != null) {
            pageInfo = pageInfoObj.toString();
        } else {
            pageInfo = "NULL";
        }

        String[] pageElements = pageInfo.split("\\s+");
        String pageNum = pageElements[0];
        String yValue = "";

        if (pageElements.length < 4) {
            if (pageInfo.toUpperCase().compareTo("NULL") == 0) {
                pageNum = "Page Number N/A";
                yValue = "Y-Value N/A in the Page";
            }
        } else {
            yValue = pageElements[3];
        }

        //System.out.println("Page Number = " + pageNum + ". Y Value = " + yValue + ".");
        String pageNumInfo = "Page Number = " + pageNum + "; Y-Value = " + yValue + ".";
        //System.out.printf("%-80.80s %s\n", title, pageNumInfo);
        String result = String.format("%-" + maxLenStr + "." + maxLenStr + "s %s\n", title, pageNumInfo);

        output += result;

        ArrayList kids = (ArrayList) bookmark.get("Kids");
        if (kids == null)
            return;
        for (Iterator i = kids.iterator(); i.hasNext();) {
            showBookmark((Map) i.next());
        }
    }
}
