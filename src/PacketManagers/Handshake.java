package PacketManagers;

import Core.Packet;
import Core.Server;

import java.util.HashMap;

public class Handshake extends PacketManager {

    public Handshake(Server server) {
        super(server);
    }

    @Override
    public Packet handle(HashMap<String, String> data) {

        Packet packet = new Packet();

        packet
                .setID(0)
                .setState("handshake")
                .setOutcome(checkHandshake(data));

        return packet;

    }

    private boolean checkHandshake(HashMap<String,String> data){

        //check the packed it
        if(!data.containsKey("packetId") || !data.get("packetId").equals("0")){
            return false;
        }

        //check state
        if(!data.containsKey("state") || !data.get("state").contentEquals("handshake")){
            return false;
        }


        if(!data.containsKey("version") || !data.get("version").contentEquals(this.server.version)){
            return false;
        }

        return !data.containsKey("username") || !data.get("username").isBlank();
    }
}
