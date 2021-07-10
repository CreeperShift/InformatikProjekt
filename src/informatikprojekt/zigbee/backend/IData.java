package informatikprojekt.zigbee.backend;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface IData {

    @Deprecated
    List<DataSet> getDataForTime(LocalDateTime from, LocalDateTime to);

    @Deprecated
    List<DataSet> getDataForTime(LocalDateTime from);

    List<Float> getDailyMeanForType(String type, LocalDateTime from, LocalDateTime to) throws SQLException;

    List<Float> getDailyMinForType(String type, LocalDateTime from, LocalDateTime to) throws SQLException;

    List<Float> getDailyMaxForType(String type, LocalDateTime from, LocalDateTime to) throws SQLException;

    List<SQLData> getStandardDeviationForType(String type) throws SQLException;

    List<String> getAllDataTypes() throws SQLException;

    List<Integer> getAllDevices() throws SQLException;

    List<String> getAllSensors() throws SQLException;

    float get15MinAverage(String type);

    boolean existRoom(String name);

    void writeRoom(Room room) throws SQLException;

    Room readRoom(String name) throws SQLException;

    boolean hasRoomData(String name);

    void deleteRoom(String name);

    int getCurrentRecordID();

}
