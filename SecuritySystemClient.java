/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.swing.JOptionPane;
import java.util.Scanner;
/**
 *
 * @author taha
 */
public class SecuritySystemClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        int portNumber = Integer.parseInt(args[0]);
        String username = args[1];
        String password = args[2];
        String IPAddress = "127.0.0.1";
         Socket MyClient = null;
        try {
           MyClient = new Socket(IPAddress, portNumber);
        }
        catch (IOException e) {
        System.out.println(e);
        }
        
        DataInputStream input = null;
    try {
       input = new DataInputStream(MyClient.getInputStream());
    }
    catch (IOException e) {
       System.out.println(e);
    }
    
    DataOutputStream output = null;
    try {
       output = new DataOutputStream(MyClient.getOutputStream());
    }
    catch (IOException e) {
       System.out.println(e);
    }
    
    int fileCounter=1;
    //Authorization 
    String datas = username+":"+password;
    byte[] data = datas.getBytes(StandardCharsets.US_ASCII);
    byte[] size = mesSizeArr(0,datas);
    output.write(size); 
    output.write(data); 
    
    int inMes= (int) input.read();
    if(inMes!=2)
        System.out.println("Server: Invalid");
    else{
         System.out.println("Server: OK");
         
         while(true){
         	inMes =-1;
	         //wait for responses
	         while(!(inMes == 1 || inMes== 4 || inMes==7)){
	             //System.out.println("no message");
	             inMes= (int) input.read();
	         };
	         //message arrived
	         if(inMes ==1){//keepalive
	             //send keepalive
	             System.out.println("Server: Keepalive");
	             byte[] mes = mesSizeArr(1,null);
	             output.write(mes);
	             System.out.println("Client: Keepalive sent");
	         }
	         else if(inMes == 4 ){//emergency
	            System.out.println("Server: Emergency");
	            //get data sent alarm or discard
                 byte[] size2 = new byte[2];
	            input.read(size2);
	            int num = (size2[1] & 0xFF) + ((size2[0] & 0xFF) * 256);
	            System.out.println(size2[0]);
	            System.out.println(size2[1]);
	            System.out.println(num);
                 byte[] myData = new byte[num];
                 input.read(myData);
	            String message = new String(myData);
	            //here
	            BufferedWriter writer = new BufferedWriter(new FileWriter("snapshot_"+(fileCounter++)+".txt"));
					writer.write(message);
	            System.out.println("Out of loop");
				writer.close();
				System.out.println("file closed");
	            Scanner scan = new Scanner(System.in);
	            System.out.print("Send alarm or discard (1/2):");
	            int userChoice = scan.nextInt();
	            if(userChoice==2){
	            	System.out.println("Client: Discard sent");
	            	byte[] mes = mesSizeArr(6,null);
	            	output.write(mes);
	            }
	            else if(userChoice==1){
	            	System.out.println("Client: alarm sent");
	            	byte[] mes = mesSizeArr(5,null);
	            	output.write(mes);
	            }
	         }
	         else if (inMes == 7) {// exit 
	         System.out.println("Server: Exit");
	             System.exit(0);
	         }
	         else
	             System.out.println("Unexpexted message code: "+ inMes);
		}
	}
	    
	    while ((inMes = input.read()) == -1);
	    System.out.println("message:"+ inMes);
    
    try {
           output.close();
           input.close();
       MyClient.close();
    } 
    catch (IOException e) {
       System.out.println(e);
    }
    }
    
    static byte[] mesSizeArr(int type,String datas){
        byte[] size = new byte[3];
        size[0]=(byte) type;
        
        if(datas != null){
            byte[] data = datas.getBytes(StandardCharsets.US_ASCII);
            size[1]=(byte) (data.length >> 8);
            size[2]=(byte) (data.length );
        }
        else 
        {
            size[1]=(byte) 0;
            size[2]=(byte) 0;
        }
        return size;
    }
            
            
            
}
