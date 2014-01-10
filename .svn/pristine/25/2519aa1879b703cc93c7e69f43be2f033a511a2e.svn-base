/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.conexred.caribe.client;

import com.brainwinner.dto.ProviderDTO;
import com.brainwinner.util.LogSocketListener;
import com.brainwinner.util.ReadPropertiesFile;
import com.conexred.caribe.client.vo.AccountQueryRequestVO;
import com.conexred.caribe.client.vo.AccountQueryResponseVO;
import com.conexred.caribe.client.vo.LoginClaroRequestVO;
import com.conexred.caribe.client.vo.LoginClaroResponseVO;
import com.conexred.caribe.client.vo.ReplenishRequestVO;
import com.conexred.caribe.client.vo.ReplenishResponseVO;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author fharias
 */
public class ClaroClient implements IClientChannel {

    static ProviderDTO provider = null;
    private static Socket socket;
    private String host;
    private int port, timeout;
    private static String name = "CLARO-Channel";
    protected DataInputStream serverIn;
    protected DataOutputStream serverOut;
    private static LogSocketListener logger = null;
    private String auth = null;
    private boolean debug = false;
    protected boolean usable;
    protected boolean isPool = false;
    private static int local = 0;
    private static int actual = 1;
    private long inicio = 0;
    static ReadPropertiesFile prop = ReadPropertiesFile.getInstance();
    private static boolean login = false;
    private boolean prepaid = false;

    public ClaroClient(ProviderDTO proveedorDTO) {
        provider = proveedorDTO;
        timeout = provider.getTimeOut();
        getLogger();
    }

    private static void getConnection() throws IOException {
        if (socket == null) {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(provider.getServidor(), provider.getPuerto()), 5000);
            } catch (Exception e) {
                logger.log(name + " / TIMEOUT DE CONEXION " + e.getMessage());
            }
        } else {
            if (!socket.isConnected()) {
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }

            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(provider.getServidor(), provider.getPuerto()), 5000);
            } catch (Exception e) {
                logger.log(name + " / TIMEOUT DE CONEXION " + e.getMessage());
            }

        }
    }

    private static void getLogger() {
        if (logger == null) {
            PrintStream out = null;
            SimpleDateFormat sdf = null;
            sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
            try {
                out = new PrintStream(new FileOutputStream("/var/local/core/logs/" + name + "_" + sdf.format(new Date()) + ".log"));
                logger = new LogSocketListener(out);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void applyTimeout() throws SocketException {
        if (timeout >= 0 && socket != null) {
            socket.setSoTimeout(timeout);
        }
    }

    protected void applyTimeout(Socket socket) throws SocketException {
        if (timeout >= 0 && socket != null) {
            socket.setSoTimeout(timeout);
        }
    }

    public boolean isConnected() throws Exception {
        if (socket != null && usable && !socket.isClosed()) {
            return true;
        } else {
            return false;
        }
    }

    public void reconnect() throws IOException {
        disconnect();
        conectar();
    }

    public void disconnect() throws IOException {
        try {
            usable = false;
            if (serverIn != null) {
                serverIn.close();
                serverIn = null;
            }
            if (serverOut != null) {
                serverOut.close();
                serverOut = null;
            }
            logger.log(name + " Disconnect Completed");
            local--;
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(name + " Exception disconnect()" + e.getMessage());
        }
    }

    public boolean conectar() throws IOException, SocketException {
        boolean estado = false;
        try {
            getConnection();
            if (socket.isConnected()) {
                logger.log(name + " /" + this.socket.getInetAddress() + ":" + this.socket.getPort());
                serverIn = new DataInputStream(
                        new BufferedInputStream(this.socket.getInputStream()));
                serverOut = new DataOutputStream(
                        new BufferedOutputStream(this.socket.getOutputStream(), 2048));
                if (!login) {
                    login = login();
                }
                if (login) {
                    usable = true;
                    local++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(logger.getPrintStream());
        }
        return estado;
    }

    public boolean login() {
        boolean estado = false;
        LoginClaroRequestVO loginRequest = null;
        LoginClaroResponseVO loginResponse = null;
        byte[] d = new byte[2048];
        int longitud = 0;
        String data = null;
        try {
            loginRequest = new LoginClaroRequestVO();
            loginRequest.setUser(provider.getUsuario());
            loginRequest.setPassword(provider.getClave());
            logger.log(name + " LOGIN " + loginRequest.toString());
            synchronized (serverOut) {
                serverOut.writeBytes(loginRequest.toString());
                serverOut.flush();
            }
            synchronized (serverIn) {
                longitud = serverIn.read(d);
                data = new String(d, 0, longitud, "ISO-8859-1");
                loginResponse = new LoginClaroResponseVO(data);
                logger.log(name + " LOGIN " + data);
                if (loginResponse.getResultCode() == 0) {
                    estado = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(logger.getPrintStream());
        }
        return estado;
    }

    public boolean consulta(String numero) {
        boolean estado = true;
        AccountQueryRequestVO queryRequest = null;
        AccountQueryResponseVO queryResponse = null;
        byte[] d = new byte[2048];
        int longitud = 0;
        String data = null;
        try {
            queryRequest = new AccountQueryRequestVO();
            queryRequest.setSubscriberNumber(numero);
            logger.log(name + " CONSULTA " + queryRequest.toString());
            synchronized (serverOut) {
                serverOut.writeBytes(queryRequest.toString());
                serverOut.flush();
            }
            synchronized (serverIn) {
                longitud = serverIn.read(d);
                data = new String(d, 0, longitud, "ISO-8859-1");
                queryResponse = new AccountQueryResponseVO(data);
                if (queryResponse.getResultCode() == 0) {
                    estado = true;
                }
                logger.log(name + " CONSULTA " + data);
            }
        } catch (Exception e) {
            e.printStackTrace(logger.getPrintStream());
        }
        return estado;
    }

    public void enviar(TopUpRequestDTO request) throws IOException {
        ReplenishRequestVO replenishRequest = null;
        try {
            if (isConnected()) {
                prepaid = consulta(request.getNumero());
                if (prepaid) {
                    replenishRequest = new ReplenishRequestVO();
                    replenishRequest.setSubscriberNumber(request.getNumero());
                    replenishRequest.setActivationNumber("");
                    replenishRequest.setAmount(Integer.parseInt(request.getValor()) * 100);
                    replenishRequest.setCardNumber("");
                    replenishRequest.setReplenishType("CASH");
                    replenishRequest.setExpirationUpdate("");
                    replenishRequest.setRecordId(Integer.toString(request.getTransaccionId()));
                    logger.log(name + " REPLENISH " + replenishRequest.toString());
                    synchronized (serverOut) {
                        serverOut.writeBytes(replenishRequest.toString());
                        serverOut.flush();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(logger.getPrintStream());
        }
    }

    public TopUpResponseDTO recibir() throws IOException {
        TopUpResponseDTO response = null;
        ReplenishResponseVO replenishResponse = null;
        byte[] d = new byte[2048];
        String data = null;
        int longitud = 0;
        try {
            if (isConnected()) {
                response = new TopUpResponseDTO();
                if (prepaid) {
                    synchronized (serverIn) {
                        longitud = serverIn.read(d);
                        data = new String(d, 0, longitud, "ISO-8859-1");
                        replenishResponse = new ReplenishResponseVO(data);
                        if (replenishResponse.getResultCode() == 0) {
                            response.setEstado("00");
                            response.setResponse("00");
                            response.setNumeroAutorizacion(replenishResponse.getRecordId());
                        } else {
                            response.setEstado(Integer.toString(replenishResponse.getResultCode()));
                            response.setResponse(Integer.toString(replenishResponse.getResultCode()));
                        }
                    }
                } else {
                    response.setEstado("20");
                    response.setResponse("20");
                }
            }
        } catch (Exception e) {
            e.printStackTrace(logger.getPrintStream());
        }
        return response;
    }
}
