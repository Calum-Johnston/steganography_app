package com.java.calumjohnston;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class main_GUI{
    private static JFrame frame;
    private JPanel rootPanel;
    private JButton encodeButton;

    private final JFileChooser openFileChooser;
    private BufferedImage coverImage;

    public main_GUI(){

        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("C:\\"));
        openFileChooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));

        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
                JFrame encoder = new JFrame("encoder_GUI");
                encoder.setVisible(true);
            }
        });
    }



    public static void main(String[] args) {
        frame = new JFrame("main_GUI");
        frame.setContentPane(new main_GUI().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
