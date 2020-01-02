package com.java.calumjohnston;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
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

    public encoder_GUI() {

        encodeButton.setEnabled(false);

        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("C:\\"));

        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
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
        });

        textButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                openFileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
                int returnValue = openFileChooser.showOpenDialog(null);
                if(returnValue == JFileChooser.APPROVE_OPTION){
                    try{
                        try{
                            // Reads in text file selected in file explorer
                            textFile = new BufferedReader(new FileReader(openFileChooser.getSelectedFile()));

                            // Converts text file to string
                            // https://stackoverflow.com/questions/15040504/how-to-easily-convert-a-bufferedreader-to-a-string
                            text = textFile.lines().collect(Collectors.joining());
                            System.out.println(text);
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        System.out.println("Success");
                    }catch(Exception e){
                        System.out.println("Fail");
                    }
                }
            }
        });

        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                LSB();
            }
        });
    }

    public void LSB(){

    }

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
