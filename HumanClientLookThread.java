/**
 * Created by Beaum on 20/03/2017.
 */
public class HumanClientLookThread implements Runnable {

    static final private int LOOK_PER_SECOND = 1;
    private HumanPlayerSend send;
    private HumanClientController controller;

    public HumanClientLookThread(HumanPlayerSend send, HumanClientController controller) {
        this.send = send;
        this.controller = controller;
    }

    public void run() {
        while (!controller.getGameOver()) {
            controller.processLookThreadAction();
            try {
                Thread.sleep(1000/LOOK_PER_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
