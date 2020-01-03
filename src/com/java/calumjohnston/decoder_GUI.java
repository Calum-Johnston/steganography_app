package com.java.calumjohnston;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class decoder_GUI {
    private JPanel rootPanel;
    private JButton selectImageButtom;
    private JTextField textField;
    private JButton decodeButton;

    private final JFileChooser openFileChooser;
    private BufferedImage stegoImage;


    // ======= CONSTRUCTOR =======
    /**
     * FUNCTION: Constructor for Encoder GUI
     * INPUT: None
     * RETURN: None
     *
     * NOTES: Sets up all default variables, listeners, etc
     * **/
    public decoder_GUI(){

        decodeButton.setEnabled(false);

        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("C:\\"));

        selectImageButtom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                readImageFile();
            }
        });

        decodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                decodeData();
            }
        });
    }

    // ======= DECODE FUNCTIONS =======
    /**
     * FUNCTION: Determines which algorithm to apply when retrieving the data
     * INPUT: None
     * RETURN: None
     *
     * NOTES: None
     * **/
    public void decodeData(){
        // Calls algorithm to embed the data
        LSB(stegoImage);
    }

    public void LSB(BufferedImage stegoImage){
        StringBuilder binaryText;


    }



    // ======= PURPOSE FUNCTIONS =======
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
                stegoImage = ImageIO.read(openFileChooser.getSelectedFile());

                // User can now encode
                decodeButton.setEnabled(true);

                // Debugging purposes
                System.out.println("Successfully read in image");

            }catch(IOException e){
                e.printStackTrace();
                System.out.println("Failed to read in image");
            }
        }
    }




    // ======= MAIN METHOD =======
    /**
     * FUNCTION: Main Method
     * INPUT: String[] args: Any arguments passed in when originally run
     * RETURN: None
     *
     * NOTES: Will remove later (only currently in use for testing)
     * **/
    public static void main(String[] args) {
        JFrame frame = new JFrame("decoder_GUI");
        frame.setContentPane(new decoder_GUI().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Steganography - Decode Menu");
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
}
