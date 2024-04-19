import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.io.*;
import java.io.File;
import java.util.Random;
import java.text.SimpleDateFormat;

class ProcessQuery implements Runnable
{
    protected Socket socketConnection;

    public ProcessQuery(Socket clientSocket)
    {
        this.socketConnection =  clientSocket;
    }

    public synchronized void run()
    {
        
        // Configurations
        String url = "jdbc:postgresql://localhost:5432/dbms_project";
        String user = "postgres";
        String password = "123456";
        Connection c = null;
        Statement stmt = null;

        try
            {

                Class.forName("org.postgresql.Driver");
                c = DriverManager.getConnection(url, user, password);
                System.out.println("Opened database successfully");
                stmt = c.createStatement();
                
                //  Reading data from client
                InputStreamReader inputStream = new InputStreamReader(socketConnection
                                                                    .getInputStream()) ;
                BufferedReader bufferedInput = new BufferedReader(inputStream) ;
                OutputStreamWriter outputStream = new OutputStreamWriter(socketConnection
                                                                        .getOutputStream()) ;
                BufferedWriter bufferedOutput = new BufferedWriter(outputStream) ;
                PrintWriter printWriter = new PrintWriter(bufferedOutput, true) ;
                String clientCommand = "" ;
                String sql = " ";
                
                // Read client query from the socket endpoint
                clientCommand = bufferedInput.readLine(); 

                while(!clientCommand.equals("#")){
                    String str = clientCommand;
                    StringTokenizer sToken = new StringTokenizer(str, " ");
                    int a = Integer.parseInt(sToken.nextToken());
                    String[] name_array = new String[a];

                    int i=0;
                    while (i < a) {
                        name_array[i] = (sToken.nextToken());
                        if (i != a - 1) {
                            name_array[i] = name_array[i].substring(0, name_array[i].length() - 1);
                        }
                        i++;
                    }
                
                    int trainNum = Integer.parseInt(sToken.nextToken());
                    String journeyDate = sToken.nextToken();
                    String coachType = sToken.nextToken();
                    int filledSeats = -1;
                    int totalSeats = 0;
                    int remainingSeats = 0;
                    String pnr = "";
            
                    int flag = 0;

                    while(true){
                        try{
                            String classType = "";
                            if(coachType.equals("AC")) {
                                classType="ac";
                            }
                            else {
                                classType="sl";
                            }

                            sql = "select " + classType + "_filled as " + classType + "1, " + classType + "_total as " + classType + "2 from seats_filled where doj = ? and train_no = ?";
                            PreparedStatement stmts = c.prepareStatement(sql);
                            stmts.setInt(2, trainNum);
                            stmts.setString(1, journeyDate);
                            ResultSet res = stmts.executeQuery();
                            
                            while(res.next()){
                                filledSeats = res.getInt(classType + "1");
                                totalSeats = res.getInt(classType + "2");
                            }

                            remainingSeats = totalSeats - filledSeats;
                            // System.out.println(filledSeats);
                            
                            if(remainingSeats < a) {
                                printWriter.println("No ticket booked\n");
                                printWriter.println("-------------------------------------------------------------------------------------------\n");
                                flag = 1;
                                break;
                            }

                            sql = "UPDATE seats_filled set " + classType + "_filled = ? where doj = ? and train_no = ?;";
                            PreparedStatement stmts2 = c.prepareStatement(sql);
                            stmts2.setInt(1, filledSeats + a);
                            stmts2.setString(2, journeyDate);
                            stmts2.setInt(3, trainNum);
                            
                            while(true){
                                try{
                                    stmts2.execute();
                                    break;
                                }
                                catch(Exception ee){
                                    System.err.println(ee.getMessage());
                                    continue;
                                }
                            }

                            Random rand = new Random();
                            pnr = "" + rand.nextInt(10000);
                            if(coachType.equals("AC")){
                                pnr = pnr + "0";
                            }
                            else{
                                pnr = pnr + "1";
                            }
                            int date = (journeyDate.charAt(8)-'0') * 10 + journeyDate.charAt(9) - '0';
                            int month = (journeyDate.charAt(5)-'0') * 10 + journeyDate.charAt(6) - '0';
                            int year = (journeyDate.charAt(2)-'0') * 10 + journeyDate.charAt(3) - '0';
                            pnr = pnr + trainNum + ((filledSeats - 1)/18 + 1) + (((filledSeats-1) % 18) + 1) + date + month + year;                            

                            for(i = 0; i < a; i++){
                                PreparedStatement stmt1 = c.prepareStatement("call booking(?,?,?,?,?,?);");
                                stmt1.setInt(1,filledSeats++);
                                stmt1.setString(2, pnr);
                                stmt1.setString(3,name_array[i]);
                                stmt1.setInt(4,trainNum);
                                stmt1.setString(5,journeyDate);
                                stmt1.setString(6,coachType);
                                stmt1.execute();                        
                            }
                            flag = 1;
                            break;  
                        }catch(Exception ee){
                            System.err.println(ee.getMessage());
                        }
                    } 
                    if(flag == 0){
                        clientCommand = bufferedInput.readLine();
                        continue;
                    }

                    sql = "select * from tickets_data where pnr = ?";
                    PreparedStatement stmt1 = c.prepareStatement(sql);
                    stmt1.setString(1, pnr);                

                    ResultSet rs = stmt1.executeQuery();
                    printWriter.println("PNR\t\t\t\tName\tCoach\tBerth\tType\tTrainNo\tDate\n");
                    while (rs.next()) {
                        String pnr_ = rs.getString("pnr");
                        String passenger_name = rs.getString("passenger_name");
                        int coachNumber  = rs.getInt("coach_number");
                        int berthNumber  = rs.getInt("berth_number");
                        String berthType = rs.getString("berth_type");
                        int trNum  = rs.getInt("train_number");
                        String doj = rs.getString("doj");
                        printWriter.println(pnr_+ "\t" + passenger_name + "\t" + coachNumber + "\t\t" + berthNumber + "\t\t" + berthType + "\t" + trNum +"\t" + doj+"\n");                   
                    }
                
                    
                    
                    printWriter.println("-------------------------------------------------------------------------------------------\n");
                    rs.close();
                    stmt1.close();
                    clientCommand = bufferedInput.readLine(); 
                }

                inputStream.close();
                bufferedInput.close();
                outputStream.close();
                bufferedOutput.close();
                printWriter.close();
                socketConnection.close();
            }
            catch(Exception e)
            {
                System.err.println(e.getMessage());
                return;
            }
        }
}


public class ServiceModule{
    // Server listens to port
    static int serverPort = 7008;
    // Max no of parallel requests the server can process
    static int numServerCores = 5;         
    //------------ Main----------------------
    public static void main(String[] args) throws IOException 
    {    
        // Creating a thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(numServerCores);
        
        try (//Creating a server socket to listen for clients
        ServerSocket serverSocket = new ServerSocket(serverPort)) {
            Socket socketConnection = null;
            
            // Always-ON server
            while(true)
            {
                System.out.println("Listening port : " + serverPort 
                                    + "\nWaiting for clients...");
                socketConnection = serverSocket.accept();   // Accept a connection from a client
                System.out.println("Accepted client :" 
                                    + socketConnection.getRemoteSocketAddress().toString() 
                                    + "\n");
                //  Create a runnable task
                Runnable runnableTask = new ProcessQuery(socketConnection);
                //  Submit task for execution   
                executorService.submit(runnableTask);   
            }
        }
    }
}

