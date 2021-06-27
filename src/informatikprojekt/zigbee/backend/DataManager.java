package informatikprojekt.zigbee.backend;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DataManager implements IData {

    private LinkedList<DataSet> dataSets = new LinkedList<>();
    private UartReader uartReader;
    Timer timer1;

    private static DataManager INSTANCE = null;

    private DataManager() {
    }

    public static DataManager get() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public void startReader() {

        if (uartReader == null) {

            uartReader = new UartReader();
            uartReader.setActive(true);
            uartReader.start();

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
                            for (DataSet s : dataSets) {
                                System.out.println(s.toString());
                            }
                        }
                    }
                }
            }, 500, 500);

            uartReader.setReaderActive(true);
        }
    }

    public boolean isConnected() {
        return uartReader.isConnected();
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
            uartReader.setActive(false);
        }
    }

    public void stopReaderActive() {
        uartReader.setReaderActive(false);
    }
}
