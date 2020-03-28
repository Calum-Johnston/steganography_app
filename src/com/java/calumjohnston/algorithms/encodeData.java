package com.java.calumjohnston.algorithms;

import com.java.calumjohnston.utilities.cannyEdgeDetection;
import com.java.calumjohnston.utilities.pseudorandom;
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
    int[] coloursToConsider;
    int endPositionX;
    int endPositionY;
    int endColourChannel;
    int algorithm;
    boolean random;

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
        encodeParameterData(red, green, blue, binary.length());

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
        if(algorithm == 0 || algorithm == 1 || algorithm == 4){
            encodeSingularData(binary);
        }else if(algorithm == 2 || algorithm == 3 || algorithm == 5 || algorithm == 6 || algorithm == 7){
            encodeDoublyData(binary);
        }
    }



    public void encodeSingularData(StringBuilder binary){
        ArrayList<int[]> pixelOrder = getPixelOrder(binary);
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
                if(algorithm == 0 || algorithm == 4){
                    newColour = LSB(data, colour);
                }else if(algorithm == 1) {
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
        ArrayList<int[]> pixelOrder = getPixelOrder(binary);
        int currentPixel = 0;
        int currentBit = 0;
        boolean complete = false;
        while(!complete){
            int currentX = pixelOrder.get(currentPixel)[0];
            int currentY = pixelOrder.get(currentPixel)[1];
            int nextX = (currentX + 1) % coverImage.getWidth();
            int nextY = ((nextX == 0) ? currentY + 1 : currentY);
            for(int colourChan : coloursToConsider){
                int firstColour  = getColourAtPosition(currentX, currentY, colourChan);
                int secondColour = getColourAtPosition(nextX, nextY, colourChan);
                int[] newData = new int[3];
                if(algorithm == 2 || algorithm == 6){
                    newData = LSBMR(firstColour, secondColour, binary, currentBit);
                }else if(algorithm == 3) {
                    newData = PVD(firstColour, secondColour, binary, currentBit);
                }else if(algorithm == 5){
                    newData = AELSB(firstColour, secondColour, binary, currentBit);
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

    public int[] LSBMR(int firstColour, int secondColour, StringBuilder binary, int currentPos){

        // Get the data to manipulate
        char firstData = binary.charAt(currentPos);
        char secondData = binary.charAt(currentPos + 1);

        // Get the binary relationships between the firstColour and secondColour
        int pixel_Relationship_2;
        if(firstColour == 0){
            // Fix for 0 - 1 / 2 giving 0 instead of -0.5
            pixel_Relationship_2 = -1 + secondColour;
        }else{
            pixel_Relationship_2 = (int) Math.floor((firstColour - 1) / 2) + secondColour;
        }
        char m1 = getLSB(pixel_Relationship_2);

        // Get LSBs of pixel colour
        char firstColourLSB = getLSB(firstColour);

        // Determines what to embed based on LSBss and relationship between them
        // Could add an update LSB function here too!! (for pixelData!!)
        if (firstData == firstColourLSB) {
            char m = getLSB((int) Math.floor(firstColour / 2) + secondColour);
            if(!(secondData == m)){
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
            if (secondData == m1) {
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

    public int[] AELSB(int firstColour, int secondColour, StringBuilder binary, int currentPos){

        int d = secondColour - firstColour;
        int bits = rangeDivision(Math.abs(d));
        String firstData;
        String secondData;

        if(currentPos + bits > binary.length()){
            firstData = binary.substring(currentPos);
            while(firstData.length() < bits){
                firstData = firstData + "0";
            }
            secondData = firstData;
        }else{
            firstData = binary.substring(currentPos, currentPos + bits);
            currentPos = currentPos + bits;
            if(currentPos + bits> binary.length()){
                secondData = binary.substring(currentPos);
                while(secondData.length() < bits){
                    secondData = secondData + "0";
                }
            }else{
                secondData = binary.substring(currentPos, currentPos + bits);
            }
        }

        // Embed data using k-bit LSB
        String firstColourStr = Integer.toBinaryString(firstColour);
        String secondColourStr = Integer.toBinaryString(secondColour);
        firstColourStr = firstColourStr.substring(0, Math.max(0, firstColourStr.length() - bits));
        secondColourStr = secondColourStr.substring(0, Math.max(0, secondColourStr.length() - bits));
        String newFirstColourStr = firstColourStr + firstData;
        String newSecondColourStr = secondColourStr + secondData;
        int newFirstColour = Integer.parseInt(newFirstColourStr, 2);
        int newSecondColour = Integer.parseInt(newSecondColourStr, 2);

        // OPAP
        int adjustedFirstColour = 0;
        int adjustedSecondColour = 0;
        int APDif = newFirstColour - firstColour;
        if(Math.pow(2, bits - 1) < APDif && APDif < Math.pow(2, bits)){
            if(newFirstColour >= Math.pow(2, bits)){
                adjustedFirstColour = newFirstColour - (int) Math.pow(2, bits);
            }else{
                adjustedFirstColour = newFirstColour;
            }
        }else if(-Math.pow(2, bits - 1) <= APDif && APDif <= Math.pow(2, bits - 1)){
            adjustedFirstColour = newFirstColour;
        }else if(-Math.pow(2, bits) < APDif && APDif < -Math.pow(2, bits - 1)){
            if(newFirstColour < 256 - Math.pow(2, bits)){
                adjustedFirstColour = newFirstColour + (int) Math.pow(2, bits);
            }else{
                adjustedFirstColour = newFirstColour;
            }
        }
        APDif = newSecondColour - secondColour;
        if(Math.pow(2, bits - 1) < APDif && APDif < Math.pow(2, bits)){
            if(newSecondColour >= Math.pow(2, bits)){
                adjustedSecondColour = newSecondColour - (int) Math.pow(2, bits);
            }else{
                adjustedSecondColour = newSecondColour;
            }
        }else if(-Math.pow(2, bits - 1) <= APDif && APDif <= Math.pow(2, bits - 1)){
            adjustedSecondColour = newSecondColour;
        }else if(-Math.pow(2, bits) < APDif && APDif < -Math.pow(2, bits - 1)){
            if(newSecondColour < 256 - Math.pow(2, bits)){
                adjustedSecondColour = newSecondColour + (int) Math.pow(2, bits);
            }else{
                adjustedSecondColour = newSecondColour;
            }
        }

        // Update difference if fucked
        int[] newValuesOne = new int[] {adjustedFirstColour, adjustedSecondColour};
        int[] newValuesTwo = new int[] {adjustedFirstColour, adjustedSecondColour};
        d = adjustedFirstColour - adjustedSecondColour;
        int newBits = rangeDivision(Math.abs(d));
        if(bits == 3 && newBits != 3){
            if(adjustedFirstColour >= adjustedSecondColour){
                newValuesOne[1] = adjustedSecondColour + (int) Math.pow(2, bits);
                newValuesTwo[0] = adjustedFirstColour - (int) Math.pow(2, bits);
            }else{
                newValuesOne[1] = adjustedSecondColour - (int) Math.pow(2, bits);
                newValuesTwo[0] = adjustedFirstColour + (int) Math.pow(2, bits);
            }
        }else if(bits == 4 && newBits == 3){
            if(adjustedFirstColour >= adjustedSecondColour){
                newValuesOne[0] = adjustedFirstColour + (int) Math.pow(2, bits);
                newValuesTwo[1] = adjustedSecondColour - (int) Math.pow(2, bits);
            }else{
                newValuesOne[1] = adjustedSecondColour + (int) Math.pow(2, bits);
                newValuesTwo[0] = adjustedFirstColour - (int) Math.pow(2, bits);
            }
        }else if(bits == 4 && newBits == 5){
            if(adjustedFirstColour >= adjustedSecondColour){
                newValuesOne[1] = adjustedSecondColour + (int) Math.pow(2, bits);
                newValuesTwo[0] = adjustedFirstColour - (int) Math.pow(2, bits);
            }else{
                newValuesOne[1] = adjustedSecondColour - (int) Math.pow(2, bits);
                newValuesTwo[0] = adjustedFirstColour + (int) Math.pow(2, bits);
            }
        }else if(bits == 5 && newBits != 5){
            if(adjustedFirstColour >= adjustedSecondColour){
                newValuesOne[1] = adjustedSecondColour - (int) Math.pow(2, bits);
                newValuesTwo[0] = adjustedFirstColour + (int) Math.pow(2, bits);
            }else{
                newValuesOne[1] = adjustedSecondColour + (int) Math.pow(2, bits);
                newValuesTwo[0] = adjustedFirstColour - (int) Math.pow(2, bits);
            }
        }
        double first = Math.pow(firstColour - newValuesOne[0], 2) + Math.pow(secondColour - newValuesOne[1], 2);
        double second = Math.pow(firstColour - newValuesTwo[0], 2) + Math.pow(secondColour - newValuesTwo[1], 2);

        if(first <= second){
            if(newValuesOne[0] > 255 || newValuesOne[0] < 0 || newValuesOne[1] > 255 || newValuesOne[1] < 0){
                return new int[] {bits * 2, newValuesTwo[0], newValuesTwo[1]};
            }
            return new int[] {bits * 2, newValuesOne[0], newValuesOne[1]};
        }else{
            if(newValuesTwo[0] > 255 || newValuesTwo[0] < 0 || newValuesTwo[1] > 255 || newValuesTwo[1] < 0){
                return new int[] {bits * 2, newValuesOne[0], newValuesOne[1]};
            }
            return new int[] {bits * 2, newValuesTwo[0], newValuesTwo[1]};
        }
    }


    public ArrayList<int[]> getPixelOrder(StringBuilder binary){
        ArrayList<int[]> pixelOrder = new ArrayList<>();
        if(algorithm == 0 || algorithm == 1){
            pixelOrder = generatePixelOrder(1, 13, 0);
        }else if(algorithm == 2 || algorithm == 3 || algorithm == 5) {
            pixelOrder = generatePixelOrder(2, 13, 0);
        }else if(algorithm == 6){
            sobelEdgeDetection s = new sobelEdgeDetection();
            pixelOrder = s.getEdgePixels(coverImage, binary.length());
        }else if(algorithm == 4 || algorithm == 7){
            cannyEdgeDetection c = new cannyEdgeDetection();
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
    public char getLSB(int colour){
        if(colour % 2 == 0) {
            return '0';
        }else{
            return '1';
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
        }else if(difference <= 15){
            return new int[] {8, 15};
        }else if(difference <= 31){
            return new int[] {16, 31};
        }else if(difference <= 63){
            return new int[] {32, 63};
        }else if(difference <= 127){
            return new int[] {64, 127};
        }else{
            return new int[] {128, 255};
        }
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





    // ======= PARAMETER ENCODING =======
    /**
     * Converts parameter data to binary and writes to image
     *
     * @param red       Determines whether the red colour channel has been used
     * @param green     Determines whether the green colour channel has been used
     * @param blue      Determines whether the blue colour channel has been used
     */
    public void encodeParameterData(boolean red, boolean green, boolean blue, int binaryLength){
        StringBuilder parameters = new StringBuilder();
        parameters.append(conformBinaryLength(algorithm, 3));
        parameters.append(conformBinaryLength(red ? 1 : 0, 1));
        parameters.append(conformBinaryLength(green ? 1 : 0, 1));
        parameters.append(conformBinaryLength(blue ? 1 : 0, 1));
        parameters.append(conformBinaryLength(random ? 1 : 0, 1));
        parameters.append(conformBinaryLength(binaryLength, 32));
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
        coloursToConsider = new int[] {0,1,2};
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
