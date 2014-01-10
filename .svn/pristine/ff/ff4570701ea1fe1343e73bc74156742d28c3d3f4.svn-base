/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.conexred.caribe.client.vo;

/**
 *
 * @author fharias
 */
public class ReplenishRequestVO implements java.io.Serializable{
    private String recordType = "Replenish";
    private String recordId = "";
    private String subscriberNumber = null;
    private int amount = 0;
    private String replenishType = null;
    private String cardNumber = null;
    private String activationNumber = null;
    private String expirationUpdate = null;
    private String bonus = null;

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("\"").append(this.recordType).append("\",");
        sb.append("\"").append(this.recordId).append("\",");
        sb.append("\"").append(this.subscriberNumber).append("\",");
        sb.append(this.amount).append(",");
        sb.append("\"").append(this.replenishType).append("\",");
        sb.append("\"").append(this.cardNumber).append("\",");
        sb.append("\"").append(this.activationNumber).append("\",");
        sb.append("\"").append(this.expirationUpdate).append("\",");
        sb.append("\"").append(this.bonus).append("\"").append((char)13);
        return sb.toString();
    }

    public String getActivationNumber() {
        return activationNumber;
    }

    public void setActivationNumber(String activationNumber) {
        this.activationNumber = activationNumber;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationUpdate() {
        return expirationUpdate;
    }

    public void setExpirationUpdate(String expirationUpdate) {
        this.expirationUpdate = expirationUpdate;
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

    public String getReplenishType() {
        return replenishType;
    }

    public void setReplenishType(String replenishType) {
        this.replenishType = replenishType;
    }

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public void setSubscriberNumber(String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }
    
}
