package com.procesverbal.procesverbal.services;

import com.procesverbal.procesverbal.AppString;
import com.procesverbal.procesverbal.dto.OfferDto;
import com.procesverbal.procesverbal.dto.SeanceDto;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static com.procesverbal.procesverbal.AppString.*;
import static com.procesverbal.procesverbal.AppString.TIMES_NEW_RAMAN_FONT;
import static com.procesverbal.procesverbal.helper.Functions.*;

@Service
public class SeanceService {


    @Autowired
    CommissionService commissionService;
    @Autowired
    JournalService journalService;
    @Autowired
    OfferService offerService;
    @Autowired
    OfferFinancierService offerFinancierService;
    @Autowired
    ReceptionService receptionService;
    Logger logger = LoggerFactory.getLogger(DocumentService.class);

    public XWPFDocument creatSeance(XWPFDocument document, String aooNumber, Long montant , SeanceDto seanceDto , String objet, OfferDto offerWinner) throws IOException {
        try {


            document = creatHeaderOfDocument(document, objet,aooNumber, seanceDto.getSeanceTitle());


            //set Commission Part:
            if (seanceDto.getIsHasCommission() == 1) {
                document = commissionService.setCommissionPart(document, seanceDto.getCommissionMember(), seanceDto.getDateOfCommission(), aooNumber,objet, seanceDto.getDecisionNumber(), seanceDto.getDecisionDate());

            }
            //cancels effect of page break
            if (seanceDto.getIsHasJournal() == 1) {
                document = journalService.setJournalPart(document, seanceDto.getJournalDtoList(), seanceDto.getDateOfPortail(), seanceDto.getHourOfPortail());
            }
            //setOffers:
            if (seanceDto.getIsHasOfferFirst() == 1) {
                document = offerService.setOffersPart1(document, seanceDto.getOfferDtoList(), montant.toString());
            }
            if(seanceDto.getIsHasOfferSecond()==1){
               document = offerService.setOffersPart2(document,seanceDto.getOfferDtoList(),montant.toString());
               document= offerFinancierService.setOffersFinancierPart(document,montant, seanceDto);

            }
            if (seanceDto.getIsHasReception()==1){

                document= receptionService.setReceptionPart(document,seanceDto,offerWinner);
            }
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.addBreak(BreakType.PAGE);

            return document;

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;

        }

    }


    public XWPFDocument creatHeaderOfDocument(XWPFDocument doc, String objet, String seanceNumber, int seanceTitle) {
        try {
            //set logo image

            doc = addImagesToWordDocument(doc, AppString.LOGO,71,256);
            //set under line image
            doc = addImagesToWordDocument(doc, AppString.UNDER_LINE,8,384);
            //set Aoo number
            XWPFParagraph seanceNumberParagraph = doc.createParagraph();
            seanceNumberParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun seanceNumberRun = seanceNumberParagraph.createRun();
            seanceNumberRun.setBold(true);
            seanceNumberRun.setFontSize(11);
            seanceNumberRun.setFontFamily(UNIVERS_57_CONDEDSED_FONT);
            seanceNumberRun.setText("AOO N° " + seanceNumber.toUpperCase());
            seanceNumberRun.setUnderline(UnderlinePatterns.SINGLE);
            //set object paragraph
            XWPFParagraph ObjetParagraph = doc.createParagraph();
            ObjetParagraph.setIndentationFirstLine(720);
            XWPFRun objetRun1 = ObjetParagraph.createRun();
            objetRun1.setFontSize(11);
            objetRun1.setUnderline(UnderlinePatterns.SINGLE);
            objetRun1.setFontFamily(TIMES_NEW_RAMAN_FONT);
            objetRun1.setText(readTextFile(AppString.OBJET_TEXT));
            XWPFRun objetRun2 = ObjetParagraph.createRun();
            objetRun2.setFontSize(12);
            objetRun2.setBold(true);
            objetRun2.setFontFamily(TIMES_NEW_RAMAN_FONT);
            objetRun2.setText(objet.toUpperCase());
            doc = setSeanceTitle(doc, seanceTitle);
            return doc;
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public XWPFDocument addImagesToWordDocument(XWPFDocument doc, String imagePath,int with,int height) throws IOException {
        try {
            XWPFParagraph p = doc.createParagraph();
            p.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun r = p.createRun();

            InputStream imageFile = new ClassPathResource(imagePath).getInputStream();
            Path temp = Files.createTempFile("resource-", ".ext");
            Files.copy(imageFile, temp, StandardCopyOption.REPLACE_EXISTING);
            FileInputStream input = new FileInputStream(temp.toFile());
            String imgFile1 = imagePath;
            r.addPicture(input, XWPFDocument.PICTURE_TYPE_PNG, imgFile1, Units.toEMU(height), Units.toEMU(with));
            return doc;
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new IOException("error with image " + imagePath);
        } catch (InvalidFormatException e) {
            logger.error(e.getMessage());
            throw new IOException("error with image " + imagePath);
        }
    }


    public XWPFDocument setSeanceTitle(XWPFDocument doc, int seanceTitle) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r = p.createRun();
        r.setUnderline(UnderlinePatterns.SINGLE);
        r.setFontFamily(TIMES_NEW_RAMAN_FONT);
        r.setItalic(true);
        r.setTextHighlightColor("Yellow");
        r.setBold(true);
        r.setText(capitalize(toFrenchNumber(seanceTitle)) + " séance");
        return doc;
    }



}
