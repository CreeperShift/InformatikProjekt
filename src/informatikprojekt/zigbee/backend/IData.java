package informatikprojekt.zigbee.backend;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface IData {

    @Deprecated
    List<DataSet> getDataForTime(LocalDateTime from, LocalDateTime to);

    @Deprecated
    List<DataSet> getDataForTime(LocalDateTime from);

    List<SQLData> getDailyMeanForType(String type, LocalDateTime from, LocalDateTime to) throws SQLException;

    List<SQLData> getDailyMinForType(String type, LocalDateTime from, LocalDateTime to) throws SQLException;

    List<SQLData> getDailyMaxForType(String type, LocalDateTime from, LocalDateTime to) throws SQLException;

    List<SQLData> getStandardDeviationForType(String type) throws SQLException;

    List<String> getAllDataTypes() throws SQLException;

    List<Integer> getAllDevices() throws SQLException;

    List<String> getAllSensors() throws SQLException;

    boolean existRoom(String name);

    void writeRoom(Room room) throws SQLException;

    Room readRoom(String name) throws SQLException;

    void editRoom(Room room);

    boolean hasRoomData(String name);

    void deleteRoom(String name);
}
