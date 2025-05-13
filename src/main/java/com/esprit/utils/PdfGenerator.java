package com.esprit.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class PdfGenerator {

    public static void generateVisiteStatsPdf(Map<Integer, Integer> stats, String filePath) throws Exception {
        // Trier et garder les 4 plus demandées
        Map<Integer, Integer> topStats = stats.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(4)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Créer le document PDF
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Ajouter un logo (depuis resources/images/logo.png)
        try {
            InputStream logoStream = PdfGenerator.class.getResourceAsStream("/images/logo.png");
            if (logoStream != null) {
                Image logo = Image.getInstance(javax.imageio.ImageIO.read(logoStream), null);
                logo.scaleAbsolute(100, 50);
                logo.setAlignment(Image.ALIGN_CENTER);
                document.add(logo);
            }
        } catch (Exception e) {
            System.out.println("Erreur chargement du logo: " + e.getMessage());
        }

        // Titre
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(0, 102, 204));
        Paragraph title = new Paragraph("Top 4 Visites les Plus Demandées", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Style texte
        Font textFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.DARK_GRAY);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(60, 60, 60));

        // Affichage des statistiques
        for (Map.Entry<Integer, Integer> entry : topStats.entrySet()) {
            Paragraph stat = new Paragraph();
            stat.add(new Chunk("\u25CF ", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.ORANGE))); // Cercle
            stat.add(new Chunk("Visite ID : ", boldFont));
            stat.add(new Chunk(String.valueOf(entry.getKey()), textFont));
            stat.add(new Chunk(" — ", textFont));
            stat.add(new Chunk("Demandes : ", boldFont));
            stat.add(new Chunk(String.valueOf(entry.getValue()), textFont));
            stat.setSpacingAfter(10);
            document.add(stat);

            // Ligne de séparation
            LineSeparator separator = new LineSeparator();
            separator.setLineColor(new BaseColor(200, 200, 200));
            document.add(separator);
        }

        document.close();
    }
}