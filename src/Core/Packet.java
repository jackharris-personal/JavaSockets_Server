package Core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;

public class Packet {

    private byte[] id;
    private byte[] bufferSize;
    private byte[] state;
    private byte[] fields;
    private boolean outcome;
    private boolean noReturn;

    public Packet(){
        this.fields = new byte[0];
        this.outcome = false;
        this.noReturn = false;
    }

    public Packet setID(int id){
        String data = "packetId="+id;
        this.id = data.getBytes();
        return this;
    }

    public Packet setState(String state){
        String data = ",state="+state;
        this.state =  data.getBytes();
        return this;
    }

    public Packet setNoReturn(boolean noReturn){
        this.noReturn = noReturn;
        return this;
    }

    public Packet setOutcome(boolean outcome){
        this.outcome = outcome;
        String data = ",outcome="+outcome;
        this.fields = this.addArrays(this.fields, data.getBytes());
        return this;
    }

    public Packet addField(String fieldName, String data){
        String fieldData = ","+fieldName+"="+data;
        this.fields = this.addArrays(this.fields, fieldData.getBytes());
        return this;
    }

    public byte[] build(){

        byte[] output = this.addArrays(this.id,this.state);

        output = this.addArrays(output,this.fields);

        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.BIG_ENDIAN);
        b.putInt(output.length);

        output = this.addArrays(b.array(),output);

        return output;
    }

    private byte[] addArrays(byte[] array1, byte[] array2) {
        byte[] output = new byte[array1.length + array2.length];

        int i = 0;
        while (i < array1.length) {
            output[i] = array1[i];
            i++;
        }
        i = 0;
        while (i < array2.length) {
            output[array1.length + i] = array2[i];
            i++;
        }

        return output;
    }

    public static HashMap<String, String> decode(byte[] bytes, int byteLength){
        byte[] output = new byte[byteLength];
        int i = 0;
        while(i < byteLength){
            output[i] = bytes[i];
            i++;
        }


        //convert to string
        String byteString = new String(output);

        HashMap<String, String> hashMap = new HashMap<String, String>();

        String[] array = byteString.split(",");
        i = 0;
        while(i < array.length) {
            String[] arrayData = array[i].split("=");
            if(arrayData.length > 1){
                hashMap.put(arrayData[0], arrayData[1]);
            }else{
                hashMap.put(arrayData[0],"");
            }
            i++;
        }

        return hashMap;
    }

    public boolean getOutcome(){
        return this.outcome;
    }

    public boolean getNoReturn(){
        return this.noReturn;
    }

}
