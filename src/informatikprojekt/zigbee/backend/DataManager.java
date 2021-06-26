package informatikprojekt.zigbee.backend;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DataManager implements IData {

    private LinkedList<DataSet> dataSets = new LinkedList<>();
    private UartReader uartReader;
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

    public void setPort(String port) {
        this.port = port;
    }

    public void startReader() {

        if (uartReader == null || isStopped() || isFailed()) {
            if (timer1 != null) {
                timer1.cancel();
            }

            uartReader = new MockUartReader(port);
            uartReader.startReader();

            timer1 = new Timer();
            timer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!uartReader.getQueue().isEmpty()) {
                        DataSet dataSet = new DataSet();

                        while (!uartReader.getQueue().isEmpty()) {
                            try {
                                dataSet.addSensorData(uartReader.getQueue().take());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        dataSets.add(dataSet);

                        if (!dataSets.isEmpty()) {
                            System.out.println(dataSets.get(dataSets.size()-1));
                        }
                    }
                }
            }, 500, 500);

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
