package PacketManagers;

import Core.Packet;
import Core.Server;

import java.util.HashMap;

public abstract class PacketManager {

    protected Server server;
    protected HashMap<String, String> response;

    public PacketManager(Server server){
        this.server = server;
        this.response = new HashMap<String, String>();
    }

    public abstract Packet handle(HashMap<String, String>data);

}
