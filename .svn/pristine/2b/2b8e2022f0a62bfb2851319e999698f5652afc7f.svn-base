/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.conexred.caribe.listener;

import com.brainwinner.connection.BWGConnection;
import com.brainwinner.dto.FormatterDTO;
import com.brainwinner.dto.ServerDTO;
import com.brainwinner.logic.HelperLogicSingleton;
import com.brainwinner.util.ReadPropertiesFile;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;
import org.jpos.util.ThreadPool;

/**
 *
 * @author fharias
 */
public class ListenerEvertec implements ISORequestListener {

    private Map<String, FormatterDTO> formatters = null;
    private BWGConnection bwgConnection = null;
    private static ReadPropertiesFile prop = ReadPropertiesFile.getInstance();
    private static String prefix = prop.getProperty("prefix");
    private static int minThread = Integer.parseInt(prop.getProperty(prefix + ".listener.minThread"));
    private static int maxThread = Integer.parseInt(prop.getProperty(prefix + ".listener.maxThread"));
    private ThreadPool poolMessage = new ThreadPool(minThread, maxThread);
    private int idServidor = 0;

    public ListenerEvertec () throws ClassNotFoundException{
        super();
        bwgConnection = BWGConnection.getInstance();
    }

    public ListenerEvertec(int idServidor) throws ClassNotFoundException, SQLException{
        this();
        this.idServidor = idServidor;
        formatters = HelperLogicSingleton.getInstance().getFormatters(idServidor);
    }

    public ListenerEvertec(ServerDTO serverDTO) throws ClassNotFoundException, SQLException{
        this();
        this.idServidor = Integer.parseInt(serverDTO.getId());
        formatters = HelperLogicSingleton.getInstance().getFormatters(idServidor);
    }

    public boolean process(ISOSource source, ISOMsg msg) {
        boolean state = true;
        System.out.println("ID SERVIDOR = "+this.idServidor);
        System.out.println("Formateadores = "+this.formatters);
        try {
            poolMessage.execute(new Process(source, msg));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return state;
    }

    class Process implements Runnable {

        private ISOSource source = null;
        private ISOMsg msg = null;

        public Process(ISOSource source, ISOMsg msg) {
            this.source = source;
            this.msg = msg;
        }

        public void run(){
            ISOMsg response = (ISOMsg)this.msg.clone();
            try{
                response.set(new ISOField(39, "99"));
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try {
                    this.source.send(response);
                } catch (IOException ex) {
                    Logger.getLogger(ListenerEvertec.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ISOException ex) {
                    Logger.getLogger(ListenerEvertec.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }


}
