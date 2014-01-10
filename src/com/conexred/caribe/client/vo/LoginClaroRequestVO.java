/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.conexred.caribe.client.vo;

/**
 *
 * @author fharias
 */
public class LoginClaroRequestVO implements java.io.Serializable {
    private String recordType = "Login";
    private String recordId = "";
    private String user = null;
    private String password = null;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("\"").append(this.recordType).append("\",");
        sb.append("\"").append(this.recordId).append("\",");
        sb.append("\"").append(this.user).append("\",");
        sb.append("\"").append(this.password).append("\"").append((char)13);
        return sb.toString();
    }
}
