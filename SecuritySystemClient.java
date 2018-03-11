/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.swing.JOptionPane;
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
        String IPAddress = "192.168.56.1"; // Change to your own IP Address
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
         inMes =-1;
         //wait for responses
         while(inMes == -1){
             System.out.println("no message");
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
         }
         else if (inMes == 7) // exit 
             return;
         else
             System.out.println("Unexpexted message code: "+ inMes);
    }
        
   inMes = -1;
    
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
