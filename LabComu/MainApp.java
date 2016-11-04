/**Codigo para los XBee**/
//package com.digi.xdmk.receiveanalogdata;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.*;
import java.io.IOException;

//Clase Principal
public class MainApp {
    
    // Dirección del puerto USB
    private static final String PORT = "/dev/ttyUSB0";

    private static final int BAUD_RATE = 9600;
    // Lectura de los puertos analogicos de los XBee.
    private static final IOLine HUMEDAD = IOLine.DIO0_AD0;
        private static final IOLine PRESION = IOLine.DIO1_AD1;
        private static final IOLine POSICION = IOLine.DIO3_AD3;
        
        
    //Muestreador de los datos.
    private static AnalogSampleListener listener = new AnalogSampleListener();

           
        
    public static void main(String[] args) {
        System.out.println("+----------------------------------------------+");
        System.out.println("|          Se recibió los datos                |");
        System.out.println("+----------------------------------------------+\n");
        
        XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
        
        try {
            myDevice.open();
            
            System.out.println("\nListening for IO samples... Rotate the potentiometer of any remote device.\n");
            
            myDevice.addIOSampleListener(listener);
            
        } catch (XBeeException e) {
            e.printStackTrace();
            myDevice.close();
            System.exit(1);
        }
    }
    
    //Esta clase administra los datos recibidos
    
    private static class AnalogSampleListener implements IIOSampleReceiveListener {
        @Override
                
        public void ioSampleReceived(RemoteXBeeDevice remoteDevice, IOSample ioSample) {
            if (ioSample.hasAnalogValue(HUMEDAD) || ioSample.hasAnalogValue(PRESION) || ioSample.hasAnalogValue(POSICION)) {
                int humeda_v = ioSample.getAnalogValue(HUMEDAD);
                                int presion_v = ioSample.getAnalogValue(PRESION);
                                int posicion_v = ioSample.getAnalogValue(POSICION);
                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date = new Date();
                                String [] Salida = new String[] {remoteDevice.get64BitAddress().toString(),dateFormat.format(date),Integer.toString(humeda_v),Integer.toString(presion_v),Integer.toString(posicion_v)};
                                //String prueba = Integer.toString(humeda_v);
                                                               
                                //FileOutputStream fop = null;
                                FileWriter out = null;
                                File file;
                                file = new File("/home/pi/Prueba.txt");
                                
                                try{
                                    out = new FileWriter(file,true);
                                    //fop = new FileOutputStream(file,true);
                                    out.write(Salida[0]);
                                    out.write(" ");
                                    out.write(Salida[1]);
                                    out.write(" ");
                                    out.write(Salida[2]);
                                    out.write(" ");
                                    out.write(Salida[3]);
                                    out.write(" ");
                                    out.write(Salida[4]);
                                    out.write(String.format("%n"));
                                    
                                    
                                //fop.flush();
                                    
                                }catch (IOException e) {
                                    e.printStackTrace();
                                    
                                }finally {
                                    try {
                                            if (out != null) {
                                                    out.close();
                                            }
                                    } catch (IOException e) {
                                            e.printStackTrace();
                                    }
                                }
                                
                System.out.println(remoteDevice.get64BitAddress()+
                                    ";" + dateFormat.format(date) +
                                    ";" + Integer.toString(humeda_v) +
                                    ";" + Integer.toString(presion_v) +
                                    ";" + Integer.toString(posicion_v));
            }
                        
        }
    }
}
