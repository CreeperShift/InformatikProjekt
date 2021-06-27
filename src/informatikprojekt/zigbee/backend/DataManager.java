package informatikprojekt.zigbee.backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DataManager implements IData {

    private final LinkedList<DataSet> dataSets = new LinkedList<>();
    private static UartReader uartReader;
    private Timer timer1;
    private String port = "COM1";

    private static DataManager INSTANCE = null;

    private DataManager() {
    }

    public static DataManager get() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public static void stop() {
        if (uartReader != null) {
            uartReader.stop();
        }
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void startReader() {

        if (uartReader == null || isStopped() || isFailed()) {
            if (timer1 != null) {
                timer1.cancel();
            }

            uartReader = new UartReader(port);
            uartReader.startReader();

            timer1 = new Timer();
            timer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!uartReader.getQueue().isEmpty()) {
                        DataSet dataSet = new DataSet();

                        while (!uartReader.getQueue().isEmpty()) {
                            try {
                                SensorData data = uartReader.getQueue().take();
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                                data.setFormattedTime(dataSet.getTime().format(formatter));
                                dataSet.addSensorData(data);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        dataSets.add(dataSet);

                    }
                }
            }, 200, 500);

        }
    }

    @Override
    public List<DataSet> getDataForTime(LocalDateTime from, LocalDateTime to) {

        LinkedList<DataSet> returnData = new LinkedList<>();

        for (DataSet data : dataSets) {
            if (data.getTime().isAfter(from) && data.getTime().isBefore(to)) {
                returnData.add(data);
            }
        }

        return returnData;
    }

    @Override
    public List<DataSet> getDataForTime(LocalDateTime from) {
        return getDataForTime(from, LocalDateTime.now());
    }

    public void stopReader() {
        if (timer1 != null) {
            timer1.cancel();
        }
        if (uartReader != null) {
            uartReader.setReaderState(UartReader.State.ENDED);
        }
    }

    public final List<DataSet> getDataAll() {
        return dataSets;
    }

    public boolean isConnected() {
        return uartReader.getReaderState() == UartReader.State.CONNECTED;
    }


    public boolean isFailed() {
        return uartReader.getReaderState() == UartReader.State.FAILED;
    }

    public boolean isStopped() {
        return uartReader.getReaderState() == UartReader.State.ENDED;
    }
}
