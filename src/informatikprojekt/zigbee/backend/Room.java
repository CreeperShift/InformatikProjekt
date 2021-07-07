package informatikprojekt.zigbee.backend;

import informatikprojekt.zigbee.util.LineGraph;

import java.time.LocalDateTime;

public class Room {

    private LineGraph roomGraph;
    private String name;
    private final LocalDateTime created;

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

    public LocalDateTime getCreated() {
        return created;
    }

    public void clear() {
        roomGraph = new LineGraph();
    }
}
