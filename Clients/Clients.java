package javaapplication4;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Clients {

    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("Chat Client");
    private JTextField textField = new JTextField(40);
    private JTextArea textArea = new JTextArea(20, 40);
    private String name;

    public Clients(String serverAddress) throws IOException {
        setupGUI();

        Socket socket = new Socket(serverAddress,55555);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Get user input for group name and username
        String groupName = JOptionPane.showInputDialog(frame, "Enter your group name:", "Group Name", JOptionPane.PLAIN_MESSAGE);
        name = JOptionPane.showInputDialog(frame, "Enter your name:", "Username", JOptionPane.PLAIN_MESSAGE);

        if (groupName == null || groupName.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Group name and username cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        out.println(groupName);

        new Thread(this::receiveMessages).start();
    }

    private void setupGUI() {
        textField.setEditable(true);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textField, BorderLayout.CENTER);
        JButton sendButton = new JButton("Send");
        panel.add(sendButton, BorderLayout.EAST);

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        sendButton.addActionListener(e -> sendMessage());
        textField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String message = textField.getText();
        if (!message.isEmpty()) {
            out.println(name + ": " + message);
            textArea.append("Me: " + message + "\n");  // Show the sent message in the client's own text area
            textField.setText("");
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                textArea.append(message + "\n");
            }
        } catch (IOException e) {
            textArea.append("Error: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        try {
            new Clients("192.168.0.168");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
