package Core;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.util.ArrayList;
import java.util.HashMap;


public class ClientConnection extends Thread {
    private final Socket socket;
    private final Server server;
    private boolean handshake;
    private String ipv4;
    private String username;
    private final ArrayList<Packet> queue;
    private boolean suspended;

    ClientConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.handshake = false;
        this.ipv4 = this.socket.getInetAddress().getHostAddress();
        this.suspended = false;

        //create our queue
        this.queue = new ArrayList<Packet>();

        //Show the connection message once
        System.out.println("\n" + this.socket.getInetAddress().getHostAddress() + " connected to the server \n");

    }

    public void run(){

        while(this.socket.isConnected() && !this.suspended) {

            InputStream in;
            OutputStream out;

            try {
                in = this.socket.getInputStream();
                out = this.socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }


            try {
                //process the que for this client
                while (this.queue.size() > 0) {
                    this.queueProcess(out);
                }

                try {
                    if (in.available() < 1) {
                        sleep(250);
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                byte[] buffer = new byte[4];
                in.read(buffer);
                int serialNumber = ByteBuffer.wrap(buffer).order(ByteOrder.BIG_ENDIAN).getInt();

                //decode the incoming packet
                byte[] bytes = new byte[serialNumber];
                int byteLength = in.read(bytes);

                HashMap<String, String> data = Packet.decode(bytes, byteLength);

                //perform our handshake check
                if (!this.handshake) {

                    Packet packet = this.server.getPackageManager("0").handle(data);

                    if (packet.getOutcome()) {
                        this.handshake = true;
                        this.username = data.get("username");
                        this.server.addClient(this);
                    }
                    out.write(packet.build());
                    continue;
                }



                //inject the username into data
                data.put("username", this.username);

                //check and load a valid packet manger
                if (this.server.validPacketId(data.get("packetId"))) {
                    Packet packet = this.server.getPackageManager(data.get("packetId")).handle(data);


                    if (!packet.getNoReturn()) {
                        out.write(packet.build());
                    }

                } else {
                    Packet packet = new Packet()

                            .setID(0)
                            .setState("error")
                            .addField("outcome", "false")
                            .addField("error", "invalid packet id call");

                    out.write(packet.build());

                }


            } catch (Exception e) {
               e.printStackTrace();
            }
        }
    }


    private void queueProcess(OutputStream out) {

        this.queue.forEach((n) -> {
            try {
                out.write(n.build());
            } catch (IOException e) {
                this.close();
            }
        });

        this.queue.clear();

    }

    public void queueAdd(Packet packet) {
        this.queue.add(packet);
    }


    private void close() {
        this.suspended = true;
        this.queue.clear();
        System.out.println("\n" + this.socket.getInetAddress().getHostAddress() + " disconnected from the server \n");
        this.stop();
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getSuspendedStatus(){
        return this.suspended;
    }

}