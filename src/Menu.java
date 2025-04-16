import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu {
    public void showMenu() {
        // Create a frame for the Menu
        JFrame menuFrame = new JFrame("Game Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(300, 200);
        menuFrame.setLocationRelativeTo(null); // Center the window

        // Create a "Start Game" button
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the menu and open the Game window
                menuFrame.dispose(); // Close the menu window
                Game game = new Game(5); // Create a new Game instance
            }
        });

        // Add the button to the menu window
        menuFrame.add(startButton);
        menuFrame.setVisible(true); // Make the menu window visible
    }
}
