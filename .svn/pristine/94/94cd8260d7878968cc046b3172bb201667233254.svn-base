/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.conexred.caribe.client.vo;

/**
 *
 * @author fharias
 */
public class AccountQueryRequestVO implements java.io.Serializable {
    private String recorType = "AccountQuery";
    private String recordId = "";
    private String subscriberNumber = null;

    public String getRecorType() {
        return recorType;
    }

    public void setRecorType(String recorType) {
        this.recorType = recorType;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public void setSubscriberNumber(String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("\"").append(this.recorType).append("\",");
        sb.append("\"").append(this.recordId).append("\",");
        sb.append("\"").append(this.subscriberNumber).append("\"").append((char)13);
        return sb.toString();
    }
}
