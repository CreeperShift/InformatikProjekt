package informatikprojekt.zigbee.backend;

import informatikprojekt.zigbee.util.LineGraph;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Room {

    private LineGraph roomGraph;
    private String name;
    private LocalDateTime created;

    public Room(String name) {
        created = LocalDateTime.now();
        this.name = name;
        roomGraph = new LineGraph();
    }

    public LineGraph getRoomGraph() {
        return roomGraph;
    }

    public String getName() {
        return name;
    }

    public void setRoomGraph(LineGraph roomGraph) {
        this.roomGraph = roomGraph;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public String getCreatedFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
        return created.format(formatter);
    }

    public void clear() {
        roomGraph = new LineGraph();
    }
}
