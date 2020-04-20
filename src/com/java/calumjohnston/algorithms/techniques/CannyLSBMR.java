package com.java.calumjohnston.algorithms.techniques;

import com.java.calumjohnston.algorithms.util;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class CannyLSBMR {

    public CannyLSBMR() {
    }

    public static void encode(BufferedImage image, ArrayList<int[]> subsetOrder, StringBuilder subsetData){

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
            differenceTable.add(encodeIndividualBlock(image, orders.get(i), subsetData, false));
        }

        // Get best embedding order
        int bestOrderPos = differenceTable.indexOf(Collections.min(differenceTable));

        // Embed data
        encodeIndividualBlock(image, orders.get(bestOrderPos), subsetData, true);

        // Embed parameter data
        StringBuilder bestOrderPosBinary = new StringBuilder(util.conformBinaryLength(bestOrderPos, 2));
        encodeIndividualBlock(image, parameterPixels, bestOrderPosBinary, true);
    }

    private static int encodeIndividualBlock(BufferedImage image, ArrayList<int[]> subsetOrder, StringBuilder subsetData, boolean write){

        int difference = 0;
        for(int i = 0; i < subsetOrder.size(); i+=2){

            // Get colours and calculate LSBMR
            int firstColour = util.getColourAtPosition(image, subsetOrder.get(i)[0], subsetOrder.get(i)[1]);
            int secondColour = util.getColourAtPosition(image, subsetOrder.get(i + 1)[0], subsetOrder.get(i + 1)[1]);

            char firstData; char secondData;
            try {
                firstData = subsetData.charAt(i);
                secondData = subsetData.charAt(i + 1);
            }catch(Exception e){
                return difference;
            }

            int[] newData = LSBMR(firstColour, secondColour, firstData, secondData);

            if(write){
                util.writeColourAtPosition(image, subsetOrder.get(i)[0], subsetOrder.get(i)[1], newData[0]);
                util.writeColourAtPosition(image, subsetOrder.get(i+1)[0], subsetOrder.get(i+1)[1], newData[1]);
            }else{
                difference += Math.abs(firstColour - newData[0]) + Math.abs(secondColour - newData[1]);
            }
        }
        return difference;
    }

    private static int[] LSBMR(int firstColour, int secondColour, char firstData, char secondData){

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
        char m1 = util.getLSB(pixel_Relationship_2);

        // Get LSBs of pixel colour
        char firstColourLSB = util.getLSB(firstColour);

        // Determines what to embed based on LSBss and relationship between them
        // Could add an update LSB function here too!! (for pixelData!!)
        if (firstData == firstColourLSB) {
            char m = util.getLSB((int) Math.floor(firstColour / 2) + secondColour);
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

    public int replaceLSB(int colour, char data){
        if(colour % 2 == 0  && data == '1'){
            return colour++;
        }else if(colour % 2 == 1 && data == '0'){
            return colour--;
        }
        return colour;
    }



    public static StringBuilder decode(BufferedImage image, ArrayList<int[]> subsetOrder){
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
        return decodeBlock(image, embeddingPixels);
    }

    private static int getParameterInfo(BufferedImage image, ArrayList<int[]> parameterPixels){
        int firstColour = util.getColourAtPosition(image, parameterPixels.get(0)[0], parameterPixels.get(0)[1]);
        int secondColour = util.getColourAtPosition(image, parameterPixels.get(1)[0], parameterPixels.get(1)[1]);
        char temp = util.getLSB(firstColour);
        char temp2 = util.getLSB((firstColour / 2) + secondColour);
        return Integer.parseInt(temp + "" + temp2, 2);
    }

    private static StringBuilder decodeBlock(BufferedImage image, ArrayList<int[]> embeddingPixels){
        StringBuilder blockData = new StringBuilder();
        for(int i = 0; i < embeddingPixels.size(); i+=2){
            int firstColour = util.getColourAtPosition(image, embeddingPixels.get(i)[0], embeddingPixels.get(i)[1]);
            int secondColour = util.getColourAtPosition(image, embeddingPixels.get(i+1)[0], embeddingPixels.get(i+1)[1]);
            blockData.append(util.getLSB(firstColour));
            blockData.append(util.getLSB((firstColour / 2) + secondColour));
        }
        return blockData;
    }




    public static ArrayList<int[]> getFirstOrder(ArrayList<int[]> embeddingPixels) {
        return embeddingPixels;
    }
    public static ArrayList<int[]> getSecondOrder(ArrayList<int[]> embeddingPixels) {
        ArrayList<int[]> temp = new ArrayList<>();
        for(int i = embeddingPixels.size() - 1; i >= 0; i--){
            temp.add(embeddingPixels.get(i));
        }
        return temp;
    }
    public static ArrayList<int[]> getThirdOrder(ArrayList<int[]> embeddingPixels) {
        ArrayList<int[]> temp = new ArrayList<>();
        for(int i = 0; i < embeddingPixels.size(); i+=2){
            temp.add(embeddingPixels.get(i % embeddingPixels.size()));
        }
        for(int i = 1; i < embeddingPixels.size(); i+=2){
            temp.add(embeddingPixels.get(i % embeddingPixels.size()));
        }
        return temp;
    }
    public static ArrayList<int[]> getFourthOrder(ArrayList<int[]> embeddingPixels) {
        ArrayList<int[]> temp = new ArrayList<>();
        for(int i = embeddingPixels.size() - 1; i >= 1; i-=2){
            temp.add(embeddingPixels.get(i % embeddingPixels.size()));
        }
        for(int i = embeddingPixels.size() - 2; i >= 0; i-=2){
            temp.add(embeddingPixels.get(i % embeddingPixels.size()));
        }
        return temp;
    }


}
