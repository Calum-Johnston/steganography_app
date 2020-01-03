package com.java.calumjohnston;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

import java.util.concurrent.ThreadLocalRandom;

public class encoder_GUI {
    private JPanel rootPanel;
    private JButton selectImageButton;
    private JButton encodeButton;
    private JTextField textField;
    private JButton textButton;

    private final JFileChooser openFileChooser;
    private BufferedImage coverImage;
    private BufferedReader textFile;
    private String text;
    private String coverImageName;


    /**
     * FUNCTION: Constructor for Encoder GUI
     * INPUT: None
     * RETURN: None
     *
     * NOTES: Sets up all default variables, listeners, etc
     * **/
    public encoder_GUI() {

        encodeButton.setEnabled(false);

        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("C:\\"));

        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                readImageFile();
            }
        });

        textButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                readTextFile();
            }
        });

        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                encodeData();
            }
        });
    }


    /**
     * FUNCTION: Determines which algorithm to apply when embedding the data
     * INPUT: None
     * RETURN: None
     *
     * NOTES: None
     * **/
    public void encodeData(){
        // Converts text to be hidden into it's binary equivalent
        StringBuilder binaryText = getBinaryText(textField.getText());

        // Calls algorithm to embed the data
        LSBM(coverImage, binaryText);
    }

    // ====== ALGORITHMS ======
    /**
     * FUNCTION: Performs the LSB algorithm
     * INPUT: BufferedImage coverImage: The image of which data will be hidden in
     *        StringBuilder binaryText: The binary data to be hidden within the image
     * RETURN: None
     *
     * NOTES: Algorithm detailed in LSB.txt
     * **/
    public void LSB(BufferedImage coverImage, StringBuilder binaryText){
        // Initialise starting image pixel position
        int x = 0; int y = 0;
        char data;

        for(int pos = 0; pos < binaryText.length(); pos++){
            // Get message data from text at a specific point
            data = binaryText.charAt(pos);

            // Get pixel data from image at a specific location
            int pixel = coverImage.getRGB(x, y);
            int red = (pixel & 0x00ff0000) >> 16;
            int green = (pixel & 0x0000ff00) >> 8;
            int blue = pixel & 0x000000ff;

            // Manipulate pixel data (only writes to Green LSB currently)
            String binaryGreen = Integer.toBinaryString(green);
            String updatedGreen = binaryGreen.substring(0, binaryGreen.length() - 1) + data;
            green = Integer.parseInt(updatedGreen, 2);

            // Write new pixel data to image at specified location
            Color newColour = new Color(red, green, blue);
            int newRGB = newColour.getRGB();
            coverImage.setRGB(x, y, newRGB);

            // Update position in image to manipulate pixel
            x += 1;
            if(x == coverImage.getWidth()){
                x = 0; y += 1;
            }
        }

        // Call a function to write image to disk
        writeImageFile(coverImage);

    }

    /**
     * FUNCTION: Performs the LSBM algorithm
     * INPUT: BufferedImage coverImage: The image of which data will be hidden in
     *        StringBuilder binaryText: The binary data to be hidden within the image
     * RETURN: None
     *
     * NOTES: Algorithm detailed in LSBM.txt
     * **/
    public void LSBM(BufferedImage coverImage, StringBuilder binaryText){
        // Initialise starting image pixel position
        int x = 0; int y = 0;
        char data;

        for(int pos = 0; pos < binaryText.length(); pos++){
            // Get message data from text at a specific point
            data = binaryText.charAt(pos);

            // Get pixel data from image at a specific location
            int pixel = coverImage.getRGB(x, y);
            int red = (pixel & 0x00ff0000) >> 16;
            int green = (pixel & 0x0000ff00) >> 8;
            int blue = pixel & 0x000000ff;

            // Manipulate pixel data (only writes to Green LSB currently)
            String binaryGreen = Integer.toBinaryString(green);
            // If the bit to be embedded is not equal to the LSB of the pixel (Green)
            if(!(binaryGreen.substring(binaryGreen.length() - 1).equals(Character.toString(data)))){
                if(ThreadLocalRandom.current().nextInt(0, 2) < 1){
                    green -= 1;
                }else{
                    green += 1;
                }
            }

            // Write new pixel data to image at specified location
            Color newColour = new Color(red, green, blue);
            int newRGB = newColour.getRGB();
            coverImage.setRGB(x, y, newRGB);

            // Update position in image to manipulate pixel
            x += 1;
            if(x == coverImage.getWidth()){
                x = 0; y += 1;
            }
        }

        // Call a function to write image to disk
        writeImageFile(coverImage);

    }



    // ====== PURPOSE FUNCTIONS ======
    /**
     * FUNCTION: Reads in a image file from the file explorer
     * INPUT: None
     * RETURN: None
     *
     * NOTES: None
     * **/
    public void readImageFile(){
        // Sets the type of file to get
        openFileChooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));
        int returnValue = openFileChooser.showOpenDialog(null);

        if(returnValue == JFileChooser.APPROVE_OPTION){
            try{
                // Get image based from file explorer
                coverImage = ImageIO.read(openFileChooser.getSelectedFile());

                // Store image name (ensuring to remove extension)
                coverImageName = openFileChooser.getSelectedFile().getName();
                coverImageName = coverImageName.replaceFirst("[.][^.]+$", "");

                // User can now encode
                encodeButton.setEnabled(true);

                // Debugging purposes
                System.out.println("Successfully read in image");

            }catch(IOException e){
                e.printStackTrace();
                System.out.println("Failed to read in image");
            }
        }
    }


    /**
     * FUNCTION: Reads in a text file from the file explorer
     * INPUT: None
     * RETURN: None
     *
     * NOTES: None
     * **/
    public void readTextFile(){
        // Sets the type of file to get
        openFileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        int returnValue = openFileChooser.showOpenDialog(null);

        if(returnValue == JFileChooser.APPROVE_OPTION){
            try{
                // Reads in text file selected in file explorer
                textFile = new BufferedReader(new FileReader(openFileChooser.getSelectedFile()));

                // Converts text file to string
                // https://stackoverflow.com/questions/15040504/how-to-easily-convert-a-bufferedreader-to-a-string
                text = textFile.lines().collect(Collectors.joining());

                // Write text to text field
                textField.setText(text);

                // Debugging purposes
                System.out.println("Successfully read in text");

            }catch(Exception e){
                e.printStackTrace();
                System.out.println("Failed to read in text");
            }
        }
    }


    /**
     * FUNCTION: Converts input text string into binary
     * INPUT: String text: The text the user wishes to convert
     * RETURN: StringBuilder binary: The binary equivalent of the input text
     *
     * NOTES: Currently only uses 7 bit ASCII, can update at later stage
     * **/
    public StringBuilder getBinaryText(String text){
        byte[] bytes = text.getBytes();
        StringBuilder binary = new StringBuilder();
        for(byte b : bytes){
            String binaryData = Integer.toBinaryString(b);
            String formatted = ("0000000" + binaryData).substring(binaryData.length());
            binary.append(formatted);
        }
        return binary;
    }


    /**
     * FUNCTION: Writes in a image file to the disk
     * INPUT: BufferedImage image: Image to write to file
     * RETURN: None
     *
     * NOTES: None
     * **/
    public void writeImageFile(BufferedImage image){
        try{
            // Writes file to the disk (w/extension of algorithm used)
            File outputFile = new File("rsts/LSB_" + coverImageName + ".png");
            ImageIO.write(coverImage, "png", outputFile);

            // Debugging purposes
            System.out.println("Successfully written file to disk");

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Failed to write file to disk");
        }
    }




    /**
     * FUNCTION: Main Method
     * INPUT: String[] args: Any arguments passed in when originally run
     * RETURN: None
     *
     * NOTES: Will remove later (only currently in use for testing)
     * **/
    public static void main(String[] args) {
        JFrame frame = new JFrame("Encoder");
        frame.setContentPane(new encoder_GUI().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Steganography - Encode Menu");
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
}
