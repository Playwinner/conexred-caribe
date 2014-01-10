/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.conexred.caribe.client;

import com.brainwinner.connection.BWGConnection;
import com.brainwinner.dto.FormatterDTO;
import com.brainwinner.logic.HelperLogicSingleton;
import com.brainwinner.util.IFormatter;
import com.brainwinner.util.ObjectDynamicCaller;
import com.brainwinner.util.ReadPropertiesFile;
import com.conexred.caribe.channel.EvertecChannel;
import java.io.PrintStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.logging.Level;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;
import org.jpos.util.ThreadPool;

/**
 *
 * @author fharias
 */
public class EvertecClient extends Thread {

    private static ReadPropertiesFile prop = ReadPropertiesFile.getInstance();
    private String host = null;
    private static Date ultimoRecibido = new Date();
    private int port = 0;
    private EvertecChannel channel = null;
    private static String prefix = prop.getProperty("prefix");
    private static int minThread = Integer.parseInt(prop.getProperty(prefix + ".listener.minThread"));
    private static int maxThread = Integer.parseInt(prop.getProperty(prefix + ".listener.maxThread"));
    private ThreadPool poolMessage = new ThreadPool(minThread, maxThread);
    public static int trace = 1;
    public static int tiempo = Integer.parseInt(prop.getProperty("waittime"));
    public BWGConnection aCon = null;
    private Map<String, FormatterDTO> formatters = null;

    public EvertecClient() throws ClassNotFoundException, SQLException {
        this.setDaemon(true);
        aCon = BWGConnection.getInstance();
        formatters = HelperLogicSingleton.getInstance().getFormatters(1);
    }

    public void run() {
        ISOMsg receive = null;
        ISOMsg response = null;
        try {
            this.conectar();
            new WatchDog().start();
            while (true) {
                try {
                    System.out.println("ESTADO DEL CANAL " + channel.isConnected());
                    if (channel.isConnected()) {
                        receive = channel.receive();
                        poolMessage.execute(new Process(receive));
                    } else {
                        this.conectar();
                    }
                } catch (Exception e) {
                    System.err.println("ERROR: " + e.getMessage());
                    this.conectar();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void conectar() throws Exception {
        try {
            if (channel != null) {
                channel.disconnect();
            }
            channel = null;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            Logger logger = new Logger();
            PrintStream out = null;
            SimpleDateFormat sdf = null;
            host = prop.getProperty(prefix + ".evertec.host");
            port = Integer.parseInt(prop.getProperty(prefix + ".evertec.port"));
            ObjectDynamicCaller objDynCaller = new ObjectDynamicCaller();
            channel = new EvertecChannel(host, port, (ISOPackager) objDynCaller.getPackager(prop.getProperty(prefix + ".evertec.packager")), prop.getProperty(prefix + ".evertec.tpdu.host").getBytes());
            logger.addListener(new SimpleLogListener(System.out));
            channel.setLogger(logger, "EVERTEC-HANDSHAKE");
            channel.connect();
            ultimoRecibido = new Date();
            System.out.println("Conectado "+host+":"+port+" "+prop.getProperty(prefix + ".evertec.packager")+" : "+prop.getProperty(prefix + ".evertec.tpdu.host"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Process implements Runnable {

        ISOMsg msg = null;

        public Process(ISOMsg msg) {
            this.msg = msg;
            trace++;

        }

        public void run() {
            int MTI = 0;
            ISOMsg response = null;
            byte[] header = null;
            int[] fields = {0, 1, 3, 4, 7, 11, 12, 13, 14, 17, 22, 25, 32, 35, 37, 41, 48, 49, 60, 61, 120, 125, 127};
            int[] fieldsR = {0, 1, 3, 4, 7, 11, 32, 35, 37, 41, 49, 61, 90};
            IFormatter iFormatter = null;
            ObjectDynamicCaller objDynCaller = new ObjectDynamicCaller();

            try {

                //System.out.println(new String(response.getHeader()));
                java.text.SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
                java.util.Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
                format.setCalendar(cal);
                header = msg.getHeader();
                if (header != null) {
                    header[header.length - 1] = 0x35;
                }
                MTI = Integer.parseInt(msg.getMTI());
                switch (MTI) {
                    case 800:
                        response = (ISOMsg) msg.clone();
                        response.setResponseMTI();
                        response.set(new ISOField(7, format.format(new Date())));
                        response.set(new ISOField(39, "00"));
                        response.setHeader(header);
                        channel.send(response);
                        ultimoRecibido = new Date();
                        break;
                    case 200:
                        response = (ISOMsg) msg.clone(fields);
                        response.setResponseMTI();
                        response.set(new ISOField(7, format.format(new Date())));
                        response.setHeader(header);
                        iFormatter = (IFormatter) objDynCaller.getFormatter(formatters.get("200" + msg.getString(3)).getFormatterClass(), aCon);
                        if (iFormatter != null) {
                            response = iFormatter.erouted(response);
                            if (response == null) {
                                response.set(new ISOField(39, "99"));
                                response.set(new ISOField(38, "000000"));
                                response.set(new ISOField(100, prop.getProperty(prefix + ".evertec.institucion")));
                            }
                        } else {
                            response.set(new ISOField(39, "99"));
                            response.set(new ISOField(38, "000000"));
                            response.set(new ISOField(100, prop.getProperty(prefix + ".evertec.institucion")));
                        }
                        ///Thread.sleep(20000);
                        channel.send(response);
                        break;
                    case 420:
                        response = (ISOMsg) msg.clone(fieldsR);
                        response.setResponseMTI();
                        response.set(new ISOField(7, format.format(new Date())));
                        response.set(new ISOField(39, "00"));
                        response.setHeader(header);
                        iFormatter = (IFormatter) objDynCaller.getFormatter(formatters.get("420" + msg.getString(3)).getFormatterClass(), aCon);
                        if (iFormatter != null) {
                            response = iFormatter.erouted(response);
                            if (response == null) {
                                response.set(new ISOField(39, "99"));
                            }
                        } else {
                            response.set(new ISOField(39, "99"));
                        }
                        channel.send(response);
                        break;
                    case 421:
                        response = (ISOMsg) msg.clone(fieldsR);
                        response.setResponseMTI();
                        response.set(new ISOField(7, format.format(new Date())));
                        response.set(new ISOField(39, "00"));
                        response.setHeader(header);
                        channel.send(response);
                        break;
                    default:
                        response = (ISOMsg) msg.clone();
                        response.setResponseMTI();
                        response.set(new ISOField(7, format.format(new Date())));
                        response.set(new ISOField(39, "XX"));
                        response.setHeader(header);
                        channel.send(response);
                        break;
                }
                //while (!channel.isConnected());

            } catch (Exception e) {
                try {
                    e.printStackTrace();
                    conectar();
                    try {
                        channel.send(response);
                    } catch (ISOException ex) {
                        java.util.logging.Logger.getLogger(EvertecClient.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        java.util.logging.Logger.getLogger(EvertecClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(EvertecClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    class WatchDog extends Thread {

        public WatchDog() {
            setDaemon(true);
        }

        public void run() {
            long fin = 0l;
            long diff = 0l;
            try {
                while (true) {
                    fin = System.currentTimeMillis();
                    diff = fin - ultimoRecibido.getTime();

                    if (diff > 180000) {
                        System.err.println("WATCHDOG: " + diff + " ms");
                        conectar();
                    }
                    Thread.sleep(10000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
