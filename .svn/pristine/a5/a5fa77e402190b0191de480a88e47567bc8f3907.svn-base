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
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

/**
 *
 * @author fharias
 */
public class RecargaEvertecFormatter implements IFormatter {

    BWGConnection aCon = null;
    private static ReadPropertiesFile prop = ReadPropertiesFile.getInstance();
    private static String prefix = prop.getProperty("prefix");

    public RecargaEvertecFormatter(BWGConnection con) {
        aCon = con;
    }

    public ISOMsg erouted(ISOMsg msg) {
        ISOMsg response = null;
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
            response = (ISOMsg)msg.clone();
            con = aCon.getConnection();
            ps = con.prepareStatement("select * from procesarredeban(?)");
            ps.setString(1, msg.getString(41));
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("response").equals("00")) {
                    comercio = rs.getString("merchantId");
                    puntodeventa = rs.getString("location");
                    terminal = rs.getString("terminal");
                    provider = ISOUtil.zeropad(msg.getString(35).substring(4, 5), 2);
                    //provider = "01";
                    numero = msg.getString(35).substring(5, 15);
                    valor = Double.parseDouble(msg.getString(4)) / 100;
                    ps = con.prepareStatement("SELECT * FROM procesar(?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    ps.setString(1, "04"); // Transaction Type
                    ps.setString(2, msg.getString(37)); // Secuencia
                    ps.setString(3, comercio); // Merchant
                    //System.out.println("COmercio :: " + rs.getString("merchantId"));
                    ps.setString(4, puntodeventa); // location
                    //System.out.println("Punto de Venta :: " + rs.getString("location"));
                    ps.setString(5, terminal); // terminal
                    //System.out.println("Terminal ::: " + rs.getString("terminal"));
                    ps.setString(6, msg.getString(7)); // datetime
                    ps.setString(7, clave); // Clerk Id
                    ps.setString(8, provider); // Provider
                    ps.setDouble(9, valor); // Valor
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        if (rs.getString("responseCode").equals("101")) {
                            idTransaccion = rs.getInt("idTransaccion");
                            valor = rs.getDouble("valorSinIVU");
                            System.out.println("Valor Sin IVU "+valor);
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
                                channel = (IClientChannel) caller.getClientChannel(providerDTO.getClassChannel(), providerDTO);
                                if (channel != null) {
                                    channel.conectar();
                                    channel.enviar(requestDTO);
                                    responseDTO = channel.recibir();
                                    if (responseDTO != null && responseDTO.getEstado().equals("00")) {
                                        con = aCon.getConnection();
                                        ps = con.prepareStatement("SELECT * FROM procesarrecarga(?, ?, ?, ?, ?)");
                                        ps.setInt(1, idTransaccion);
                                        ps.setString(2, requestDTO.getNumero());
                                        ps.setString(3, responseDTO.getEstado());
                                        ps.setString(4, responseDTO.getNumeroAutorizacion());
                                        ps.setInt(5, 1);
                                        rs = ps.executeQuery();
                                        numeroAutorizacion = ISOUtil.zeropad(Integer.toString(idTransaccion), 12);
                                        response.set(new ISOField(38, numeroAutorizacion.substring(6)));
                                        response.set(new ISOField(39, "00"));
                                        response.set(new ISOField(100, prop.getProperty(prefix + ".evertec.institucion")));
                                    } else {
                                        if(responseDTO != null){
                                            responseOperador = responseDTO.getEstado();
                                        }else{
                                            responseOperador = "-1";
                                        }
                                        con = aCon.getConnection();
                                        ps = con.prepareStatement("SELECT * FROM procesarrecarga(?, ?, ?, ?, ?)");
                                        ps.setInt(1, idTransaccion);
                                        ps.setString(2, requestDTO.getNumero());
                                        ps.setString(3, responseOperador);
                                        ps.setString(4, "00000000");
                                        ps.setInt(5, 1);
                                        rs = ps.executeQuery();
                                        //----->
                                        ps = con.prepareStatement("select * from reversar(?)");
                                        ps.setInt(1, idTransaccion);
                                        rs = ps.executeQuery();
                                        response.set(new ISOField(39, "20")); // ERROR DEL OPERADOR
                                        response.set(new ISOField(38, ISOUtil.zeropad(Integer.toString(0), 6)));
                                        response.set(new ISOField(100, prop.getProperty(prefix + ".evertec.institucion")));
                                    }
                                } else {
                                    con = aCon.getConnection();
                                    ps = con.prepareStatement("select * from reversar(?)");
                                    ps.setInt(1, idTransaccion);
                                    rs = ps.executeQuery();
                                    response.set(new ISOField(39, "51")); // NO SE ENCONTRO LA IMPLEMENTACION DEL OPERADOR
                                    response.set(new ISOField(38, ISOUtil.zeropad(Integer.toString(0), 6)));
                                    response.set(new ISOField(100, prop.getProperty(prefix + ".evertec.institucion")));
                                }
                            } else {
                                System.err.println("PROVIDER "+provider+" error no encontrado provider");
                                response.set(new ISOField(39, "50")); // No encontro Terminal
                                response.set(new ISOField(38, ISOUtil.zeropad(Integer.toString(0), 6)));
                                response.set(new ISOField(100, prop.getProperty(prefix + ".evertec.institucion")));
                            }
                        } else {
                            response.set(new ISOField(39, rs.getString("responseCode").substring(1))); // No encontro Terminal
                            response.set(new ISOField(38, ISOUtil.zeropad(Integer.toString(0), 6)));
                            response.set(new ISOField(100, prop.getProperty(prefix + ".evertec.institucion")));
                        }
                    } else {
                        response.set(new ISOField(39, "93")); // Error al Ejecutar Sentencia
                        response.set(new ISOField(38, ISOUtil.zeropad(Integer.toString(0), 6)));
                        response.set(new ISOField(100, prop.getProperty(prefix + ".evertec.institucion")));
                    }
                } else {
                    response.set(new ISOField(39, "92")); // No encontro Terminal
                    response.set(new ISOField(38, ISOUtil.zeropad(Integer.toString(0), 6)));
                    response.set(new ISOField(100, prop.getProperty(prefix + ".evertec.institucion")));
                }
            } else {
                response.set(new ISOField(39, "99"));
                response.set(new ISOField(38, ISOUtil.zeropad(Integer.toString(0), 6)));
                response.set(new ISOField(100, prop.getProperty(prefix + ".evertec.institucion")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.set(new ISOField(39, "94")); // ERROR POR EXCEPTION
                response.set(new ISOField(38, ISOUtil.zeropad(Integer.toString(0), 6)));
                response.set(new ISOField(100, prop.getProperty(prefix + ".evertec.institucion")));
            } catch (Exception ee) {
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                rs = null;
                if (ps != null) {
                    ps.close();
                }
                ps = null;
                if (con != null) {
                    con.close();
                }
                con = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return response;
    }
}
