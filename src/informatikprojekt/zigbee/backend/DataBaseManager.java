package informatikprojekt.zigbee.backend;

import java.sql.*;


public class DataBaseManager {

    public static void main(String args[]) {
        try {


            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sensordaten","root","123");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from test");
            String query = " insert into sensor (TIMEATLOCATION, DEVICE, SENSOR, DATENTYP, WERT )" + " values (?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
/*
            while(rs.next()) {
                preparedStmt.setString();
                preparedStmt.setInt();
                preparedStmt.setString();
                preparedStmt.setString();
                preparedStmt.setFloat();
            }
            // execute the preparedstatement
            preparedStmt.execute();*/
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getInt(3));
            }
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


    }
}