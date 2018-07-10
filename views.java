import java.sql.*;
import java.util.Scanner;

final class views {
    final static String user = "dic9812";
    final static String password = "passsword"; //mysql password
    final static String db = "dic9812";
    final static String jdbc = "jdbc:mysql://localhost:3306/"+db+"?user="+user+"&password="+password;

    public static void main ( String[] args ) throws Exception {

      System.out.println("Airplane-Mode");

        Scanner reader = new Scanner(System.in);
        int mainMenuInput;
        boolean execute = true;

        createPlanesBookingsView();
        createAirportPlaneArrivalsView();

        do{
          mainMenu();
          mainMenuInput = reader.nextInt();
          reader.nextLine();

          switch(mainMenuInput){
            case 1:
              displayAirportPlaneArrivalsView();
              break;
            case 2:
              displayPlanesBookingsView();
              break;
            case 3:
              makeReservation();
              break;
          }
        }
        while(execute);

    }

    public static void mainMenu(){
      System.out.println(" ----------------------------------");
      System.out.println("|             Main Menu            |");
      System.out.println("|1. Display Airport Arrivals       |");
      System.out.println("|2. Display Plane Bookings         |");
      System.out.println("|3. Make a Reservation             |");
      System.out.println("|/q Quit                           |");
      System.out.println(" ----------------------------------");
      System.out.print("Input: ");
    }

    public static void createPlanesBookingsView() throws Exception{
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      Connection con = DriverManager.getConnection(jdbc);
      Statement stmt = con.createStatement();
      try {
          Statement st = con.createStatement();
          String code = "Create VIEW Planes_Bookings as SELECT pl.ID, pl.Maker, pl.Model, "
          + "fl.FLNO, fl.Seq, fl.FromA, fl.ToA, fl.DeptTime, fl.ArrTime "
          + "FROM Plane as pl "
          + "JOIN FlightLeg AS fl on pl.ID = fl.Plane";
          st.executeUpdate(code);

          System.out.println("Planes_Bookings successfully created!");
      } catch (SQLException s) {
          System.out.println("Planes_Bookings VIEW already exists!");
      }

      stmt.close();
      con.close();
    }

    public static void displayPlanesBookingsView()throws Exception{
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      Connection con = DriverManager.getConnection(jdbc);
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery("select * from Planes_Bookings");

      try {
        System.out.println("ID  Maker   Model FLNO  Seq FromA  ToA   DeptTime   ArrTime");
        while (rs.next())
            System.out.println(rs.getString("ID")+"   "+rs.getString("Maker")+"   "+rs.getString("Model")
            +"   "+rs.getString("FLNO")+"   "+rs.getString("Seq")+"   "+rs.getString("FromA")+"   "+rs.getString("ToA")
            +"   "+rs.getString("DeptTime")+"   "+rs.getString("ArrTime"));
        rs.close();
      }
      catch (SQLException s){
        System.out.println("Error");
      }

      stmt.close();
      con.close();
    }

    public static void createAirportPlaneArrivalsView() throws Exception{
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      Connection con = DriverManager.getConnection(jdbc);
      Statement stmt = con.createStatement();
      try {
          Statement st = con.createStatement();
          String code = "Create VIEW Airport_Plane_Arrivals as SELECT ai.Code, ai.City, ai.State, pl.Maker, pl.Model, "
          + "count(fl.ToA) as DailyArrivals "
          + "FROM FlightLeg as fl, Airport as ai, Plane as pl "
          + "WHERE fl.Plane = pl.ID AND fl.ToA = ai.Code "
          + "GROUP BY Code";
          st.executeUpdate(code);

          System.out.println("Airport_Plane_Arrivals successfully created!");
      } catch (SQLException s) {
          System.out.println("Airport_Plane_Arrivals VIEW already exists!");
      }

      stmt.close();
      con.close();
    }

    public static void displayAirportPlaneArrivalsView() throws Exception{
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      Connection con = DriverManager.getConnection(jdbc);
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery("select * from Airport_Plane_Arrivals");

      try {
        System.out.println("Code   City         State   Maker   Model   DailyArrivals");
        while (rs.next())
            System.out.println(rs.getString("Code")+"   "+rs.getString("City")+"     "+rs.getString("State")
            +"   "+rs.getString("Maker")+"   "+rs.getString("Model")+"   "+rs.getString("DailyArrivals"));
        rs.close();
      }
      catch (SQLException s){
        System.out.println("Error");
      }

      stmt.close();
      con.close();
    }

    public static void makeReservation() throws Exception{
      Scanner reader = new Scanner(System.in);
      int planeID = 0;
      int flightNum = 0;
      String fromA = null;
      String toA = null;
      String deptTime = null;
      String arrTime =  null;
      int matchingPlaneID = 0;
      int matchingFLNO;
      int matchingFromA = 0;
      int matchingToA = 0;

      Class.forName("com.mysql.jdbc.Driver").newInstance();
      Connection con = DriverManager.getConnection(jdbc);
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery("select * from Planes_Bookings");

      //check plane ID
      while(matchingPlaneID == 0){
        System.out.print("Enter Valid Plane ID: ");
        planeID = reader.nextInt();
        reader.nextLine();

        while (rs.next()){
            int existingPlaneID = rs.getInt("ID");
            if(existingPlaneID == planeID){
              matchingPlaneID += 1;
            }
          }
          rs = stmt.executeQuery("select * from Planes_Bookings");
      }

      //check flight Number
      do{
        matchingFLNO = 0;
        rs = stmt.executeQuery("select * from Planes_Bookings");
        System.out.print("Enter New Flight Number: ");
        flightNum = reader.nextInt();
        reader.nextLine();

        while(rs.next()){
          int existingFlightNumber = rs.getInt("FLNO");
          if(existingFlightNumber == flightNum){
            matchingFLNO += 1;
          }
        }
      }
      while(matchingFLNO > 0);

      //check from Airport
      while(matchingFromA == 0){
        rs = stmt.executeQuery("select * from Planes_Bookings");
        System.out.print("From What Airport? ");
        fromA = reader.nextLine();

        while (rs.next()){
            String existingFromA = rs.getString("FromA");
            if(existingFromA.equals(fromA)){
              matchingFromA += 1;
            }
          }
          rs = stmt.executeQuery("select * from Planes_Bookings");
      }

      //check to Airport
      while(matchingToA == 0){
        rs = stmt.executeQuery("select * from Planes_Bookings");
        System.out.print("To What Airport? ");
        toA = reader.nextLine();

        while (rs.next()){
            String existingToA = rs.getString("ToA");
            if(existingToA.equals(toA) && (!toA.equals(fromA))){
              matchingToA += 1;
            }
          }
          rs = stmt.executeQuery("select * from Planes_Bookings");
      }

      //check departure Time
      System.out.print("Departure Time(HH:MM:SS): ");
      deptTime = reader.nextLine();
      System.out.print("Arrival Time(HH:MM:SS) ");
      arrTime = reader.nextLine();

      //insert new tuple
      String add = "Insert INTO Flight(FLNO,Meal,Smoking) VALUES('"+flightNum+ "','"+0+"','"+0+"')";
      stmt.executeUpdate(add);
      add = "Insert INTO FlightLeg(FLNO,FromA,ToA,DeptTime,ArrTime,Plane) VALUES('"+flightNum+
      "','"+fromA+"','"+toA+"','"+deptTime+"','"+arrTime+"','"+planeID+"')";
      stmt.executeUpdate(add);
      System.out.println("Reservation Added!");


      rs.close();
      stmt.close();
      con.close();
    }

}
