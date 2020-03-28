package com.java.calumjohnston.algorithms;

import com.java.calumjohnston.utilities.cannyEdgeDetection;
import com.java.calumjohnston.utilities.pseudorandom;
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
    int[] coloursToConsider;
    pseudorandom generator;
    int endPositionX;
    int endPositionY;
    int endColourChannel;
    int algorithm;
    int dataLength;

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

        // Get the colours to consider (some combination of red, green and blue)
        boolean red = binaryToInt(parameters.substring(3,4)) == 1;
        boolean green = binaryToInt(parameters.substring(4,5)) == 1;
        boolean blue = binaryToInt(parameters.substring(5,6)) == 1;
        this.coloursToConsider = getColoursToConsider(red, green, blue);

        // Determine whether random embedding is being used
        this.random = binaryToInt(parameters.substring(6, 7)) == 1;
        if (random) {
            String seed = "";
            if(testEnv){
                seed = "Calum";
            }else{
                seed = JOptionPane.showInputDialog("Please select a password for the data");
            }
        }

        // Get end position for data encoding
        if(algorithm != 4) {
            endPositionX = binaryToInt(parameters.substring(7, 22));
            endPositionY = binaryToInt(parameters.substring(22, 37));
            endColourChannel = binaryToInt(parameters.substring(37, 39));
        }else{
            dataLength = binaryToInt(parameters.substring(7,39));
        }
    }

    /**
     * Decodes the parameter data from the image using LSB
     *
     * @return      The binary parameter data that was hidden within the image
     */
    public StringBuilder decodeParameters(){
        ArrayList<int[]> pixelOrder = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        pixelOrder = generateParameterPixelOrder(13, 0);
        coloursToConsider = new int[] {0,1,2};
        int currentPixel = 0;
        boolean complete = false;
        while(!complete){
            int currentX = pixelOrder.get(currentPixel)[0];
            int currentY = pixelOrder.get(currentPixel)[1];
            for(int colourChan : coloursToConsider){
                int colour  = getColourAtPosition(currentX, currentY, colourChan);
                result.append(LSB(colour));
            }
            if(currentX == 12 && currentY == 0){
                complete = true;
            }
            currentPixel++;
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
     * @param colourChannel     The colour channel we are considering (R, G or B)
     * @return                  The value of the colour channel at position (x,y) in image
     */
    public int getColourAtPosition(int x, int y, int colourChannel) {
        int pixel = stegoImage.getRGB(x, y);
        if(colourChannel == 0){
            int red = (pixel & 0x00ff0000) >> 16;
            return red;
        }
        if(colourChannel == 1){
            int green = (pixel & 0x0000ff00) >> 8;
            return green;
        }
        if(colourChannel == 2){
            int blue = pixel & 0x000000ff;
            return blue;
        }
        return -1;
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

    /**
     * Gets the colour channels that will be used
     *
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the green colour channel will be used
     * @param blue          Determines whether the blue colour channel will be used
     * @return              The colours that will be considered when encoding data
     */
    public int[] getColoursToConsider(boolean red, boolean green, boolean blue) {
        if (red && green && blue) {
            return new int[]{0, 1, 2};
        } else if (red && green) {
            return new int[]{0, 1};
        } else if (red && blue) {
            return new int[]{0, 2};
        } else if (blue && green) {
            return new int[]{1, 2};
        } else if (red) {
            return new int[]{0};
        } else if (green) {
            return new int[]{1};
        } else if (blue) {
            return new int[]{2};
        }
        return new int[]{0, 1, 2};
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
        // Determine which encoding scheme to use
        if(algorithm == 0 || algorithm == 1 || algorithm == 4){
            return decodeSingularData();
        }else if(algorithm == 2 || algorithm == 3 || algorithm == 5){
            return decodeDoublyData();
        }
        return new StringBuilder();
    }

    public StringBuilder decodeSingularData() {
        StringBuilder result = new StringBuilder();
        ArrayList<int[]> pixelOrder = new ArrayList<>();
        if(algorithm == 0 || algorithm == 1){
            pixelOrder = generatePixelOrder(1, 13, 0);
        }else if(algorithm == 4){
            cannyEdgeDetection c = new cannyEdgeDetection();
            pixelOrder = c.getEdgePixels(stegoImage, dataLength);
        }
        if(random){
            Collections.shuffle(pixelOrder, new Random("seed".hashCode()));
        }
        int currentPixel = 0;
        boolean complete = false;
        while(!complete){
            int currentX = pixelOrder.get(currentPixel)[0];
            int currentY = pixelOrder.get(currentPixel)[1];
            for(int colourChan : coloursToConsider){
                int colour  = getColourAtPosition(currentX, currentY, colourChan);
                if(algorithm == 0 || algorithm == 4){ //LSB
                    result.append(LSB(colour));
                }else if(algorithm == 1){ //LSBM
                    result.append(LSB(colour));
                }
                if(result.length() >= dataLength){
                    complete = true; break;
                }

            }
            currentPixel++;
        }
        return result;
    }

    public StringBuilder decodeDoublyData(){
        StringBuilder result = new StringBuilder();
        ArrayList<int[]> pixelOrder = new ArrayList<>();
        if(algorithm == 2 || algorithm == 3 || algorithm == 5){
            pixelOrder = generatePixelOrder(2, 13, 0);
        }else if(algorithm == 4){
            cannyEdgeDetection c = new cannyEdgeDetection();
            pixelOrder = c.getEdgePixels(stegoImage, dataLength);
        }
        if(random){
            Collections.shuffle(pixelOrder, new Random("seed".hashCode()));
        }
        int currentPixel = 0;
        boolean complete = false;
        while(!complete){
            int currentX = pixelOrder.get(currentPixel)[0];
            int currentY = pixelOrder.get(currentPixel)[1];
            int nextX = (currentX + 1) % stegoImage.getWidth();
            int nextY = ((nextX == 0) ? currentY + 1 : currentY);
            for(int colourChan : coloursToConsider){
                int firstColour  = getColourAtPosition(currentX, currentY, colourChan);
                int secondColour = getColourAtPosition(nextX, nextY, colourChan);
                if(algorithm == 2 || algorithm == 4){
                    result.append(LSB(firstColour));
                    result.append(LSB((firstColour / 2) + secondColour));
                }else if(algorithm == 3){
                    result.append(PVD(firstColour, secondColour));
                }else if(algorithm == 5){
                    result.append(AELSB(firstColour, secondColour));
                }
                if(result.length() >= dataLength){
                    complete = true; break;
                }
            }
            currentPixel++;
        }
        return result;
    }


    public String LSB(int colour){
        if(colour % 2 == 0){
            return "0";
        }else{
            return "1";
        }
    }

    public String PVD(int firstColour, int secondColour){
        int d = secondColour - firstColour;

        int[] decodingData = quantisationRangeTable(Math.abs(d));

        // Calculate the quantisation range width, then the number of bits to encode
        int width = decodingData[1] - decodingData[0] + 1;
        int t = (int)Math.floor(Math.log(width)/Math.log(2.0));

        // DETERMINE WHETHER DATA EMBEDDING HAS OCCURRED
        int[] newColours = updateColoursPVD(firstColour, secondColour, d, decodingData[1]);

        if(newColours[0] < 0 || newColours[0] > 255 || newColours[1] < 0 || newColours[1] > 255) {
            int a = 2;
        }else{
            int b = Math.abs(d) - decodingData[0];
            return conformBinaryLength(b, t);
        }
        return "";
    }

    public String AELSB(int firstColour, int secondColour){
        int d = firstColour - secondColour;
        int bits = rangeDivision(Math.abs(d));
        return conformBinaryLength((firstColour % (int) Math.pow(2, bits)), bits) + conformBinaryLength((secondColour % (int) Math.pow(2, bits)), bits);
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
