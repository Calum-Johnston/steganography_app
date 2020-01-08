package com.java.calumjohnston;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The main_GUI class runs the GUI for the main page
 */
public class main extends JFrame implements ActionListener{
    private JPanel rootPanel;
    private JPanel cards;

    /**
     * Constructor for this class
     */
    public main() {
        setLayout(new GridLayout());
        setSize(500, 500);
        setTitle("Steganography");
        setBackground(Color.YELLOW);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Define main GUI in here for now
        JPanel card0 = new JPanel();
        JButton encodeButton = new JButton("encodeButton!");
        encodeButton.setText("Encode");
        encodeButton.addActionListener(this);
        card0.add(encodeButton);

        JButton decodeButton = new JButton("decodeButton");
        decodeButton.setText("Decode");

        // THIS WORKS!!!
        // https://stackoverflow.com/questions/29751414/search-for-buttons-in-jpanel-and-get-the-buttons-text
        // https://stackoverflow.com/questions/36283545/how-to-switch-between-panels-in-cardlayout-from-actionlisteners-in-external-clas
        JPanel card1 = new encoder().getPanel();
        Component[] components = card1.getComponents();
        JPanel card1_new =  (JPanel) components[0];
        components = card1_new.getComponents();
        for(Object component : components){
            if(component instanceof JButton){
                String temp = ((JButton) component).getText();
                if(temp == "Button"){
                    ((JButton) component).addActionListener(this);
                }
            }
        }
        //JPanel card2 = new decoder().getPanel();

        // Create panel that contains cards
        cards = new JPanel(new CardLayout());
        cards.add(card0, "main");
        cards.add(card1, "encode");
        //cards.add(card2, "decode");

        getContentPane().add(cards);
        setVisible(true); //Make JFrame visible

    }

    public void actionPerformed(ActionEvent e){
        JButton button = (JButton) e.getSource();
        CardLayout cardLayout = (CardLayout) cards.getLayout();
        if(button.getText().equals("Encode")){
            cardLayout.show(cards, "encode");
        }else if(button.getText().equals("Button")){
            cardLayout.show(cards, "main");
        }
    }

    public static void main(String[] args) {
        new main();
    }
}
