package informatikprojekt.zigbee.backend;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IData {

    @Deprecated
    List<DataSet> getDataForTime(LocalDateTime from, LocalDateTime to);

    @Deprecated
    List<DataSet> getDataForTime(LocalDateTime from);

    Map<String, Float> getDailyCalcForType(String type, String calc) throws SQLException;

    List<SQLData> getStandardDeviationForType(String type) throws SQLException;

    List<String> getAllDataTypes() throws SQLException;

    List<Integer> getAllDevices() throws SQLException;

    List<String> getAllSensors() throws SQLException;

    List<ExportData> getExportList();

    List<ExportData> getExportListForRoom(String name);

    float get15MinAverage(String type);

    boolean existRoom(String name);

    void writeRoom(Room room) throws SQLException;

    Room readRoom(String name) throws SQLException;

    boolean hasRoomData(String name);

    void deleteRoom(String name);

    int getCurrentRecordID();

    List<String> getRecordingsForRoom(String room);

    void loadRecording(String time);

}
