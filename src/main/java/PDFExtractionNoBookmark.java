import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PDFExtractionNoBookmark {
    public static int maxLen = -1;
    private static String output = "";
    public String fileName;

    public PDFExtractionNoBookmark(String name) throws Exception {
        setFileName(name);
        PDFExtraction(fileName);
    }

    public void PDFExtraction(String name) throws Exception {
        PdfReader reader = new PdfReader(name);
        LinkedList<Integer> pagesOfTC = getTCPageNums(reader);

        LinkedList<String> tbName = getTCTableTitles(reader, pagesOfTC);
        LinkedList<Integer> tbNum = getTCTableNums(reader, pagesOfTC);

        //System.out.println("tbName Size = " + tbName.size());
        //System.out.println("tbNum Size = " + tbNum.size());
        //System.out.println("maxLen = " + maxLen);

        //String result = String.format("%-" + maxLen + "." + maxLen + "s %s\n", title, pageNumInfo);

        int index = 0;

        for (String title: tbName) {
            if((! inSet(title.replaceAll("\\s+","").toLowerCase())) && index < tbName.size()) {
                //System.out.println(title + "\n");
//                System.out.println(String.format("%-" + maxLen + "." + maxLen + "s %s\n", title, "" + tbNum.get(index)));
                output += String.format("%-" + maxLen + "." + maxLen + "s %s\n", title, "" + tbNum.get(index));
                index++;
            } else {
                //System.out.println(title + "\n");
                output += title + "\n";
            }
        }

        String newFileName = name.replace(".pdf", ".txt");

        try {
            FileWriter myWriter = new FileWriter(newFileName);
            myWriter.write(output.toCharArray());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static boolean inSet(String title) {
        Set<String> set = new HashSet<String>();

        // Using add() method to add elements into the Set
        set.add("listoftables");
        set.add("contents");
        set.add("tableofcontents");
        set.add("tables");

        return set.contains(title);
    }

    private static LinkedList<Integer> getTCTableNums(PdfReader reader, LinkedList<Integer> pagesOfTC) throws IOException {
        LinkedList<Integer> tbNum = new LinkedList<Integer>();

        for (int pageNum: pagesOfTC) {
            ArrayList<PdfAnnotation.PdfImportedLink> list = reader.getLinks(pageNum);

            for (PdfAnnotation.PdfImportedLink link: list) {
                //System.out.print(link.toString() + " ");
                tbNum.add(link.getDestinationPage());
                //System.out.println(link.getDestinationPage());
            }
        }

        return tbNum;
    }

    private static LinkedList<String> getTCTableTitles(PdfReader reader, LinkedList<Integer> pagesOfTC) throws IOException {
        LinkedList<String> tbName = new LinkedList<>();
        tbName.add("Table of Contents");
        for (int pageNum: pagesOfTC) {
            boolean start = true;
            int count = 0;
            //int pageNum = Integer.parseInt(str);
            ArrayList<PdfAnnotation.PdfImportedLink> list = reader.getLinks(pageNum);
            String myLine = PdfTextExtractor.getTextFromPage(reader, pageNum);

            String[] lines = myLine.split("\n");

            for(int i = 0; i < lines.length; i++) {
                String[] tbNameArr = new String[2];

                if (lines[i].indexOf("    ") > 0) {
                    if (checkExpression(lines[i]))
                        tbNameArr = lines[i].split(" \\s+");
                } else { //
                    if (lines[i].indexOf("..") >= 0) {
                        //System.out.println(i + "    " + lines[i]);
                        start = false;
                        count ++;

                        if(lines[i].indexOf("..") == 0 || lines[i].indexOf("..") == 1) {
                            //System.out.println(i + "    " + lines[i]);
                            String temp = lines[i];
                            if (temp.length() > maxLen)
                                maxLen = temp.length();
                            tbName.add(temp);
                        } else {
                            String temp = lines[i].substring(0, lines[i].indexOf(".."));
                            //System.out.println(i + "    " + temp);
                            if (temp.length() > maxLen)
                                maxLen = temp.length();
                            tbName.add(temp);
                        }
                    } else {
                        if (start || (i + 1 >= lines.length) || lines[i].replaceAll("[\\n\\t ]", "").length() == 0) {
                            continue;
                        } else {
                            count ++;
                            //System.out.println(i + "    " + lines[i]);
                            String temp = lines[i];
                            if (temp.length() > maxLen)
                                maxLen = temp.length();
                            tbName.add(temp);
                        }
                    }
                }
            }
            //System.out.println("COUNT = " + count);
        }

        return tbName;
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


    public boolean verify() throws Exception {
        PdfReader reader = new PdfReader(fileName);
        LinkedList<Integer> pagesOfTC = getTCPageNums(reader);

        LinkedList<String> tbName = getTCTableTitles(reader, pagesOfTC);

        if (tbName != null)
            return true;
        else
            return false;
    }

    private void setFileName(String name) {
        this.fileName = name;
    }
}
