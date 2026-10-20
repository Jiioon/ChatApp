import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static List<ClientsHandler> clientsList = new ArrayList<>();
    private static JTextArea messageArea;
    private static JTextField textField;
    private static JButton sendButton;

    public static void main(String[] args) {
        setupGUI();

        try (ServerSocket server = new ServerSocket(55555)) {
            showMessage("Server is running...");

            while (true) {
                Socket client = server.accept();
                ClientsHandler clientHandler = new ClientsHandler(client, clientsList);
                clientsList.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            showMessage("Error: " + e.getMessage());
        }
    }

    private static void setupGUI() {
        JFrame frame = new JFrame("Chat Server");
        messageArea = new JTextArea(20, 50);
        messageArea.setEditable(false);
        textField = new JTextField(40);
        sendButton = new JButton("Send");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        frame.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }

    private static void sendMessage() {
        String message = textField.getText();
        if (!message.isEmpty()) {
            String fullMessage = "Server: " + message;
            showMessage(fullMessage);
            broadcastMessage(fullMessage);
            textField.setText("");
        }
    }

    public static void showMessage(String message) {
        SwingUtilities.invokeLater(() -> messageArea.append(message + "\n"));
    }

    public static void broadcastMessage(String message) {
        synchronized (clientsList) {
            for (ClientsHandler clientHandler : clientsList) {
                clientHandler.sendMessage(message);
            }
        }
    }
}
