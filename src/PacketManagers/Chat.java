package PacketManagers;

import Core.Packet;
import Core.Server;

import java.util.HashMap;

public class Chat extends PacketManager {
    public Chat(Server server) {
        super(server);
    }

    @Override
    public Packet handle(HashMap<String, String> data) {

        Packet packet = new Packet();
            packet
                    .setID(3)
                    .setState("chat")
                    .setNoReturn(true)
                    .setOutcome(true)
                    .addField("message",data.get("username")+": "+data.get("message"));


        System.out.println(data.get("username")+": "+data.get("message"));

        this.server.globalBroadcastPacket(packet);

        return packet;
    }

}
