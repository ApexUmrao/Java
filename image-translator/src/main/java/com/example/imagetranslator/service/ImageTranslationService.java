package com.example.imagetranslator.service;

import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ImageTranslationService {

    private final ChatClient chatClient;
    private final String tessDataPath;

    public ImageTranslationService(ChatClient.Builder chatClientBuilder,
                                   @Value("${tesseract.datapath}") String tessDataPath) {
        this.chatClient = chatClientBuilder.build();
        this.tessDataPath = tessDataPath;
    }

    public byte[] translateImageText(MultipartFile file, String targetLanguage) throws IOException, TesseractException {
        // 1. Read the image
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new IllegalArgumentException("Invalid image file");
        }

        // 2. Setup Tesseract OCR
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage("eng"); // Assuming source is English

        // 3. Extract words and their bounding boxes
        // ITessAPI.TessPageIteratorLevel.RIL_WORD gets bounding boxes for individual words
        List<Word> words = tesseract.getWords(image, ITessAPI.TessPageIteratorLevel.RIL_WORD);

        // 4. Setup Graphics2D to modify the image
        Graphics2D g2d = image.createGraphics();

        // Anti-aliasing for smoother text
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (Word word : words) {
            String originalText = word.getText().trim();
            if (originalText.isEmpty()) continue;

            // Translate using Spring AI
            String translatedText = translateText(originalText, targetLanguage);

            Rectangle rect = word.getBoundingBox();

            // Mask the original text (Simplified: drawing a white box over it)
            // Note: A production app would sample the background color of the surrounding pixels
            g2d.setColor(Color.WHITE);
            g2d.fillRect(rect.x, rect.y, rect.width, rect.height);

            // Draw the translated text
            g2d.setColor(Color.BLACK);
            // Calculate a rough font size based on bounding box height
            Font font = new Font("Arial", Font.PLAIN, (int) (rect.height * 0.8));
            g2d.setFont(font);

            // Draw string slightly above the bottom line of the bounding box
            g2d.drawString(translatedText, rect.x, rect.y + rect.height - (rect.height / 4));
        }

        g2d.dispose();

        // 5. Convert back to byte array to return via API
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    private String translateText(String text, String targetLanguage) {
        String prompt = String.format("Translate the following word/phrase to %s. Provide ONLY the translation, no extra words or punctuation: %s", targetLanguage, text);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content()
                .trim();
    }
}