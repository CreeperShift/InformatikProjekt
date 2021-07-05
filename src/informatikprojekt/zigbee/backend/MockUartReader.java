package informatikprojekt.zigbee.backend;

import informatikprojekt.zigbee.util.CommonUtils;

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
            for (int i = 1; i < 5; i++) {
                for (int f = 2; f < 4; f++) {
                    Random rand = new Random();
                    if (i > 1) {
                        SensorData data = new SensorData("", i, 1, "Temperatur", rand.nextInt(60));
                        sensorDataQueue.add(data);
                    }
                    SensorData data = new SensorData("", i, f, CommonUtils.CO2, rand.nextInt(300)+350);
                    sensorDataQueue.add(data);
                }
            }

            try {
                Thread.sleep(1000 * 7);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
