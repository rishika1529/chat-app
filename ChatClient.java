import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 1234;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to chat server.");

            new Thread(new ReadThread(socket)).start();
            new Thread(new WriteThread(socket)).start();
        } catch (IOException e) {
            System.out.println("Unable to connect to server.");
        }
    }

    // Read messages from server
    static class ReadThread implements Runnable {
        private BufferedReader in;

        public ReadThread(Socket socket) throws IOException {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }
    }

    // Send messages to server
    static class WriteThread implements Runnable {
        private PrintWriter out;
        private BufferedReader userInput;

        public WriteThread(Socket socket) throws IOException {
            out = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));
        }

        public void run() {
            try {
                String input;
                while ((input = userInput.readLine()) != null) {
                    out.println(input);
                }
            } catch (IOException e) {
                System.out.println("Error sending message.");
            }
        }
    }
}
