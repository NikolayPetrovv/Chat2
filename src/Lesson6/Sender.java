package Lesson6;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Sender extends Thread {

    private DataOutputStream out;
    private boolean isFinished;

    public Sender(DataOutputStream out) {
        this.out = out;
    }

    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void run() {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            while (true) {
                String message = null;
                message = reader.readLine();
                if (message.equals("/exit")) {
                    isFinished = true;
                    break;
                }
                if (!message.trim().isEmpty()) {
                    try {
                        out.writeUTF(message);
                    } catch (IOException e) {
                        out.close();
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
