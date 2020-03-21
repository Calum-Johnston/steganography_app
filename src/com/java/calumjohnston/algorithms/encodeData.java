package com.java.calumjohnston.algorithms;

import com.java.calumjohnston.exceptions.DataOverflowException;
import com.java.calumjohnston.utilities.cannyEdgeDetection;
import com.java.calumjohnston.utilities.pseudorandom;
import com.java.calumjohnston.utilities.cannyEdgeDetection;
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
    pseudorandom generator;
    int[] coloursToConsider;
    int endPositionX;
    int endPositionY;
    int endColourChannel;
    int algorithm;
    boolean random;
    int lowThresh;
    int highThresh;

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
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the green colour channel will be used
     * @param blue          Determines whether the blue colour channel will be used
     * @param random        Determines whether random embedding has been used
     * @param seed          The seed used to initialise the PRNG (if used)
     * @param text          The data to be hidden within the image
     * @return              The image with the hidden data embedded into it
     */
    public BufferedImage encode(BufferedImage coverImage, int algorithm, boolean red, boolean green, boolean blue,
                                boolean random, String seed, String text){
        // Setup data to be used for encoding
        setupData(coverImage, algorithm, red, green, blue, random, seed);

        // Get the binary data to encode
        StringBuilder binary = getBinaryText(text);

        // Encode data to the image
        encodeSecretData(binary);

        // Encode parameters to the image
        encodeParameterData(red, green, blue);

        // Return the manipulated image
        return coverImage;
    }



    // ======= SETUP FUNCTIONS =======
    /**
     * Sets up the initial data required for encoding
     * @param coverImage    The image we will encode data into
     * @param algorithm     The algorithm being used for encoding
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the green colour channel will be used
     * @param blue          Determines whether the blue colour channel will be used
     * @param random        Determines whether random embedding has been used
     * @param seed          The seed used to initialise the PRNG (if used)
     */
    public void setupData(BufferedImage coverImage, int algorithm, boolean red, boolean green, boolean blue,
                          boolean random, String seed){

        // Define the image we are embedding data into
        this.coverImage = coverImage;

        // Define the algorithm being used
        this.algorithm = algorithm;

        // Get the colours to consider (some combination of red, green and blue)
        coloursToConsider = getColoursToConsider(red, green, blue);

        // Determine whether random embedding is being used
        this.random = random;
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
        // Determine which encoding scheme to use
        if(algorithm <= 1){
            encodeSingularData(binary);
        }else if(algorithm == 2 || algorithm == 3 || algorithm == 4){
            encodeDoublyData(binary);
        }
    }



    public void encodeSingularData(StringBuilder binary){
        ArrayList<int[]> pixelOrder = new ArrayList<>();
        if(algorithm == 0 || algorithm == 1){
            pixelOrder = generatePixelOrder(1, 13, 0);
            if(random){
                Collections.shuffle(pixelOrder, new Random("seed".hashCode()));
            }
        }
        int currentPixel = 0;
        int currentBit = 0;
        boolean complete = false;
        while(!complete){
            int currentX = pixelOrder.get(currentPixel)[0];
            int currentY = pixelOrder.get(currentPixel)[1];
            for(int colourChan : coloursToConsider){
                char data = binary.charAt(currentBit);
                int colour  = getColourAtPosition(currentX, currentY, colourChan);
                int newColour = 0;
                if(algorithm == 0){
                    newColour = LSB(data, colour);
                }else if(algorithm == 1){
                    newColour = LSBM(data, colour);
                }
                writeColourAtPosition(currentX, currentY, colourChan, newColour);
                currentBit += 1;
                if(currentBit == binary.length()){
                    endPositionX = currentX;
                    endPositionY = currentY;
                    endColourChannel = colourChan;
                    complete = true;
                    break;
                }
            }
            currentPixel++;
        }
    }

    public void encodeDoublyData(StringBuilder binary){
        ArrayList<int[]> pixelOrder;
        if(algorithm == 2 || algorithm == 3){
            pixelOrder = generatePixelOrder(2, 13, 0);
        }else{
            cannyEdgeDetection c = new cannyEdgeDetection();
            pixelOrder = c.getEdgePixels(coverImage, binary);
        }
        if(random){
            Collections.shuffle(pixelOrder, new Random("seed".hashCode()));
        }
        int currentPixel = 0;
        int currentBit = 0;
        boolean complete = false;
        while(!complete){
            int currentX = pixelOrder.get(currentPixel)[0];
            int currentY = pixelOrder.get(currentPixel)[1];
            int nextX = (currentX + 1) % coverImage.getWidth();
            int nextY = ((currentY == 0) ? currentY : currentY + 1);
            for(int colourChan : coloursToConsider){
                char firstData = binary.charAt(currentBit);
                char secondData = binary.charAt(currentBit + 1);
                int firstColour  = getColourAtPosition(currentX, currentY, colourChan);
                int secondColour = getColourAtPosition(nextX, nextY, colourChan);
                int[] newData = new int[3];
                if(algorithm == 2){
                    newData = LSBMR(firstData, secondData, firstColour, secondColour);
                }else if(algorithm == 3 || algorithm == 4){
                    newData = PVD(firstColour, secondColour, binary, currentBit);
                }
                writeColourAtPosition(currentX, currentY, colourChan, newData[1]);
                writeColourAtPosition(nextX, nextY, colourChan, newData[2]);
                currentBit += newData[0];
                if(currentBit >= binary.length()){
                    endPositionX = currentX;
                    endPositionY = currentY;
                    endColourChannel = colourChan;
                    complete = true;
                    break;
                }
            }
            currentPixel++;
        }
    }

    public int LSB(char data, int colour){
        if(colour % 2 != Character.getNumericValue(data)){
            if(colour % 2 == 0){
                colour++;
            }else{
                colour--;
            }
        }
        return colour;
    }

    public int LSBM(char data, int colour){
        if(colour % 2 != Character.getNumericValue(data)) {
            if (ThreadLocalRandom.current().nextInt(0, 2) < 1) {
                colour--;
                if(colour < 0){
                    colour = 1;
                }
            } else {
                colour += 1;
                if(colour > 255){
                    colour = 254 ;
                }
            }
        }
        return colour;
    }

    public int[] LSBMR(char firstData, char secondData, int firstColour, int secondColour){

        // Get the binary relationships between the firstColour and secondColour
        int pixel_Relationship = (int) Math.floor(firstColour / 2) + secondColour;
        String LSB_Relationship = getLSB(pixel_Relationship);
        int pixel_Relationship_2;
        if(firstColour == 0){
            // Fix for 0 - 1 / 2 giving 0 instead of -0.5
            pixel_Relationship_2 = -1 + secondColour;
        }else{
            pixel_Relationship_2 = (int) Math.floor((firstColour - 1) / 2) + secondColour;
        }
        String LSB_Relationship_2 = getLSB(pixel_Relationship_2);

        // Get LSBs of pixel colour
        String firstColourLSB = getLSB(firstColour);

        // Determines what to embed based on LSBss and relationship between them
        // Could add an update LSB function here too!! (for pixelData!!)
        if (Character.toString(firstData).equals(firstColourLSB)) {
            if(!(Character.toString(secondData).equals(LSB_Relationship))) {
                if (ThreadLocalRandom.current().nextInt(0, 2) < 1) {
                    secondColour -= 1;
                    if(secondColour == -1){
                        secondColour = 255;
                    }
                } else {
                    secondColour += 1;
                    if(secondColour == 256){
                        secondColour = 0;
                    }
                }
            }
        } else if (!(Character.toString(firstData).equals(firstColourLSB))) {
            if (Character.toString(secondData).equals(LSB_Relationship_2)) {
                firstColour -= 1;
                if(firstColour == -1){
                    firstColour = 255;
                }
            } else {
                firstColour += 1;
                if(firstColour == 256){
                    firstColour = 0;
                }
            }
        }
        return new int[] {2, firstColour, secondColour};
    }

    public int[] PVD(int firstColour, int secondColour, StringBuilder binary, int currentPos){

        // Calculate pixel difference
        int d = secondColour - firstColour;

        // Get the range data for this colour interval
        int[] encodingData = quantisationRangeTable(Math.abs(d));

        // Calculate the quantisation range width, then the number of bits to encode
        int width = encodingData[1] - encodingData[0] + 1;
        int n = (int)Math.floor(Math.log(width)/Math.log(2.0));

        // Get the data to encode into the image (ensuring we don't go out of range)
        String data = "";
        if(currentPos + n > binary.length()){
            data = binary.substring(currentPos);
            while(data.length() < n){
                data = data + "0";
            }
        }else{
            data = binary.substring(currentPos, currentPos + n);
        }
        int decVal = Integer.parseInt(data, 2);

        // CHECK RANGE
        int[] newColours = updateColoursPVD(firstColour, secondColour, d, encodingData[1]);

        if(newColours[0] < 0 || newColours[0] > 255 || newColours[1] < 0 || newColours[1] > 255){
            data = "";
            return new int[] {data.length(), firstColour, secondColour};
        }else {

            // Calculate embedding values
            int d1;
            if(d >= 0){ d1 = encodingData[0] + decVal; }
            else{ d1 = -(encodingData[0] + decVal); }

            // Encode the data
            newColours = updateColoursPVD(firstColour, secondColour, d, d1);

            // Write colour data back to the image
            return new int[] {data.length(), newColours[0], newColours[1]};
        }
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

    /**
     * Gets colour channel data from an image at a specific location
     *
     * @param x                 x coordinate of pixel
     * @param y                 y coordinate of pixel
     * @param colourChannel     The colour channel we are considering (R, G or B)
     * @return                  The value of the colour channel at position (x,y) in image
     */
    public int getColourAtPosition(int x, int y, int colourChannel) {
        int pixel = coverImage.getRGB(x, y);
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
     * Writes colour channel data to an image at a specific location
     *
     * @param x                 x coordinate of pixel
     * @param y                 y coordinate of pixel
     * @param colourChannel     The colour channel we are considering (R, G or B)
     * @param data              The data to be written
     */
    public void writeColourAtPosition(int x, int y, int colourChannel, int data){
        int pixel = coverImage.getRGB(x, y);
        int red = (pixel & 0x00ff0000) >> 16;
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;
        if(colourChannel == 0){
            red = data;
        }
        if(colourChannel == 1){
            green = data;
        }
        if(colourChannel == 2){
            blue = data;
        }
        Color color = new Color(red, green, blue);
        coverImage.setRGB(x, y, color.getRGB());
    }


    /**
     * Gets the least significant bit of a colour
     *
     * @param colour        The colour to get the LSB of
     * @return              The LSB of colour
     */
    public String getLSB(int colour){
        if(colour % 2 == 0) {
            return "0";
        }else{
            return "1";
        }
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





    // ======= PARAMETER ENCODING =======
    /**
     * Converts parameter data to binary and writes to image
     *
     * @param red       Determines whether the red colour channel has been used
     * @param green     Determines whether the green colour channel has been used
     * @param blue      Determines whether the blue colour channel has been used
     */
    public void encodeParameterData(boolean red, boolean green, boolean blue){
        StringBuilder parameters = new StringBuilder();
        parameters.append(conformBinaryLength(algorithm, 3));
        parameters.append(conformBinaryLength(red ? 1 : 0, 1));
        parameters.append(conformBinaryLength(green ? 1 : 0, 1));
        parameters.append(conformBinaryLength(blue ? 1 : 0, 1));
        parameters.append(conformBinaryLength(random ? 1 : 0, 1));
        parameters.append(conformBinaryLength(endPositionX, 15));
        parameters.append(conformBinaryLength(endPositionY, 15));
        parameters.append(conformBinaryLength(endColourChannel, 2));
        if(algorithm == 4){
            conformBinaryLength(lowThresh, 16);
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
        pixelOrder = generateParameterPixelOrder(13, 0);
        int[] coloursToConsider = new int[] {0, 1, 2};
        int currentPixel = 0;
        int currentBit = 0;
        boolean complete = false;
        while(!complete){
            int currentX = pixelOrder.get(currentPixel)[0];
            int currentY = pixelOrder.get(currentPixel)[1];
            for(int colourChan : coloursToConsider){
                char data = parameters.charAt(currentBit);
                int colour  = getColourAtPosition(currentX, currentY, colourChan);
                int newColour = LSB(data, colour);
                writeColourAtPosition(currentX, currentY, colourChan, newColour);
                currentBit += 1;
                if(currentBit == parameters.length()){
                    complete = true;
                    break;
                }
            }
            currentPixel++;
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

}
