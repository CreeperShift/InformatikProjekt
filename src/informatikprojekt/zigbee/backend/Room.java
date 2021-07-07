package informatikprojekt.zigbee.backend;

import informatikprojekt.zigbee.frontend.Device;
import informatikprojekt.zigbee.util.LineGraph;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Room {

    private LineGraph roomGraph;
    private String name;
    private LocalDateTime created;
    private List<Device> deviceList;

    public Room(String name) {
        created = LocalDateTime.now();
        this.name = name;
        roomGraph = new LineGraph();
        deviceList = new ArrayList<>();
    }

    public void addDevice(Device device) {
        deviceList.add(device);
    }

    public void removeDevice(Device device) {
        deviceList.remove(device);
    }

    public List<Device> getDeviceList() {
        return deviceList;
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
