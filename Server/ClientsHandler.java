import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

class ClientsHandler implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private List<ClientsHandler> clientsList;
    private String groupName;

    ClientsHandler(Socket client, List<ClientsHandler> clientsList) throws IOException {
        this.client = client;
        this.clientsList = clientsList;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    public void run() {
        try {
            Server.showMessage("Waiting for group name...");
            this.groupName = in.readLine();
            Server.showMessage("Joined group: " + this.groupName);

            String message;
            while ((message = in.readLine()) != null) {
                Server.showMessage(message);
                sendMessageToGroup(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void sendMessageToGroup(String message) {
        synchronized (clientsList) {
            for (ClientsHandler clientHandler : clientsList) {
                if (clientHandler != this && clientHandler.groupName.equals(this.groupName)) {
                    clientHandler.sendMessage(message);
                }
            }
        }
    }
}
