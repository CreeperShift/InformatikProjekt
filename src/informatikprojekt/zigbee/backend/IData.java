package informatikprojekt.zigbee.backend;

import java.time.LocalDateTime;
import java.util.List;

public interface IData {

    List<DataSet> getDataForTime(LocalDateTime from, LocalDateTime to);

    List<DataSet> getDataForTime(LocalDateTime from);

}
