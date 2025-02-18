
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;

public class basicserver{
    public static ArrayList<Thread> threads= new ArrayList<>();
        public static void main(String[] args) throws Exception{
        Integer numeroConnesso=0;
        int port=12345;
        ServerSocket serversock=new ServerSocket(port);
        System.out.println("server avviato!");
        while (true) {
            Socket clientSocket= serversock.accept();
            System.out.println("connessione accettata da "+clientSocket.getPort());
            Thread t= new Thread(new GestioneMessaggi(clientSocket));
            threads.add(t);
            t.start();
        }
    }

}

class GestioneMessaggi extends Thread{
    // porta/ip
    public TreeMap<Integer,String> utentiRegistrati= new TreeMap<Integer,String>(); 
    
    //porta/nome univoco e testo in un array di stringhe
    public TreeMap<String[],Integer> referenzeMessaggi= new TreeMap<String[],Integer>(); 

    private Socket clientSocket;
    public GestioneMessaggi(Socket serv){
        this.clientSocket=serv;
    }
    @Override
    public void run() {
        try{
            BufferedReader input= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String nome = input.readLine();
            System.out.println("nome: "+nome);
            utentiRegistrati.put((Integer)clientSocket.getPort(),(String)clientSocket.getInetAddress().getHostAddress());//associo porta e nome utente ad un socket
            System.out.println("connsessione eseguita "+clientSocket.getInetAddress()+" porta: "+clientSocket.getPort());
            while (true) { 
                String message = input.readLine();//questo messaggio dovrà essere formattato come NomeUtenteACuiInoltrareIlMessaggio-Corpomessaggio
                System.out.println("messaggio ricevuto: "+message);
                String[] nomeECorpo=message.split("-");//posizione 0 il nome posizione 1 il messaggio
                referenzeMessaggi.put(nomeECorpo,(Integer)clientSocket.getPort());
                for(Thread t:basicserver.threads){
                    if (t instanceof GestioneMessaggi) { // Verifica che il thread sia del tipo GestioneMessaggi
                        GestioneMessaggi ut = (GestioneMessaggi) t;
                        if(ut.getReferenzeMessaggi().get(nomeECorpo[0])!=null&&ut.getUtentiRegistrati().get(ut.getReferenzeMessaggi().get(nomeECorpo[0]))!=null){
                        Integer portaUtenteDaContattare=ut.getReferenzeMessaggi().get(nomeECorpo[0]);
                        String ipUtenteDaContattare=ut.getUtentiRegistrati().get(portaUtenteDaContattare);
                        }
                    }
                }

            }
        }catch(Exception ex){
        }
    }
    public  TreeMap<Integer, String> getUtentiRegistrati() {
        return utentiRegistrati;
    }
    public TreeMap<String[], Integer> getReferenzeMessaggi() {
        return referenzeMessaggi;
    }

    

}
