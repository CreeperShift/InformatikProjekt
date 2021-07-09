package informatikprojekt.zigbee.backend;

import java.sql.SQLException;

public class MockUartReader extends UartReader {

    public MockUartReader(String port) {
        super(port, "test");
    }

    @Override
    public void startReader() {
        activeState = State.CONNECTED;
        this.start();
    }

    @Override
    public void run() {
        while (activeState == State.CONNECTED) {

            String[] testData = new String[10];

            //String: 0;1;SCD30;3;CO2;350;Temperatur;30.5;Feuchtigkeit;40

            testData[0] = "0"; //Type: Data
            testData[1] = "1"; // DEVICE: 1
            testData[2] = "SCD30"; //SENSOR name
            testData[3] = "3"; //Databits
            testData[4] = "CO2"; //1. typ
            testData[5] = "350"; //1. data
            testData[6] = "Temperatur"; // 2. typ
            testData[7] = "30.5"; // 2. data
            testData[8] = "Feuchtigkeit"; // 3. typ
            testData[9] = "40"; // 3. data

            String data = "0;1;SCD30;3;CO2;350;Temperatur;250;Feuchtigkeit;100;";

            String[] dataSplit = data.split(";");


            try {
                //handleData(testData);
                handleData(dataSplit);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                Thread.sleep(1000 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
