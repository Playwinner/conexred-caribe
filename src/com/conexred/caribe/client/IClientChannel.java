/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.conexred.caribe.client;

import java.io.IOException;
import java.net.SocketException;

/**
 *
 * @author Fabio Arias
 */
public interface IClientChannel {
    public boolean conectar() throws IOException, SocketException ;
    public void enviar(TopUpRequestDTO request) throws IOException ;
    public TopUpResponseDTO recibir() throws IOException;
    public void disconnect() throws IOException;
}
