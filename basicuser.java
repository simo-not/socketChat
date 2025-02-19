import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class basicuser {
    public static void main(String[] args) {
        Thread clientThread = new Thread(new ClientRunnable());
        clientThread.start();
    }
}

class ClientRunnable extends  Thread {
    @Override
    public void run() {
        String serverAddress = "localhost";
        int port = 13356;
        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connesso al server su " + serverAddress + ":" + port);
            System.out.print("Inserisci il nome utente che verrà memorizzato: ");
            String username = userInput.readLine();
            output.println(username);

            // Thread per ricevere messaggi dal server
            new Thread(() -> {
                try {
                    String risposta;
                    while ((risposta = input.readLine()) != null) {
                        System.out.println("\n" + risposta);
                        System.out.print(">>> ");
                    }
                } catch (IOException e) {
                    System.out.println("Errore nella lettura dei messaggi: " + e.getMessage());
                }
            }).start();

            // Ciclo per inviare messaggi
            while (true) {
                System.out.print("Inserire il nome dell'utente a cui inviare il messaggio: ");
                String destinatario = userInput.readLine();
                System.out.print("Inserire il corpo del messaggio da inoltrare a " + destinatario + ": ");
                String corpo = userInput.readLine();
                String messaggio = destinatario + "-" + corpo;
                output.println(messaggio);
            }

        } catch (IOException e) {
            System.out.println("Errore nel client: " + e.getMessage());
        }
    }
}