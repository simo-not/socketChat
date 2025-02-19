import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class basicserver {
    // Mappa globale per tenere traccia degli utenti connessi: username -> GestioneMessaggi
    public static ConcurrentHashMap<String, GestioneMessaggi> utentiConnessi = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        int port = 13356;
        ServerSocket serverSock = new ServerSocket(port);

        System.out.println("Server avviato su " + serverSock.getInetAddress() + " porta: " + serverSock.getLocalPort());
        while (true) {
            Socket clientSocket = serverSock.accept();
            System.out.println("Connessione accettata da " + clientSocket.getPort());
            GestioneMessaggi gm = new GestioneMessaggi(clientSocket);
            gm.start();
        }
    }
}

class GestioneMessaggi extends Thread {
    private Socket clientSocket;
    private PrintWriter output;

    public GestioneMessaggi(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);

            // Il primo messaggio è il nome utente
            String username = input.readLine();
            if (username == null || username.trim().isEmpty()) {
                System.out.println("Username non valido. Chiusura connessione.");
                clientSocket.close();
                return;
            }
            System.out.println("Utente connesso: " + username);
            basicserver.utentiConnessi.put(username, this);

            // Ascolta continuamente i messaggi
            String message;
            while ((message = input.readLine()) != null) {
                String[] parts = message.split("-", 2);
                if (parts.length != 2) {
                    output.println("Formato del messaggio non corretto. Usa: destinatario-messaggio");
                    continue;
                }
                String destinatario = parts[0];
                String corpo = parts[1];
                
                GestioneMessaggi destinatarioThread = basicserver.utentiConnessi.get(destinatario);
                if (destinatarioThread != null) {
                    destinatarioThread.sendMessage("Messaggio da " + username + ": " + corpo);
                } else {
                    output.println("Utente destinatario '" + destinatario + "' non trovato!");
                }
            }
        } catch (Exception ex) {
            System.out.println("Errore: " + ex.getMessage());
        } finally {
            basicserver.utentiConnessi.values().remove(this);
            try {
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendMessage(String msg) {
        if (output != null) {
            output.println(msg);
        }
    }
}