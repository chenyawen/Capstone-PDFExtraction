import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PDFExtractionNoBookmarkNoLink {
    public static Map<String, Integer> pageMap = new HashMap<String, Integer>();
    public static int startPage = -1;
    public static String output = "";

    public PDFExtractionNoBookmarkNoLink(String name) throws Exception {
        PDFExtraction(name);
    }

//    public static void main (String[] args) throws Exception {
//        PDFExtraction("NoBookmarkB.pdf");
//    }

    public static void PDFExtraction(String name) throws Exception {
        PdfReader reader = new PdfReader(name);

        LinkedList<Integer> pagesOfTC = getTCPageNums(reader);
        
        createMap(reader, pagesOfTC);

        String result = replaceTC(reader, pagesOfTC);

        writeToFile(result, ("NoBookmarkNoLink_ " + name).replace(".pdf", ".txt"));
    }

    private static String replaceTC(PdfReader reader, LinkedList<Integer> pagesOfTC) throws IOException {

        for(int i: pagesOfTC) {
            //System.out.println(i);
            String content = PdfTextExtractor.getTextFromPage(reader, i);
            //System.out.println(content);
            String[] lines = content.split("\n");

            for(String line: lines) {
                if (line.trim().isEmpty()) continue;

                String lineM = line.replaceAll("\t*", "");
                lineM = lineM.replaceAll("\n*", "");
                lineM = lineM.replaceAll(" ", "");

                if (lineM.indexOf("ProposedCottonwoodRMPAmendmentfor") >= 0 || lineM.compareTo("DomesticSheepGrazingandFinalSEIS") == 0) continue;
//                System.out.println("OLD LINE: " + line);
                int max = -1;

                for(int j = 0; j < lineM.length(); j++) {
                    if(lineM.charAt(j) == '.' && j > max) {
                        max = j;
                    } else {
                        continue;
                    }
                }
                 if (max > 5){
//                     System.out.println("Max = " + max + " and the char at MAX + 1 = " + lineM.substring(max + 1));
                     String key = lineM.substring(max + 1);

                     if(pageMap.containsKey(key)){
                         String toReplace = "" + pageMap.get(key);
//                         System.out.println("To replace = " + toReplace);
                         int index = line.indexOf(key);
                         line = line.substring(0, index) + toReplace;
//                         line.replace(key, toReplace);
//                         System.out.println("NEW LINE: " + line);
                     }
                     output += line + "\n";
                 } else {
//                     System.out.println(line);
                     output += line + "\n";
                 }
            }
            output += "\n";
        }
        //System.out.println(output);
        return output;
    }

    private static void createMap(PdfReader reader, LinkedList<Integer> pagesOfTC) throws IOException {
        Map<String, Integer> temp = new HashMap<String, Integer>();
        LinkedList<Integer> pages = new LinkedList<>();
        LinkedList<String> fakePages = new LinkedList<>();

        startPage = pagesOfTC.get(0);
//        System.out.println("Start = " + startPage);
        for (int i = pagesOfTC.get(0); i < reader.getNumberOfPages(); i++) {//reader.getNumberOfPages(); i++) { 26; i < 29; i++) {//
            String content = PdfTextExtractor.getTextFromPage(reader, i);
            content = content.replaceAll("\t", "");
            content = content.replaceAll("\n", "");
            content = content.replaceAll(" ", "");
//            content = content.replaceAll("\t", "");
//            content = content.replaceAll("\n", "");
//            content = content.replaceAll(" ", "");

            if(content.indexOf("April2016DomesticSheepGrazingandFinalSEIS") > 0) {
                //String[] segments = content.split(" \\s*");
                String frontTarget = "Amendmentfor";
                String endTarget = "April2016Domestic";

                int frontPos = content.indexOf(frontTarget) + frontTarget.length();
                int endPos = content.indexOf(endTarget);
                String segment = "";
                if(endPos >= 0) {
                    segment = content.substring(frontPos, endPos);
                } else {
                    //System.out.println("!!!!!!");
                    segment = content.substring(frontPos);
                }

                fakePages.add(segment);
                pages.add(i);

                if(!temp.containsKey(segment)) {
                    temp.put(segment, i);
                }
            }
        }

        pageMap = temp;
    }

    private static LinkedList<Integer> getTCPageNums(PdfReader reader) throws IOException {
        LinkedList<Integer> pagesOfTC = new LinkedList<>();

        //System.out.println("The page number is: " + reader.getNumberOfPages());
        /* One way to do this is get the information from the pages and adjustment accordingly. Which is not very smart.*/
        for (int i = 1; i < reader.getNumberOfPages(); i++) {
            String content = PdfTextExtractor.getTextFromPage(reader, i);
//            if(i == 5) {
//                System.out.println(content);
////                System.out.println(content.indexOf("          "));
//            }

            if (content.indexOf("          ") > 0 || content.indexOf(".....") > 0) {
                if(content.indexOf("          ") > 0 && content.indexOf(".....") <= 0) {
                    String toCheck = content.substring(content.indexOf("          "));

                    if (checkExpression(toCheck)) {
                        //System.out.println(toCheck);
                        pagesOfTC.add(i);
                    }
                }else{
                    //System.out.println("Table of Contents in Page Number: " + i);

                    // Check with instructor if we need the additional information within a page or not. Such as Page 174
                    // in the sample PDF document.

                    // Now, we assume all the table of contents should appear before Page 100.

                    if (i <= 100) {
                        pagesOfTC.add(i);
                    }
                }
            }
        }

        Collections.sort(pagesOfTC);

        /* The other way is to use the getLinks method in the Class PdfReader. This method will only require the page number of
        |* table of contents, which I think is reasonable. The outcomes with the provided page numbers are good, I will be able
        |* to find the correct page numbers of the links in the table of contents. However, it seems that I cannot find the method
        |* to retrieve the text itself, which are the titles and subtitles.*/

        //String[] pageArray = pages.split("\\s+");
        return pagesOfTC;
    }

    private static boolean checkExpression(String toCheck) {
        if(toCheck.indexOf("          ") > 0) {
            String check = toCheck.substring(toCheck.indexOf("          "));
            checkExpression(check);
        } else {
            if (toCheck.matches("\\s+[0-9]+")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private static void writeToFile(String result, String newFileName) {
        try {
            FileWriter myWriter = new FileWriter(newFileName);
            myWriter.write(result.toCharArray());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}