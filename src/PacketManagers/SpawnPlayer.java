package PacketManagers;

import Core.Packet;
import Core.Server;

import java.util.HashMap;

public class SpawnPlayer extends PacketManager{
    public SpawnPlayer(Server server) {
        super(server);
    }

    @Override
    public Packet handle(HashMap<String, String> data) {
        return null;
    }
}
