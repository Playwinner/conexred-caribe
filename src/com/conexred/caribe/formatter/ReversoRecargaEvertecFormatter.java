/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.conexred.caribe.formatter;

import com.brainwinner.connection.BWGConnection;
import com.brainwinner.dao.ProviderDAO;
import com.brainwinner.dto.ProviderDTO;
import com.brainwinner.util.IFormatter;
import com.brainwinner.util.ObjectDynamicCaller;
import com.brainwinner.util.ReadPropertiesFile;
import com.conexred.caribe.client.IClientChannel;
import com.conexred.caribe.client.TopUpRequestDTO;
import com.conexred.caribe.client.TopUpResponseDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

/**
 *
 * @author fharias
 */
public class ReversoRecargaEvertecFormatter implements IFormatter {

    BWGConnection aCon = null;
    private static ReadPropertiesFile prop = ReadPropertiesFile.getInstance();
    private static String prefix = prop.getProperty("prefix");

    public ReversoRecargaEvertecFormatter(BWGConnection con) {
        aCon = con;
    }

    public ISOMsg erouted(ISOMsg msg) {
        ISOMsg response = (ISOMsg) msg.clone();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        String comercio = null;
        String puntodeventa = null;
        String terminal = null;
        ObjectDynamicCaller caller = new ObjectDynamicCaller();
        String clave = "20111124-everTec";
        double valor = 0;
        String provider = "";
        String numero = "";
        TopUpRequestDTO requestDTO = null;
        TopUpResponseDTO responseDTO = null;
        IClientChannel channel = null;
        ProviderDTO providerDTO = null;
        ProviderDAO providerDAO = null;
        int idTransaccion = 0;
        String responseOperador = null;
        String numeroAutorizacion = null;
        try {
            con = aCon.getConnection();
            ps = con.prepareStatement("select * from procesarredeban(?)");
            ps.setString(1, msg.getString(41));
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("response").equals("00")) {
                    comercio = rs.getString("merchantId");
                    puntodeventa = rs.getString("location");
                    terminal = rs.getString("terminal");
                    System.out.println("COM:"+comercio+";PDV:"+puntodeventa+";TERMINAL:"+terminal);
                    provider = ISOUtil.zeropad(msg.getString(35).substring(4, 5), 2);
                    numero = msg.getString(35).substring(5, 15);
                    valor = Double.parseDouble(msg.getString(4)) / 100;
                    ps = con.prepareStatement("select * from prereverso(?,?,?,?,?,?,?)");
                    ps.setString(1, comercio);
                    ps.setString(2, puntodeventa);
                    ps.setString(3, terminal);
                    ps.setString(4, clave);
                    ps.setString(5, msg.getString(37));
                    ps.setDouble(6, valor);
                    ps.setString(7, numero);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        System.out.println("EVERTEC:REVERSO;" + numero + ";" + valor + ";" + provider + ";" + msg.getString(37)+":: "+rs.getString("response"));
                        if (rs.getString("response").equals("101")) {
                            String idOperador = rs.getString("idOperador");
                            idTransaccion = rs.getInt("idTransaccion");
                            valor = rs.getDouble("valorSinIVU");
                            System.out.println("Valor Sin IVU " + valor);
                            providerDAO = new ProviderDAO(con);
                            providerDTO = providerDAO.consultarPorCodigoProducto(provider);
                            if (providerDTO != null) {
                                con.close();
                                con = null;
                                requestDTO = new TopUpRequestDTO();
                                requestDTO.setNumero(numero);
                                requestDTO.setTerminalId(terminal);
                                requestDTO.setTransaccionId(idTransaccion);
                                requestDTO.setValor(Double.toString(valor));
                                requestDTO.setIdOperador(idOperador);
                                channel = (IClientChannel) caller.getClientChannel(providerDTO.getClassChannel(), providerDTO);
                                if (channel != null) {
                                    channel.conectar();
                                    channel.enviar(requestDTO);
                                    responseDTO = channel.recibir();
                                    if (responseDTO != null && responseDTO.getEstado().equals("00")) {
                                        System.out.println("REVERSANDO SIN ERROR "+idTransaccion);
                                        con = aCon.getConnection();
                                        ps = con.prepareStatement("select * from reversar(?)");
                                        ps.setInt(1, idTransaccion);
                                        rs = ps.executeQuery();
                                        response.set(new ISOField(39, "00"));
                                    } else {
                                        System.out.println("REVERSANDO CON ERROR "+idTransaccion);
                                        con = aCon.getConnection();
                                        ps = con.prepareStatement("select * from reversar(?,?)");
                                        ps.setInt(1, idTransaccion);
                                        ps.setString(2, "3");
                                        rs = ps.executeQuery();
                                        response.set(new ISOField(39, "00"));
                                    }
                                }else{
                                    response.set(new ISOField(39, "20"));
                                }
                            } else {
                                response.set(new ISOField(39, "20"));
                            }
                        } else {
                            response.set(new ISOField(39, "90"));// Transaccion no existe
                        }
                    } else {
                        response.set(new ISOField(39, "99"));

                    }
                } else {
                    response.set(new ISOField(39, "92")); // No encontro Terminal
                }
            } else {
                response.set(new ISOField(39, "99"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(ReversoRecargaEvertecFormatter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return response;
    }
}
