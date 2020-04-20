package testingEnv;

import com.java.calumjohnston.algorithms.decodeData;
import com.java.calumjohnston.algorithms.encodeData;

import java.awt.image.BufferedImage.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.Buffer;
import java.security.SecureRandom;


/**
 * Runs tests on the various algorithms in the program
 */
public class batchGen {

    /**
     * Constructor for the class
     */
    public batchGen() {
        runTests();
    }

    /**
     * Runs the tests
     */
    public void runTests() {

        encodeData encoder = new encodeData();
        decodeData decoder = new decodeData();

        int algorithm = 0;
        String seed = "seed";
        String text = generateText((((512*512) - 32) / 8));

        try {

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("src/testingEnv/payloads_" + algorithm + ".txt"), "UTF-8"));
            for(int i = 1; i < 10001; i++){
                System.out.println(i);
                BufferedImage coverImage = null;
                String image_name = i + ".png";
                try{
                    coverImage = ImageIO.read(new File("C:\\Users\\Calum\\Documents\\Projects\\Dissertation\\Dissertation Project\\src\\testingEnv\\testdata\\" + image_name));
                }catch(Exception e){
                }

                String subText = randomSubString(text, (((512*512) - 32) / 8));

                writer.println(subText.length());

                BufferedImage stegoImage = encoder.encode(deepCopy(coverImage), algorithm, true, seed, subText);
                writeImageFile(stegoImage, Integer.toString(i), algorithm);
            }
            writer.close();
        }catch(Exception e){ }
    }

    public static void main(String[] args){
        batchGen t = new batchGen();
    }




    public BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public void writeImageFile(BufferedImage image, String name, int algorithm) {
        try {
            // Writes file to the disk (w/extension of algorithm used)
            File outputFile = new File("src/testingEnv/testdata_" +  algorithm + "/" + name + "_encoded.png");
            ImageIO.write(image, "png", outputFile);

            // Debugging purposes
            System.out.println("Successfully written file to disk");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to write file to disk");
        }
    }

    public String generateText(int maxLength){
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < maxLength; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    public String randomSubString(String text, int maxLength){
        SecureRandom rnd = new SecureRandom();
        return text.substring(0, rnd.nextInt(maxLength));
    }

    /**public BufferedImage convert(BufferedImage src){
        BufferedImage img = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(src, 0, 0,null);
        g2d.dispose();
        return img;
    }*/

}
