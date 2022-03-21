package Core;

public class Loop implements Runnable{

    boolean run;
    Server server;
    double tpsTarget;
    int realTPS;
    private int mb = 1024*1024;
    private int usedMemory;
    private Thread thread;

    public Loop(Server server){
        this.run = false;
        this.server = server;
        this.tpsTarget =  30;
        this.server.setLoop(this);
    }

    @Override
    public void run(){
        double timePerTick = 1000000000 / this.tpsTarget;

        long now;
        long lastTime = System.nanoTime();
        long timer = 0L;

        int realTPS = 0;

        double deltaTPS = 0;

        while(this.run){
            now = System.nanoTime();
            deltaTPS+=(now-lastTime) / timePerTick;

            timer += now -lastTime;

            lastTime = now;

            if(deltaTPS >= 1){
                this.server.tick();
                realTPS++;
                deltaTPS--;
            }

            if(timer >= 1000000000){
                this.realTPS = realTPS;
                this.usedMemory = (int) (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/this.mb;
                this.server.slowTick();
                realTPS = 0;
                timer = 0;
            }
        }

        this.stop();
    }

    public int getUsedMemory(){
        return this.usedMemory;
    }

    public int getTPS(){
        return this.realTPS;
    }

    public synchronized void start(){
        if(this.run){
            return;
        }else{
            this.run = true;
            this.thread = new Thread(this);
            this.thread.start();
        }
    }

    public synchronized  void stop(){
        if(!this.run){
            return;
        }else{
            try{
                this.thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
