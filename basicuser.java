import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class basicuser{
    public static void main(String[] args)  {
        for(int i=0;i<1;i++){
            Thread t= new Thread(new t());
            t.start();
        }
    }
}

class t extends Thread{
    @Override
    public void run() {
        String serverAddress = "localhost"; 
        int port = 12345; 
            try (Socket socket = new Socket(serverAddress, port);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
                System.out.println("Connesso al server su " + serverAddress + ":" + port);
                while(true){
                System.out.print("Inserisci un messaggio: ");
                String messaggio = userInput.readLine();
                output.println(messaggio);
                }
                
            } catch (IOException e) {
                System.out.println("Errore nel client: " + e.getMessage());
            }
        
    }
}

