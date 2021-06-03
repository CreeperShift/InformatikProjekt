package informatikprojekt.zigbee.backend;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class DataManager implements IData {

    private LinkedList<DataSet> dataSets = new LinkedList<>();

    private static DataManager INSTANCE;

    private DataManager() {
    }

    public DataManager get() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
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
}
