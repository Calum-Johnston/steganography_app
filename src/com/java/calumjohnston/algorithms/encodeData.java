package com.java.calumjohnston.algorithms;

import com.java.calumjohnston.algorithms.techniques.*;
import com.java.calumjohnston.utilities.cannyEdgeDetection;
import com.java.calumjohnston.utilities.sobelEdgeDetection;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Encode Class: This class implements the embedding of data into an image
 * using one of several LSB techniques
 */
public class encodeData {

    BufferedImage coverImage;
    int algorithm;
    boolean random;
    double threshold;

    cannyEdgeDetection c;
    sobelEdgeDetection s;

    /**
     * Constructor
     */
    public encodeData(){

    }

    /**
     * Acts as central controller to encoding functions
     *
     * @param coverImage    The image we will encode data into
     * @param algorithm     The algorithm being used for encoding
     * @param random        Determines whether random embedding has been used
     * @param seed          The seed used to initialise the PRNG (if used)
     * @param text          The data to be hidden within the image
     * @return              The image with the hidden data embedded into it
     */
    public BufferedImage encode(BufferedImage coverImage, int algorithm, boolean random, String seed, String text){
        // Setup data to be used for encoding
        setupData(coverImage, algorithm, random, seed);

        // Get the binary data to encode
        StringBuilder binary = getBinaryText(text);

        // Encode data to the image
        encodeSecretData(binary);

        // Encode parameters to the image
        encodeParameterData(binary.length());

        // Return the manipulated image
        return coverImage;
    }



    // ======= SETUP FUNCTIONS =======
    /**
     * Sets up the initial data required for encoding
     * @param coverImage    The image we will encode data into
     * @param algorithm     The algorithm being used for encoding
     * @param random        Determines whether random embedding has been used
     * @param seed          The seed used to initialise the PRNG (if used)
     */
    public void setupData(BufferedImage coverImage, int algorithm, boolean random, String seed){

        // Define the image we are embedding data into
        this.coverImage = coverImage;

        // Define the algorithm being used
        this.algorithm = algorithm;

        // Determine whether random embedding is being used
        this.random = random;

        // Setup edge detection technique
        if(algorithm == 4 || algorithm == 7){
            c = new cannyEdgeDetection();
        }
        if(algorithm == 6){
            s = new sobelEdgeDetection();
        }
    }

    /**
     * Converts character data (ASCII) into its binary equivalent
     *
     * @param text      The character data to be converted
     * @return          Binary equivalent of text
     */
    public StringBuilder getBinaryText(String text) {
        byte[] bytes = text.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            String binaryData = Integer.toBinaryString(b);
            String formatted = ("00000000" + binaryData).substring(binaryData.length());
            binary.append(formatted);
        }
        return binary;
    }



    // ======= ENCODING FUNCTIONS =======
    /**
     * Determines which encoding scheme to use based on the algorithm selected
     *
     * @param binary        The binary data to be hidden within the image
     */
    public void encodeSecretData(StringBuilder binary){

        // Pre-process
        ArrayList<int[]> pixelOrder = getPixelOrder(binary);
        int currentPixel = 0;
        int update = 1;
        if(algorithm == 0 || algorithm == 1 || algorithm == 3 || algorithm == 4 || algorithm == 5) {
            update = 1;
        }else if(algorithm == 2 || algorithm == 6) {
            update = 2;
        }else if(algorithm == 7){
            update = 50;  // Block Size (+2)
        }

        // Embed
        for(int i = 0; i < binary.length(); i += update) {
            if (algorithm == 0) {  // LSB
                LSB.encode(coverImage, pixelOrder.get(currentPixel), binary.charAt(i));
                currentPixel += 1;
            }
            if (algorithm == 1) {  // LSBM
                LSBM.encode(coverImage, pixelOrder.get(currentPixel), binary.charAt(i));
                currentPixel += 1;
            }
            if (algorithm == 2) { // LSBMR
                LSBMR.encode(coverImage, pixelOrder.get(currentPixel), pixelOrder.get(currentPixel + 1), binary.charAt(i), binary.charAt(i + 1));
                currentPixel += 2;
            }
            if (algorithm == 3) { // PVD
                try {
                    update = PVD.encode(coverImage, pixelOrder.get(currentPixel), binary.substring(i, i + 7));
                } catch (Exception e) {
                    update = PVD.encode(coverImage, pixelOrder.get(currentPixel), binary.substring(i));
                }
                currentPixel += 1;
            }
            if (algorithm == 4) { // Canny LSB
                LSB.encode(coverImage, pixelOrder.get(currentPixel), binary.charAt(i));
                currentPixel += 1;
            }
            if (algorithm == 5) { // AE-LSB
                try {
                    update = AELSB.encode(coverImage, pixelOrder.get(currentPixel), binary.substring(i, i + 10));
                } catch (Exception e) {
                    update = AELSB.encode(coverImage, pixelOrder.get(currentPixel), binary.substring(i));
                }
                currentPixel += 1;
            }
            if (algorithm == 6) { // Sobel LSBMR
                LSBMR.encode(coverImage, pixelOrder.get(currentPixel), pixelOrder.get(currentPixel + 1), binary.charAt(i), binary.charAt(i + 1));
                currentPixel += 2;
            }
            if (algorithm == 7) { // Canny LSBMR (Proposed
                try {
                    ArrayList<int[]> subsetOrder = new ArrayList<>(pixelOrder.subList(currentPixel, currentPixel + 52));
                    StringBuilder subsetData = new StringBuilder(binary.substring(i, i + 50));
                    CannyLSBMR.encode(coverImage, subsetOrder, subsetData);
                } catch (Exception e) {
                    ArrayList<int[]> subsetOrder = new ArrayList<>(pixelOrder.subList(currentPixel, currentPixel + binary.length() - i + 2));
                    StringBuilder subsetData = new StringBuilder(binary.substring(i));
                    CannyLSBMR.encode(coverImage, subsetOrder, subsetData);
                }
                currentPixel += 52;
            }
        }

        // Post-process
        if(algorithm == 6){
            threshold = s.adjustGradients(coverImage, binary.length());
        }
    }



    public ArrayList<int[]> getPixelOrder(StringBuilder binary){
        ArrayList<int[]> pixelOrder = new ArrayList<>();
        if(algorithm == 0 || algorithm == 1 || algorithm == 2){
            pixelOrder = generatePixelOrder(1, 33, 0);
        }else if(algorithm == 3 || algorithm == 5) {
            pixelOrder = generatePixelOrder(2, 33, 0);
        }else if(algorithm == 6){
            pixelOrder = s.getEdgePixels(coverImage, binary.length());
        }else if(algorithm == 4 || algorithm == 7){
            pixelOrder = c.getEdgePixels(coverImage, binary.length());
        }
        if(random){
            Collections.shuffle(pixelOrder, new Random("seed".hashCode()));
        }
        return pixelOrder;
    }

    public ArrayList<int[]> generatePixelOrder(int increment, int startX, int startY){
        ArrayList<int[]> order = new ArrayList<>();
        int x = startX; int y = startY;
        int prevX = 0;
        while(x < coverImage.getWidth() && y < coverImage.getHeight()){
            order.add(new int[] {x,y});
            x = (x + increment) % coverImage.getWidth();
            if(prevX > x){
                y++;
            }
            prevX = x;
        }
        return order;
    }








    // ======= PARAMETER ENCODING =======
    /**
     * Converts parameter data to binary and writes to image
     *
     * @param binaryLength      The amount of data embedded
     */
    public void encodeParameterData(int binaryLength){
        StringBuilder parameters = new StringBuilder();
        parameters.append(util.conformBinaryLength(algorithm, 3));
        parameters.append(util.conformBinaryLength(random ? 1 : 0, 1));
        parameters.append(util.conformBinaryLength(binaryLength, 20));
        if(algorithm == 6){
            parameters.append(util.conformBinaryLength((int) threshold, 9));
        }
        encodeParameters(parameters);
    }

    /**
     * Encodes the parameter data into the image using LSB
     *
     * @param parameters    The binary parameter data to be hidden within the image
     */
    public void encodeParameters(StringBuilder parameters){
        ArrayList<int[]> pixelOrder = new ArrayList<>();
        pixelOrder = generateParameterPixelOrder(33, 0);
        int currentPixel = 0;
        for(int i = 0; i < parameters.length(); i++){
            LSB.encode(coverImage, pixelOrder.get(currentPixel), parameters.charAt(i));
            currentPixel += 1;
        }
    }

    public ArrayList<int[]> generateParameterPixelOrder(int endX, int endY){
        ArrayList<int[]> order = new ArrayList<>();
        int x = 0; int y = 0;
        while(x < endX || y < endY){
            order.add(new int[] {x,y});
            x = (x + 1) % coverImage.getWidth();
            if(x == 0){
                y += 1;
            }
        }
        return order;
    }
}
