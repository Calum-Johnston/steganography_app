package com.java.calumjohnston.algorithms;

import com.java.calumjohnston.algorithms.techniques.*;
import com.java.calumjohnston.utilities.cannyEdgeDetection;
import com.java.calumjohnston.utilities.pseudorandom;
import com.java.calumjohnston.utilities.sobelEdgeDetection;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Decode Class: This class implements the extraction of data from an image
 * that has been embedded using the one of several LSB techniques
 */
public class decodeData {

    BufferedImage stegoImage;
    boolean random;
    pseudorandom generator;
    int algorithm;
    int dataLength;
    int threshold;

    /**
     * Constructor
     */
    public decodeData(){

    }

    /**
     * Acts as central controller to decoding functions
     *
     * @param stegoImage    The image we will decode data from
     * @param testEnv       Describes what environment the program is running in
     * @return              The data to be hidden within the image
     */
    public String decode(BufferedImage stegoImage, boolean testEnv){
        // Setup data to be used for decoding
        setupData(stegoImage, testEnv);

        // Check parameter data will allow for successful decoding
        checkData();

        // Decode data from the image
        StringBuilder binary = decodeSecretData();

        // Convert the data back to text
        String text = getText(binary);

        // Return the retrieved text
        return text;
    }



    // ======= SETUP & PARAMETER FUNCTIONS =======
    /**
     * Sets up the initial data required for encoding
     * @param stegoImage    The image we will decode data from
     * @param testEnv       Describes what environment the program is running in
     */
    public void setupData(BufferedImage stegoImage, boolean testEnv){

        // Define the image we are reading data from
        this.stegoImage = stegoImage;

        // Get parameter binary data
        StringBuilder parameters = decodeParameters();

        // Get the algorithm
        this.algorithm = binaryToInt(parameters.substring(0, 3));

        // Determine whether random embedding is being used
        this.random = binaryToInt(parameters.substring(3, 4)) == 1;
        if (random) {
            String seed = "";
            if(testEnv){
                seed = "seed";
            }else{
                seed = JOptionPane.showInputDialog("Please select a password for the data");
            }
        }

        // Get end position for data encoding
        dataLength = binaryToInt(parameters.substring(4,24));

        // Get threshold (if sobel techique)
        if(algorithm == 6){
            threshold = binaryToInt(parameters.substring(24, 33));
        }

    }

    /**
     * Decodes the parameter data from the image using LSB
     *
     * @return      The binary parameter data that was hidden within the image
     */
    public StringBuilder decodeParameters(){
        StringBuilder result = new StringBuilder();
        ArrayList<int[]> pixelOrder = generateParameterPixelOrder(33, 0);
        int currentPixel = 0;
        while(result.length() < pixelOrder.size()){
            result.append(LSB.decode(stegoImage, pixelOrder.get(currentPixel)));
            currentPixel += 1;
        }
        return result;
    }

    public ArrayList<int[]> generateParameterPixelOrder(int endX, int endY){
        ArrayList<int[]> order = new ArrayList<>();
        int x = 0; int y = 0;
        while(x < endX || y < endY){
            order.add(new int[] {x,y});
            x = (x + 1) % stegoImage.getWidth();
            if(x == 0){
                y += 1;
            }
        }
        return order;
    }

    /**
     * Generates the next pixel position to consider when encoding data
     *
     * @param currentPosition   The position which data has just been encoded
     * @param random                Determines whether random embedding was used
     * @return                  The new position to consider
     */
    public int[] generateNextPosition(int[] currentPosition, boolean random) {
        int imageWidth = stegoImage.getWidth();
        if (random) {
            int position = generator.getNextElement();
            return new int[] {position % imageWidth, position / imageWidth};
        } else {
            int newLine = (currentPosition[0] + 1) % imageWidth;
            if (newLine == 0) {
                return new int[] {0, currentPosition[1] + 1};
            }else{
                return new int[] {currentPosition[0] + 1, currentPosition[1]};
            }
        }
    }

    /**
     * Gets colour channel data from an image at a specific location
     *
     * @param x                 x coordinate of pixel
     * @param y                 y coordinate of pixel
     * @return                  The value of the colour channel at position (x,y) in image
     */
    public int getColourAtPosition(int x, int y) {
        return stegoImage.getRGB(x, y) & 0xFF;
    }

    /**
     * Gets the least significant bit of a colour
     *
     * @param colour        The colour to get the LSB of
     * @return              The LSB of colour
     */
    public int getLSB(int colour){
        if(colour % 2 == 0) {
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * Converts binary input to its integer equivalent
     *
     * @param binary        The binary input (1+ bits)
     * @return              The integer equivalent of binary
     */
    public int binaryToInt(String binary){
        return Integer.parseInt(binary, 2);
    }



    // ======= CHECK FUNCTIONS =======
    public void checkData(){

    }



    // ======= DECODING FUNCTIONS =======
    /**
     * Determines which encoding scheme to use based on the algorithm selected
     *
     * @return        The binary data hidden within the image
     */
    public StringBuilder decodeSecretData(){

        // Pre-process
        StringBuilder result = new StringBuilder();
        ArrayList<int[]> pixelOrder = getPixelOrder(dataLength);
        int currentPixel = 0;
        int update = 1;
        if(algorithm == 0 || algorithm == 1 || algorithm == 3 || algorithm == 4 || algorithm == 5) {
            update = 1;
        }else if(algorithm == 2 || algorithm == 4 || algorithm == 6) {
            update = 2;
        }else if(algorithm == 7){
            update = 50;  // Block Size (+2)
        }

        // Embed
        while(result.length() < dataLength){
            if (algorithm == 0) {  // LSB
                result.append(LSB.decode(stegoImage, pixelOrder.get(currentPixel)));
                currentPixel += 1;
            }
            if (algorithm == 1) {  // LSBM
                result.append(LSBM.decode(stegoImage, pixelOrder.get(currentPixel)));
                currentPixel += 1;
            }
            if (algorithm == 2) { // LSBMR
                result.append(LSBMR.decode(stegoImage, pixelOrder.get(currentPixel), pixelOrder.get(currentPixel + 1)));
                currentPixel += 2;
            }
            if (algorithm == 3) { // PVD
                result.append(PVD.decode(stegoImage, pixelOrder.get(currentPixel)));
                currentPixel += 1;
            }
            if (algorithm == 4) { // Canny LSB
                result.append(LSB_2.decode(stegoImage, pixelOrder.get(currentPixel)));
                currentPixel += 1;
            }
            if (algorithm == 5) { // AE-LSB
                result.append(AELSB.decode(stegoImage, pixelOrder.get(currentPixel)));
                currentPixel += 1;
            }
            if (algorithm == 6) { // Sobel LSBMR
                result.append(LSBMR.decode(stegoImage, pixelOrder.get(currentPixel), pixelOrder.get(currentPixel + 1)));
                currentPixel += 2;
            }
            if (algorithm == 7) { // Canny LSBMR (Proposed
                if(result.length() + 50 < dataLength){
                    ArrayList<int[]> subsetOrder = new ArrayList<>(pixelOrder.subList(currentPixel, currentPixel + 52));
                    result.append(CannyLSBMR.decode(stegoImage, subsetOrder));
                }else{
                    ArrayList<int[]> subsetOrder = new ArrayList<>(pixelOrder.subList(currentPixel, currentPixel + dataLength - result.length() + 2));
                    result.append(CannyLSBMR.decode(stegoImage, subsetOrder));
                }
                currentPixel += 52;
            }
        }

        return result;
    }





    public ArrayList<int[]> getPixelOrder(int binaryLength){
        ArrayList<int[]> pixelOrder = new ArrayList<>();
        if(algorithm == 0 || algorithm == 1 || algorithm == 2){
            pixelOrder = generatePixelOrder(1, 33, 0);
        }else if(algorithm == 3 || algorithm == 5) {
            pixelOrder = generatePixelOrder(2, 33, 0);
        }else if(algorithm == 6){
            sobelEdgeDetection s = new sobelEdgeDetection();
            pixelOrder = s.getEdgePixels(stegoImage, binaryLength, threshold);
        }else if(algorithm == 4 || algorithm == 7){
            cannyEdgeDetection c = new cannyEdgeDetection();
            int requiredPixels = binaryLength + ((int)(Math.ceil(binaryLength / 50)) * 2);
            pixelOrder = c.getEdgePixels(stegoImage, requiredPixels, "seed");
        }
        if(random){
            if(algorithm != 4 && algorithm != 7) {
                Collections.shuffle(pixelOrder, new Random("seed".hashCode()));
            }
        }
        return pixelOrder;
    }

    public ArrayList<int[]> generatePixelOrder(int increment, int startX, int startY){
        ArrayList<int[]> order = new ArrayList<>();
        int x = startX; int y = startY;
        int prevX = 0;
        while(x < stegoImage.getWidth() && y < stegoImage.getHeight()){
            order.add(new int[] {x,y});
            x = (x + increment) % stegoImage.getWidth();
            if(prevX > x){
                y++;
            }
            prevX = x;
        }
        return order;
    }



    /**
     * Inserts data into two colours by modifying their difference
     *
     * @param firstColour       First colour to be manipulated
     * @param secondColour      Second colour to be manipulated
     * @param d                 The difference between the colours
     * @param d1                The new difference calculated from the data to be inserted
     * @return                  Updated colours
     */
    public int[] updateColoursPVD(int firstColour, int secondColour, int d, int d1){
        double m = d1 - d;

        // Obtain new colour values for firstColour and secondColour by averaging new difference to them
        if (Math.abs(d) % 2 == 1) {
            firstColour -= (int) Math.ceil(m/2);
            secondColour += (int) Math.floor(m/2);
        } else {
            firstColour -= (int) Math.floor(m/2);
            secondColour += (int) Math.ceil(m/2);
        }

        return new int[] {firstColour, secondColour};
    }

    /**
     * Returns the range the difference of two values sits in (for PVD)
     *
     * @param difference    The difference between two consecutive pixel values
     * @return              The data required for encoding
     */
    public int[] quantisationRangeTable(int difference){
        if(difference <= 7){
            return new int[] {0, 7};
        }
        if(difference <= 15){
            return new int[] {8, 15};
        }
        if(difference <= 31){
            return new int[] {16, 31};
        }
        if(difference <= 63){
            return new int[] {32, 63};
        }
        if(difference <= 127){
            return new int[] {64, 127};
        }
        if(difference <= 255){
            return new int[] {128, 255};
        }
        return null;
    }

    /**
     * Converts some integer to binary of a defined length
     *
     * @param data      The data to be converted to binary
     * @param length    The number of bits to represent the data as
     * @return          The binary equivalent of data
     */
    public String conformBinaryLength(int data, int length){
        String binaryParameter = Integer.toBinaryString(data);
        binaryParameter = (StringUtils.repeat('0', length) + binaryParameter).substring(binaryParameter.length());
        return binaryParameter;
    }

    public int rangeDivision(int difference){
        if(difference <= 15){
            return 3;
        }else if(difference <= 31){
            return 4;
        }else{
            return 5;
        }
    }



    // ======= CONVERSION FUNCTIONS =======
    /**
     * Converts the binary stream of data to it's ASCII equivalent
     *
     * @param binaryText    Binary stream of data
     * @return              ASCII representation of binary data
     */
    public String getText(StringBuilder binaryText){
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binaryText.length(); i += 8) {
            try {
                String binaryData = binaryText.substring(i, i + 8);
                char characterData = (char) Integer.parseInt(binaryData, 2);
                text.append(characterData);
            } catch (Exception e) {
            }
        }
        return text.toString();
    }
}
