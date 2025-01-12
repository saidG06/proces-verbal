package com.procesverbal.procesverbal.helper;

import com.procesverbal.procesverbal.services.DocumentService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.procesverbal.procesverbal.AppString.BORDER_SIZE;

@Component
public class Functions {
    static Logger logger = LoggerFactory.getLogger(DocumentService.class);
    public static int getImageFormat(String imgFileName) {
        int format;
        if (imgFileName.endsWith(".emf"))
            format = XWPFDocument.PICTURE_TYPE_EMF;
        else if (imgFileName.endsWith(".wmf"))
            format = XWPFDocument.PICTURE_TYPE_WMF;
        else if (imgFileName.endsWith(".pict"))
            format = XWPFDocument.PICTURE_TYPE_PICT;
        else if (imgFileName.endsWith(".jpeg") || imgFileName.endsWith(".jpg"))
            format = XWPFDocument.PICTURE_TYPE_JPEG;
        else if (imgFileName.endsWith(".png"))
            format = XWPFDocument.PICTURE_TYPE_PNG;
        else if (imgFileName.endsWith(".dib"))
            format = XWPFDocument.PICTURE_TYPE_DIB;
        else if (imgFileName.endsWith(".gif"))
            format = XWPFDocument.PICTURE_TYPE_GIF;
        else if (imgFileName.endsWith(".tiff"))
            format = XWPFDocument.PICTURE_TYPE_TIFF;
        else if (imgFileName.endsWith(".eps"))
            format = XWPFDocument.PICTURE_TYPE_EPS;
        else if (imgFileName.endsWith(".bmp"))
            format = XWPFDocument.PICTURE_TYPE_BMP;
        else if (imgFileName.endsWith(".wpg"))
            format = XWPFDocument.PICTURE_TYPE_WPG;
        else {
            return 0;
        }
        return format;
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    public static String toFrenchNumber(int number) {
        switch (number) {
            case 1:
                return "premierère";
            case 2:
                return "deuxième";
            case 3:
                return "troisième";
            case 4:
                return "quatrième";
            case 5:
                return "cinquième";
            case 6:
                return "sixième";
            case 7:
                return "septième";
            case 8:
                return "huitième";
            case 9:
                return "neuvième";
            case 10:
                return "dixième";

            default:
                return "premierère";
        }
    }
    public static String readTextFile(String textPath) throws FileNotFoundException {
        try {
            InputStream myObj =  new ClassPathResource(textPath).getInputStream();
            String data = "";
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data += myReader.nextLine();
            }
            myReader.close();
            return data;
        } catch (FileNotFoundException e) {

            System.out.println("An error occurred.");
            e.printStackTrace();
            throw new FileNotFoundException("Error with file " + textPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static XWPFDocument addNewLine(XWPFDocument doc) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.addBreak();
        return doc;

    }
    public static String exportDocument(XWPFDocument doc, String title) throws IOException {
        try {
          String path=  makeDirAndSaveFile(title);
          String pathFile= path+"/"+title + ".docx";
          File  file =new File(pathFile);
            FileOutputStream out = new FileOutputStream(file);
            doc.write(out);
            out.close();

            return pathFile;



        } catch (IOException e) {
            logger.error(e.getMessage());

            throw new IOException("error with document " + title);
        }

    }

    public static  String makeDirAndSaveFile(String title){
        String path="C:/proces-verbal-document/"+title+"/";
        File theDir = new File(path);
        if (!theDir.exists()){
            theDir.mkdirs();
        }

        return path;
    }
    public static void setTableBorderColor(XWPFTable table, String color) {

        table.getCTTbl().getTblPr().getTblBorders().getBottom().setColor(color);
        table.getCTTbl().getTblPr().getTblBorders().getTop().setColor(color);
        table.getCTTbl().getTblPr().getTblBorders().getLeft().setColor(color);
        table.getCTTbl().getTblPr().getTblBorders().getRight().setColor(color);
        table.getCTTbl().getTblPr().getTblBorders().getInsideH().setColor(color);
        table.getCTTbl().getTblPr().getTblBorders().getInsideV().setColor(color);

        table.getCTTbl().getTblPr().getTblBorders().getRight().setSz(BigInteger.valueOf(BORDER_SIZE));
        table.getCTTbl().getTblPr().getTblBorders().getTop().setSz(BigInteger.valueOf(BORDER_SIZE));
        table.getCTTbl().getTblPr().getTblBorders().getLeft().setSz(BigInteger.valueOf(BORDER_SIZE));
        table.getCTTbl().getTblPr().getTblBorders().getBottom().setSz(BigInteger.valueOf(BORDER_SIZE));
        table.getCTTbl().getTblPr().getTblBorders().getInsideH().setSz(BigInteger.valueOf(BORDER_SIZE));
        table.getCTTbl().getTblPr().getTblBorders().getInsideV().setSz(BigInteger.valueOf(BORDER_SIZE));
    }
    public Resource loadFileAsResource(String path) throws Exception {
        try {
            File file =new File(path);
            Resource resource = new UrlResource(file.toURI());
            if(resource.exists()) {
                return resource;
            } else {
                throw new Exception("File not found " + path);
            }
        } catch (Exception ex) {
            throw new Exception("File not found " + path, ex);
        }
    }


    public static List<String> listFilesForFolder(final File folder) {

        List<String> filesList=new ArrayList<String>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                filesList.add(fileEntry.getName());
            }
        }
        return filesList;
    }


}
