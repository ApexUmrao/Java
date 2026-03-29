package apexHack;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class GenerateTestImage {
    public static void main(String[] args) {
        try {
            // Create a 600x200 blank image
            BufferedImage img = new BufferedImage(600, 200, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();

            // Fill background with white
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, 600, 200);

            // Draw crisp black text
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // The text we want Tesseract to read
            g2d.drawString("Translate this text", 80, 110);
            g2d.dispose();

            // Save as PNG
            File file = new File("sample.png");
            ImageIO.write(img, "png", file);

            System.out.println("Success! Created: " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
