package com.java.calumjohnston.algorithms.techniques;

import com.java.calumjohnston.algorithms.util;

import java.awt.image.BufferedImage;

public class AELSB {

    public static int encode(BufferedImage image, int[] firstPixel, String data){
        int currentX = firstPixel[0];
        int currentY = firstPixel[1];
        int nextX = (currentX + 1) % image.getWidth();
        int nextY = ((nextX == 0) ? currentY + 1 : currentY);
        int firstColour = util.getColourAtPosition(image, currentX, currentY);
        int secondColour = util.getColourAtPosition(image, nextX, nextY);
        int[] newColour = updateData(firstColour, secondColour, data);
        util.writeColourAtPosition(image, currentX, currentY, newColour[1]);
        util.writeColourAtPosition(image, nextX, nextY, newColour[2]);
        return newColour[0];
    }

    private static int[] updateData(int firstColour, int secondColour, String data){

        int d = secondColour - firstColour;
        int bits = rangeDivision(Math.abs(d));
        String firstData;
        String secondData;

        if(data.length() < bits){
            firstData = data;
            while(firstData.length() < bits){
                firstData = firstData + "0";
            }
            secondData = firstData;
        }else{
            firstData = data.substring(0, bits);
            if(data.length() < 2 * bits){
                secondData = data.substring(bits);
                while(secondData.length() < bits){
                    secondData = secondData + "0";
                }
            }else{
                secondData = data.substring(bits, bits + bits);
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

    private static int rangeDivision(int difference){
        if(difference <= 15){
            return 3;
        }else if(difference <= 31){
            return 4;
        }else{
            return 5;
        }
    }



    public static String decode(BufferedImage image, int[] firstPixel){
        int currentX = firstPixel[0];
        int currentY = firstPixel[1];
        int nextX = (currentX + 1) % image.getWidth();
        int nextY = ((nextX == 0) ? currentY + 1 : currentY);
        int firstColour = util.getColourAtPosition(image, currentX, currentY);
        int secondColour = util.getColourAtPosition(image, nextX, nextY);
        return retrieveData(firstColour, secondColour);
    }

    private static String retrieveData(int firstColour, int secondColour){
        int d = firstColour - secondColour;
        int bits = rangeDivision(Math.abs(d));
        return util.conformBinaryLength((firstColour % (int) Math.pow(2, bits)), bits)
                + util.conformBinaryLength((secondColour % (int) Math.pow(2, bits)), bits);
    }

}
