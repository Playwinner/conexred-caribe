/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.conexred.caribe.client.vo;

/**
 *
 * @author fharias
 */
public class CreditRequestVO implements java.io.Serializable{
    private String recordType = "Credit";
    private String recordId = "";
    private String subscriberNumber1 = null;
    private String subscriberNumber2 = null;
    private String transcode = null;
    private int realMoney = 0;
    private int freeMoney = 0;
    private String secureDate = null;
    private String description = null;

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("\"").append(this.recordType).append("\",");
        sb.append("\"").append(this.recordId).append("\",");
        sb.append("\"").append(this.subscriberNumber1).append("\",");
        sb.append("\"").append(this.subscriberNumber2).append("\",");
        sb.append("\"").append(this.transcode).append("\",");
        sb.append(this.realMoney).append(",");
        sb.append(this.freeMoney).append(",");
        sb.append("\"").append(this.secureDate).append("\",");
        sb.append("\"").append(this.description).append("\"").append((char)13);
        return sb.toString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFreeMoney() {
        return freeMoney;
    }

    public void setFreeMoney(int freeMoney) {
        this.freeMoney = freeMoney;
    }

    public int getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(int realMoney) {
        this.realMoney = realMoney;
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

    public String getSecureDate() {
        return secureDate;
    }

    public void setSecureDate(String secureDate) {
        this.secureDate = secureDate;
    }

    public String getSubscriberNumber1() {
        return subscriberNumber1;
    }

    public void setSubscriberNumber1(String subscriberNumber1) {
        this.subscriberNumber1 = subscriberNumber1;
    }

    public String getSubscriberNumber2() {
        return subscriberNumber2;
    }

    public void setSubscriberNumber2(String subscriberNumber2) {
        this.subscriberNumber2 = subscriberNumber2;
    }

    public String getTranscode() {
        return transcode;
    }

    public void setTranscode(String transcode) {
        this.transcode = transcode;
    }
    
}
