
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;

public class basicserver{
        public static void main(String[] args) throws Exception{
        Integer numeroConnesso=0;
        int port=12345;
        ServerSocket serversock=new ServerSocket(port);
        while (true) {    
            Socket clientSocket= serversock.accept();
            Thread t= new Thread(new GestioneMessaggi(clientSocket));
            t.start();
        }
    }

}

class GestioneMessaggi extends Thread{
    // porta/ip
    private static TreeMap<Integer,String> utentiRegistrati= new TreeMap<Integer,String>(); 
    
    //porta/nome univoco e testo in un array di stringhe
    private static TreeMap<Integer,String[]> referenzeMessaggi= new TreeMap<Integer,String[]>(); 

    private Socket clientSocket;
    public GestioneMessaggi(Socket serv){
        this.clientSocket=serv;
    }
    @Override
    public void run() {
        try{
            while (true) { 
                BufferedReader input= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String nome = input.readLine();
                utentiRegistrati.put((Integer)clientSocket.getPort(),(String)clientSocket.getInetAddress().getHostAddress());//associo porta e nome utente ad un socket
                System.out.println("connsessione eseguita "+clientSocket.getInetAddress()+" porta: "+clientSocket.getPort());
                String message = input.readLine();//questo messaggio dovrà essere formattato come NomeUtenteACuiInoltrareIlMessaggio-Corpomessaggio
                String[] nomeECorpo=message.split("-");//posizione 0 il nome posizione 1 il messaggio
                referenzeMessaggi.put((Integer)clientSocket.getPort(),nomeECorpo);

            }
        }catch(Exception ex){
        }
    }
    

}
