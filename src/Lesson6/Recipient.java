package Lesson6;

import java.io.DataInputStream;

public class Recipient extends Thread{

    private DataInputStream in;
    private Sender sender;

    public Recipient(DataInputStream in, Sender sender) {
        this.in = in;
        this.sender = sender;
    }
    @Override
    public void run() {
        try {
            while (true) {
                String strFromServer = in.readUTF();
                if (sender.isFinished()) {
                    break;
                }
                System.out.println(strFromServer);
            }
        } catch (Exception e) {
            if (!sender.isFinished()) {
                e.printStackTrace();
            }
        }
    }
}
