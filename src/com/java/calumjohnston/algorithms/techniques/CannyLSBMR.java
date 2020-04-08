package com.java.calumjohnston.algorithms.techniques;

import com.sun.xml.internal.ws.api.addressing.OneWayFeature;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class CannyLSBMR {

    int blockSize;

    public CannyLSBMR(){
        blockSize = 52;
    }

    public void embed(BufferedImage image, ArrayList<int[]> pixelOrder, StringBuilder binary){
        int currentPos = 0;
        while(currentPos + blockSize < binary.length()){
            ArrayList<int[]> subsetOrder = new ArrayList<>(pixelOrder.subList(currentPos, currentPos + blockSize));
            StringBuilder subsetData = new StringBuilder(binary.substring(currentPos, currentPos + blockSize - 2));
            embedIndividualBlock(image, subsetOrder, subsetData);
            currentPos += blockSize - 2;
        }
        if(currentPos < binary.length()){
            ArrayList<int[]> subsetOrder = new ArrayList<>(pixelOrder.subList(currentPos, currentPos + binary.substring(currentPos).length() + 2));
            StringBuilder subsetData = new StringBuilder(binary.substring(currentPos));
            embedIndividualBlock(image, subsetOrder, subsetData);
        }

    }

    public void embedIndividualBlock(BufferedImage image, ArrayList<int[]> subsetOrder, StringBuilder subsetData){

        // Setup parameters
        ArrayList<int[]> parameterPixels = new ArrayList<int[]>(subsetOrder.subList(0, 2));
        ArrayList<int[]> embeddingPixels = new ArrayList<int[]>(subsetOrder.subList(2, subsetOrder.size()));

        // Get different orders to embed into
        ArrayList<ArrayList<int[]>> orders = new ArrayList<>();
        orders.add(getFirstOrder(embeddingPixels));
        orders.add(getSecondOrder(embeddingPixels));
        orders.add(getThirdOrder(embeddingPixels));
        orders.add(getFourthOrder(embeddingPixels));

        // Find best order to embed data
        ArrayList<Integer> differenceTable = new ArrayList<Integer>();
        for(int i = 0; i < orders.size(); i++){
            differenceTable.add(embedBlockOrder(image, orders.get(i), subsetData, false));
        }

        // Get best embedding order
        int bestOrderPos = differenceTable.indexOf(Collections.min(differenceTable));

        // Embed data
        embedBlockOrder(image, orders.get(bestOrderPos), subsetData, true);

        // Embed parameter data
        StringBuilder bestOrderPosBinary = new StringBuilder(conformBinaryLength(bestOrderPos, 2));
        embedBlockOrder(image, parameterPixels, bestOrderPosBinary, true);
    }

    public int embedBlockOrder(BufferedImage image, ArrayList<int[]> subsetOrder, StringBuilder subsetData, boolean write){

        int difference = 0;
        for(int i = 0; i < subsetOrder.size(); i+=2){

            // Get colours and calculate LSBMR
            int firstColour = getColourAtPosition(image, subsetOrder.get(i)[0], subsetOrder.get(i)[1]);
            int secondColour = getColourAtPosition(image, subsetOrder.get(i + 1)[0], subsetOrder.get(i + 1)[1]);

            char firstData; char secondData;
            try {
                firstData = subsetData.charAt(i);
                secondData = subsetData.charAt(i + 1);
            }catch(Exception e){
                return difference;
            }

            int[] newData = LSBMR(firstColour, secondColour, firstData, secondData);

            if(write){
                writeColourAtPosition(image, subsetOrder.get(i)[0], subsetOrder.get(i)[1], newData[0]);
                writeColourAtPosition(image, subsetOrder.get(i+1)[0], subsetOrder.get(i+1)[1], newData[1]);
            }else{
                difference += Math.abs(firstColour - newData[0]) + Math.abs(secondColour - newData[1]);
            }
        }
        return difference;
    }

    public int[] LSBMR(int firstColour, int secondColour, char firstData, char secondData){

        int newFirstColour = firstColour;
        int newSecondColour = secondColour;

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
                    newSecondColour = secondColour - 1;
                    if(newSecondColour == -1){
                        newSecondColour = 1;
                    }else if((newSecondColour ^ secondColour) > 3){
                        newSecondColour += 2;
                    }
                } else {
                    newSecondColour = secondColour + 1;
                    if(newSecondColour == 256){
                        newSecondColour = 254;
                    }else if((newSecondColour ^ secondColour) > 3){
                        newSecondColour -= 2;
                    }
                }
            }
        } else if (!(Character.toString(firstData).equals(firstColourLSB))) {
            if (secondData == m1) {
                newFirstColour = firstColour - 1;
                if(newFirstColour == -1){
                    newFirstColour = 3;
                }else if((newFirstColour ^ firstColour) > 3){
                    newFirstColour += 4;
                }
            } else {
                newFirstColour = firstColour + 1;
                if(newFirstColour == 256){
                    newFirstColour = 252;
                }else if((newFirstColour ^ firstColour) > 3){
                    newFirstColour -= 4;
                }
            }
        }
        return new int[] {newFirstColour, newSecondColour};
    }


    public StringBuilder decode(BufferedImage image, ArrayList<int[]> pixelOrder, int length){
        StringBuilder binary = new StringBuilder();
        int currentPos = 0;
        while(currentPos + blockSize < length){
            ArrayList<int[]> subsetOrder = new ArrayList<>(pixelOrder.subList(currentPos, currentPos + blockSize));
            ArrayList<int[]> parameterPixels = new ArrayList<int[]>(subsetOrder.subList(0, 2));
            ArrayList<int[]> embeddingPixels = new ArrayList<int[]>(subsetOrder.subList(2, subsetOrder.size()));
            int block = getParameterInfo(image, parameterPixels);
            if(block == 0){
                embeddingPixels = getFirstOrder(embeddingPixels);
            }else if(block == 1){
                embeddingPixels = getSecondOrder(embeddingPixels);
            }else if(block == 2){
                embeddingPixels = getThirdOrder(embeddingPixels);
            }else if(block == 3){
                embeddingPixels = getFourthOrder(embeddingPixels);
            }
            binary.append(decodeBlock(image, embeddingPixels));
        }
        if(currentPos < length){
            ArrayList<int[]> subsetOrder = new ArrayList<>(pixelOrder.subList(currentPos, currentPos + length - binary.length() + 2));
            ArrayList<int[]> parameterPixels = new ArrayList<int[]>(subsetOrder.subList(0, 2));
            ArrayList<int[]> embeddingPixels = new ArrayList<int[]>(subsetOrder.subList(2, subsetOrder.size()));
            int block = getParameterInfo(image, parameterPixels);
            if(block == 0){
                embeddingPixels = getFirstOrder(embeddingPixels);
            }else if(block == 1){
                embeddingPixels = getSecondOrder(embeddingPixels);
            }else if(block == 2){
                embeddingPixels = getThirdOrder(embeddingPixels);
            }else if(block == 3){
                embeddingPixels = getFourthOrder(embeddingPixels);
            }
            binary.append(decodeBlock(image, embeddingPixels));
        }
        return binary;
    }

    public int getParameterInfo(BufferedImage image, ArrayList<int[]> parameterPixels){
        int firstColour = getColourAtPosition(image, parameterPixels.get(0)[0], parameterPixels.get(0)[1]);
        int secondColour = getColourAtPosition(image, parameterPixels.get(1)[0], parameterPixels.get(1)[1]);
        char temp = getLSB(firstColour);
        char temp2 = getLSB((firstColour / 2) + secondColour);
        return Integer.parseInt(temp + "" + temp2, 2);
    }

    public StringBuilder decodeBlock(BufferedImage image, ArrayList<int[]> embeddingPixels){
        StringBuilder blockData = new StringBuilder();
        for(int i = 0; i < embeddingPixels.size(); i+=2){
            int firstColour = getColourAtPosition(image, embeddingPixels.get(i)[0], embeddingPixels.get(i)[1]);
            int secondColour = getColourAtPosition(image, embeddingPixels.get(i+1)[0], embeddingPixels.get(i+1)[1]);
            blockData.append(getLSB(firstColour));
            blockData.append(getLSB((firstColour / 2) + secondColour));
        }
        return blockData;
    }




    public ArrayList<int[]> getFirstOrder(ArrayList<int[]> embeddingPixels) {
        return embeddingPixels;
    }
    public ArrayList<int[]> getSecondOrder(ArrayList<int[]> embeddingPixels) {
        ArrayList<int[]> temp = new ArrayList<>();
        for(int i = embeddingPixels.size() - 1; i >= 0; i--){
            temp.add(embeddingPixels.get(i));
        }
        return temp;
    }
    public ArrayList<int[]> getThirdOrder(ArrayList<int[]> embeddingPixels) {
        ArrayList<int[]> temp = new ArrayList<>();
        for(int i = 0; i < embeddingPixels.size(); i+=2){
            temp.add(embeddingPixels.get(i % embeddingPixels.size()));
        }
        for(int i = 1; i < embeddingPixels.size(); i+=2){
            temp.add(embeddingPixels.get(i % embeddingPixels.size()));
        }
        return temp;
    }
    public ArrayList<int[]> getFourthOrder(ArrayList<int[]> embeddingPixels) {
        ArrayList<int[]> temp = new ArrayList<>();
        for(int i = embeddingPixels.size() - 1; i >= 1; i-=2){
            temp.add(embeddingPixels.get(i % embeddingPixels.size()));
        }
        for(int i = embeddingPixels.size() - 2; i >= 0; i-=2){
            temp.add(embeddingPixels.get(i % embeddingPixels.size()));
        }
        return temp;
    }



    // USEFUL FUNCTIONS - MAYBE PUT IN UTILITIES
    /**
     * Gets colour channel data from an image at a specific location
     *
     * @param image             The image to get the data from
     * @param x                 x coordinate of pixel
     * @param y                 y coordinate of pixel
     * @return                  The value of the colour channel at position (x,y) in image
     */
    public int getColourAtPosition(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) & 0x000000ff;
    }

    /**
     * Writes colour channel data to an image at a specific location
     *
     * @param image             The image to write the data to
     * @param x                 x coordinate of pixel
     * @param y                 y coordinate of pixel
     * @param data              The data to be written
     */
    public void writeColourAtPosition(BufferedImage image, int x, int y, int data){
        Color color = new Color(data, data, data);
        image.setRGB(x, y, color.getRGB());
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
