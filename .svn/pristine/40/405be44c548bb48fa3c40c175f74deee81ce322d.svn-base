/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2009 Alejandro P. Revilla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.conexred.caribe.channel;

import java.io.IOException;
import java.net.ServerSocket;

import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOFilter.VetoException;
import org.jpos.util.LogEvent;
import org.jpos.util.Logger;

/**
 * Talks with TCP based NACs
 * Sends [LEN][TPDU][ISOMSG]
 * (len=2 bytes network byte order)
 *
 * @author Alejandro P. Revilla
 * @version $Revision: 2706 $ $Date: 2009-03-05 09:24:43 -0200 (Thu, 05 Mar 2009) $
 * @see ISOMsg
 * @see ISOException
 * @see ISOChannel
 */
public class EvertecChannel extends BaseChannel {

    /**
     * Public constructor 
     */
    boolean tpduSwap = true;
    protected boolean overrideHeader;

    public EvertecChannel() {
        super();
    }

    /**
     * Construct client ISOChannel
     * @param host  server TCP Address
     * @param port  server port number
     * @param p     an ISOPackager
     * @param TPDU  an optional raw header (i.e. TPDU)
     * @see ISOPackager
     */
    public EvertecChannel(String host, int port, ISOPackager p, byte[] TPDU) {
        super(host, port, p);
        this.header = TPDU;
    }

    /**
     * Construct server ISOChannel
     * @param p     an ISOPackager
     * @param TPDU  an optional raw header (i.e. TPDU)
     * @exception IOException
     * @see ISOPackager
     */
    public EvertecChannel(ISOPackager p, byte[] TPDU) throws IOException {
        super(p);
        this.header = TPDU;
    }

    /**
     * constructs server ISOChannel associated with a Server Socket
     * @param p     an ISOPackager
     * @param TPDU  an optional raw header (i.e. TPDU)
     * @param serverSocket where to accept a connection
     * @exception IOException
     * @see ISOPackager
     */
    public EvertecChannel(ISOPackager p, byte[] TPDU, ServerSocket serverSocket)
            throws IOException {
        super(p, serverSocket);
        this.header = TPDU;
    }

    protected void sendMessageLength(int len) throws IOException {
        serverOut.write(len >> 8);
        serverOut.write(len);
    }

    protected int getMessageLength() throws IOException, ISOException {
        byte[] b = new byte[2];
        serverIn.readFully(b, 0, 2);
        return (int) (((((int) b[0]) & 0xFF) << 8)
                | (((int) b[1]) & 0xFF));
    }

    protected void sendMessageHeader(ISOMsg m, int len) throws IOException {
        byte[] h = m.getHeader();
        if (h != null) {
            if (tpduSwap && h.length == 5) {
                // swap src/dest address
                byte[] tmp = new byte[2];
                System.arraycopy(h, 1, tmp, 0, 2);
                System.arraycopy(h, 3, h, 1, 2);
                System.arraycopy(tmp, 0, h, 3, 2);
            }
        } else {
            h = header;
        }
        if (h != null) {
            serverOut.write(h);
        }
    }

    /**
     * New QSP compatible signature (see QSP's ConfigChannel)
     * @param header String as seen by QSP
     */
    public void setHeader(String header) {
        super.setHeader(ISOUtil.str2bcd(header, false));
    }

    public void setConfiguration(Configuration cfg)
            throws ConfigurationException {
        super.setConfiguration(cfg);
        tpduSwap = cfg.getBoolean("tpdu-swap", true);
    }
    
    protected int getHeaderLength(ISOMsg m) {                                   
        return (!overrideHeader && m.getHeader() != null) ?
            m.getHeader().length : getHeaderLength();
    }

    /**
     * sends an ISOMsg over the TCP/IP session
     * @param m the Message to be sent
     * @exception IOException
     * @exception ISOException
     * @exception ISOFilter.VetoException;
     */
    public void send(ISOMsg m)
            throws IOException, ISOException, VetoException {
        LogEvent evt = new LogEvent(this, "send");
        try {
            if (!isConnected()) {
                throw new ISOException("unconnected ISOChannel");
            }
            m.setDirection(ISOMsg.OUTGOING);
            m = applyOutgoingFilters(m, evt);
            evt.addMessage(m);
            m.setDirection(ISOMsg.OUTGOING); // filter may have drop this info
            m.setPackager(getDynamicPackager(m));
            byte[] b = new byte[m.pack().length+1];
            System.arraycopy(m.pack(), 0, b, 0, (m.pack().length));
            b[m.pack().length] = 0x03;
            synchronized (serverOut) {
                sendMessageLength(b.length + getHeaderLength(m));
                sendMessageHeader(m, b.length);
                sendMessage(b, 0, b.length);
                //sendMessageTrailler(m, b);
                serverOut.flush();
            }
            cnt[TX]++;
            setChanged();
            notifyObservers(m);
        } catch (VetoException e) {
            //if a filter vets the message it was not added to the event
            evt.addMessage(m);
            evt.addMessage(e);
            throw e;
        } catch (ISOException e) {
            evt.addMessage(e);
            throw e;
        } catch (IOException e) {
            evt.addMessage(e);
            throw e;
        } catch (Exception e) {
            evt.addMessage(e);
            throw new ISOException("unexpected exception", e);
        } finally {
            Logger.log(evt);
        }
    }
}

