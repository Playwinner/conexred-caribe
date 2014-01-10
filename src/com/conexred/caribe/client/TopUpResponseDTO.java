/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.conexred.caribe.client;

/**
 *
 * @author Fabio Arias
 */
public class TopUpResponseDTO implements java.io.Serializable {
    private String response = "99";
    private String numeroAutorizacion = "00000000000000";
    private String estado = "99";
    private String dataOriginal = null;

    public String getDataOriginal() {
        return dataOriginal;
    }

    public void setDataOriginal(String dataOriginal) {
        this.dataOriginal = dataOriginal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


    public String getNumeroAutorizacion() {
        return numeroAutorizacion;
    }

    public void setNumeroAutorizacion(String numeroAutorizacion) {
        this.numeroAutorizacion = numeroAutorizacion;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

}
