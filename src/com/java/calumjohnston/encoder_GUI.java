package com.java.calumjohnston;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class encoder_GUI {
    private JPanel rootPanel;
    private JButton selectImageButton;

    private final JFileChooser openFileChooser;
    private BufferedImage coverImage;

    public encoder_GUI() {


        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("C:\\"));
        openFileChooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));

        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int returnValue = openFileChooser.showOpenDialog(null);
                if(returnValue == JFileChooser.APPROVE_OPTION){
                    try{
                        // Get image based from file explorer
                        coverImage = ImageIO.read(openFileChooser.getSelectedFile());
                        System.out.println("Success");
                    }catch(IOException e){
                        System.out.println("Fail");
                    }
                }
            }
        });
    }

    public void LSB(){

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Encoder");
        frame.setContentPane(new encoder_GUI().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500); // << not working!!!
        frame.setVisible(true);
    }
}
