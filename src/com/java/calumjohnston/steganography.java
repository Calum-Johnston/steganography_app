package com.java.calumjohnston;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The main_GUI class runs the GUI for the main page
 */
public class steganography extends JFrame{
    private JPanel rootPanel;
    private JPanel cards;

    /**
     * Constructor for this class
     */
    public steganography() {
        setLayout(new GridLayout());
        setSize(500, 500);
        setTitle("Steganography");
        setBackground(Color.YELLOW);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the "cards"
        JPanel card0 = new main().getPanel();
        JPanel card1 = new encoder().getPanel();
        JPanel card2 = new decoder().getPanel();

        // Update ActionListeners of buttons in each card
        updateActionListeneres(card0);
        updateActionListeneres(card1);
        updateActionListeneres(card2);

        // Create panel that contains the "cards"
        cards = new JPanel(new CardLayout());
        cards.add(card0, "main");
        cards.add(card1, "encode");
        cards.add(card2, "decode");

        CardLayout cardLayout = (CardLayout) cards.getLayout();


        // Display default panel (i.e. the first one in cards)
        getContentPane().add(cards);
        setVisible(true);
    }

    /**
     * Updates the ActionListeners of buttons (for switching between panels)
     *
     * @param card      The panel for which buttons will have ActionListeners implemented
     */
    public void updateActionListeneres(JPanel card){
        // https://stackoverflow.com/questions/29751414/search-for-buttons-in-jpanel-and-get-the-buttons-text
        // https://stackoverflow.com/questions/36283545/how-to-switch-between-panels-in-cardlayout-from-actionlisteners-in-external-clas
        Component[] mainComponents = card.getComponents();
        for(Object component : mainComponents){
            if(component instanceof JButton){
                String type = ((JButton) component).getUIClassID();
                System.out.println(type);
                if(type.equals("encodeMenuButton")){
                    ((JButton) component).addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            CardLayout cardLayout = (CardLayout) cards.getLayout();
                            cardLayout.show(cards, "encode");
                        }
                    });
                }else if(type.equals("decodeMenuButton")){
                    ((JButton) component).addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            CardLayout cardLayout = (CardLayout) cards.getLayout();
                            cardLayout.show(cards, "decode");
                        }
                    });
                }else if(type.equals("mainMenuButton")){
                    ((JButton) component).addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            CardLayout cardLayout = (CardLayout) cards.getLayout();
                            cardLayout.show(cards, "main");
                        }
                    });
                }
            }
        }
    }

    public static void main(String[] args) {
        new steganography();
    }
}
