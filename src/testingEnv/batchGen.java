package testingEnv;

import com.java.calumjohnston.algorithms.decodeData;
import com.java.calumjohnston.algorithms.encodeData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.security.SecureRandom;

import static org.junit.Assert.assertEquals;


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

        String text = "";
        boolean random = true;

        double[] values = new double[] {0.05, 0.1, 0.2, 0.3, 0.4, 0.5};
        String[] names = new String[] {"005", "010", "020", "030", "040","050"};
        int[] algorithms = new int[] {1};

        try {
            for (int algorithm : algorithms) {
                for (int a = 0; a < names.length; a++) {
                    double bpp = values[a] * (512 * 512);
                    for (int i = 1; i < 2000; i++) {
                        BufferedImage coverImage = null;
                        String image_name = i + ".png";
                        try {
                            coverImage = ImageIO.read(new File("C:\\Users\\Calum\\Documents\\Projects\\Dissertation\\Dataset\\testdata\\" + image_name));
                        } catch (Exception e) {
                        }
                        text = generateText((int) bpp / 8);
                        System.out.println(algorithm + " " +  values[a] + " " + i + " " + text);
                        String seed = generateText(10);
                        BufferedImage stegoImage = encoder.encode(deepCopy(coverImage), algorithm, random, seed, text);
                        writeImageFile(stegoImage, Integer.toString(i), algorithm, names[a]);
                    }
                }
            }
        }catch(Exception e){
        }
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

    public void writeImageFile(BufferedImage image, String name, int algorithm, String bpp) {
        try {
            // Writes file to the disk (w/extension of algorithm used)
            File outputFile = new File("C:\\Users\\Calum\\Documents\\Projects\\Dissertation\\Dataset\\stego_" +  algorithm + "_" + bpp + "\\" + name + ".png");
            ImageIO.write(image, "png", outputFile);

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
