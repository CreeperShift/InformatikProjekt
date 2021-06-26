package informatikprojekt.zigbee.backend;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MockUartReader extends UartReader {

    public MockUartReader(String port) {
        super(port);
    }

    private final ArrayBlockingQueue<SensorData> sensorDataQueue = new ArrayBlockingQueue<>(100);

    @Override
    public void startReader() {
        activeState = State.CONNECTED;
        this.start();
    }

    @Override
    public BlockingQueue<SensorData> getQueue() {
        return sensorDataQueue;
    }

    @Override
    public void run() {
        while (activeState == State.CONNECTED) {
            for (int i = 0; i < 4; i++) {
                for (int f = 0; f < 2; f++) {
                    Random rand = new Random();
                    if (i > 1) {
                        SensorData data = new SensorData("", i, 0, 0, rand.nextInt(100));
                        sensorDataQueue.add(data);
                    }
                    SensorData data = new SensorData("", i, f, f, rand.nextInt(100));
                    sensorDataQueue.add(data);
                }
            }

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
