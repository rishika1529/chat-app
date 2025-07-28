import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 1234;
    private static Set<ClientHandler> clients = new HashSet<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress());

            ClientHandler clientHandler = new ClientHandler(clientSocket);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }

    // Broadcast message to all clients
    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // Client handler class
    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("Enter your name:");
                username = in.readLine();
                System.out.println(username + " has joined.");

                broadcast(username + " has joined the chat.", this);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(username + ": " + message);
                    broadcast(username + ": " + message, this);
                }
            } catch (IOException e) {
                System.out.println("Connection lost: " + username);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // ignore
                }
                clients.remove(this);
                broadcast(username + " has left the chat.", this);
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
