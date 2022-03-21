package Core;

import PacketManagers.Chat;
import PacketManagers.Handshake;
import PacketManagers.PacketManager;
import PacketManagers.SpawnPlayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server
{
    private ServerSocket serverSocket;
    private final int port;
    public String version;
    private final HashMap<String, PacketManager> packetManagers;
    private final ArrayList<ClientConnection> clients;


    public Server(int port)
    {
        this.port = port;
        this.version = "0.0.1";

        this.packetManagers = new HashMap<String, PacketManager>();
        this.clients = new ArrayList<ClientConnection>();

        this.packetManagers.put("0",new Handshake(this));
        this.packetManagers.put("3",new Chat(this));
        this.packetManagers.put("4", new SpawnPlayer(this));
    }

    public void start(){
        try {
            this.serverSocket = new ServerSocket( port );
            System.out.println( "Listening for a connection" );
            this.accept();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accept(){
        while(true){
            try
            {
                // Call accept() to receive the next connection
                Socket socket = serverSocket.accept();

                // Pass the socket to the ClientConnection thread for processing
                ClientConnection client = new ClientConnection(socket, this);
                client.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void tick(){
        //tick the clients
        //this.clients.forEach(ClientConnection::tick);
        //remove the closed clients
        //int i = 0;
        //while(i< this.clients.size()){
        //    if(this.clients.get(i).getSuspendedStatus()){
        //        this.clients.remove(this.clients.get(i));
        //    }
       // }
    }

    public void slowTick(){
        //System.out.println("Ticks Per Second: "+this.loop.realTPS+"\n");
        //System.out.println("Memory Used: "+this.loop.getUsedMemory()+"\n");
    }

    public PacketManager getPackageManager(String key){
        return ((PacketManager) this.packetManagers.get(key));
    }

    public boolean validPacketId(String packetId){
        return this.packetManagers.containsKey(packetId);
    }

    public void addClient(ClientConnection clientConnection){
        this.clients.add(clientConnection);
    }

    public void removeClient(ClientConnection clientConnection){
        this.clients.remove(clientConnection);
    }

    public ArrayList<ClientConnection> getClients(){
        return this.clients;
    }

    public void globalBroadcastPacket(Packet packet){
        this.clients.forEach((n)-> n.queueAdd(packet));
    }
}

