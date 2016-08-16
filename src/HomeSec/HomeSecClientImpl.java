package HomeSec;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.*;
import java.net.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.imageio.*;

/**
 * Java Class defines a RESTful Web Service. 
 * The Server is started by the classes main routine.
 * 
 * @author khaves
 */
@WebService(serviceName = "HomeSecClientService", portName = "HomeSecClientPort",
endpointInterface = "HomeSec.HomeSecClient")
public class HomeSecClientImpl implements HomeSecClient {
    static private HttpServer server;
    static private Endpoint endpoint;
        
    @Override
    public synchronized void stopSvc() {
        int sec = 10;
        System.out.println("Stopping server after " + sec + " seconds.");
        try {
            Thread.sleep(sec*1000);
        } catch (java.lang.InterruptedException ie) {
            System.out.println("Cannot delay server stop!");
        }
        System.out.println("Stopping server now.");
        endpoint.stop();
        server.stop(0);
    }
    
    public static void main(String[] args) {
        try {
            server = HttpServer.create(new InetSocketAddress(8180), 25);
            HttpContext context = server.createContext("/HomeSec");
            endpoint = Endpoint.create(new HomeSecClientImpl());
            endpoint.publish(context);
            server.start();
            System.out.println ("Service started!");
        } catch (Exception e) {
            System.out.println ("Exception during start of HomeSecClientService " + e);
        }
    }

    /**
     * Take a Picture.
     * Create Directory and take Picture. 
     * 
     * @param ImgDir Path to image directory
     * @return Path to taken Picture
     */
    @Override
    public String takePicture(String ImgDir) {
        try {
            String day,time,path,command;
            day=new SimpleDateFormat("dd.MM.yyy").format(Calendar.getInstance().getTime());
            time=new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());
            new File(ImgDir+day).mkdirs();
            //Create Directory an set path for image

            path=ImgDir+day;
            new File(path).mkdirs();
            path=path+"/"+time+".jpg";
            System.out.println("Neues Foto wird aufgenommen!");
            //Excute command to take picture
            command="raspistill -q 25 -h 1080 -w 1920 -hf -vf -o "+path;
            Process p=Runtime.getRuntime().exec(command);
            p.waitFor();
            System.out.println(command+" ausgefuehrt!");
            return path;
        } catch (IOException|InterruptedException ex ) {
            System.err.println("Error in takePicture()!! "+ex);
        } 
      return null;
    }
        
    /**
     * Send a Picture.
     * Connect to Server Socket and send Picture. 
     * 
     * @param ip IP of server
     * @param path Path to image
     * @return True if Picture was sent. False if error occured.
     */
    @Override
    public boolean sendPicture(String ip, String path) {
        Socket CliSock = null;
        BufferedImage bimg=null;
        boolean bSend=false;

        try {
            //Connect to Socket
            CliSock = new Socket(ip, 8998);
            System.out.println("Socket connected!!!!");
            //Read image and send it so server
            bimg = ImageIO.read(new File(path));
            System.out.println("Image "+path+" read!!!!");
            bSend=ImageIO.write(bimg,"JPG",CliSock.getOutputStream());
            System.out.println("Image sent!!!!\r\n");
            
            CliSock.close();
            
        } catch (IOException ex) {
            System.err.println("Error in sendPicture()!! "+ex);
        }
    return bSend;
    }
}

  