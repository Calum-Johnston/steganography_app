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
                LSB();
            }
        });
    }


    /**
     * FUNCTION: Performs the LSB algorithm
     *           (detailed in text document: LSB.txt)
     * INPUT: BufferedImage coverImage: The image of which data will be hidden in
     *        String binary: The data to hide in the image
     * RETURN: None
     *
     * NOTES: None
     * **/
    public void LSB(){
        // Get binary equivalent of text to hide
        StringBuilder binary = getBinaryData(textField.getText());

        int currentPosition = 0;
        int x = 0; int y = 0;
        while(currentPosition < binary.length()){

            // Get current pixel data
            int pixel = coverImage.getRGB(x, y);
            int red = (pixel & 0x00ff0000) >> 16;
            int green = (pixel & 0x0000ff00) >> 8;
            int blue = pixel & 0x000000ff;

            // Manipulate data here

            // Update current pixel data
            Color newColour = new Color(red, green, blue);
            int newRGB = newColour.getRGB();
            coverImage.setRGB(x, y, newRGB);

            // Update position
            currentPosition += 1;
            x += 1;
            if(x == coverImage.getWidth()){
                x = 0;
                y += 1;
            }
        }
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
        openFileChooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));
        int returnValue = openFileChooser.showOpenDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION){
            try{
                // Get image based from file explorer
                coverImage = ImageIO.read(openFileChooser.getSelectedFile());

                // User can now encode
                encodeButton.setEnabled(true);

                // Debugging purposes
                System.out.println("Successfully read in image");

            }catch(IOException e){
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
                System.out.println("Success");

            }catch(Exception e){
                System.out.println("Fail");
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
    public StringBuilder getBinaryData(String text){
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
