package informatikprojekt.zigbee.backend;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class DataManager implements IData {

    private final LinkedList<DataSet> dataSets = new LinkedList<>();
    private static UartReader uartReader;
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
            uartReader = new MockUartReader(port);
            uartReader.startReader();
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

    @Override
    public List<SQLData> getDailyMeanForType(String type, LocalDateTime from, LocalDateTime to) {
        return null;
    }

    @Override
    public List<SQLData> getDailyMinForType(String type, LocalDateTime from, LocalDateTime to) {
        return null;
    }

    @Override
    public List<SQLData> getDailyMaxForType(String type, LocalDateTime from, LocalDateTime to) {
        return null;
    }

    @Override
    public List<SQLData> getStandardDeviationForType(String type) {
        return null;
    }

    @Override
    public List<String> getAllDataTypes() {
        return null;
    }

    @Override
    public List<Integer> getAllDevices() {
        return null;
    }

    @Override
    public List<String> getAllSensors() {
        return null;
    }

    public void stopReader() {
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
