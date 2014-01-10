/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.conexred.caribe.client.vo;

import com.brainwinner.util.StringUtil;

/**
 *
 * @author fharias
 */
public class AccountQueryResponseVO implements java.io.Serializable {
    private String recordType = null;
    private String recordId = "";
    private int resultCode = 0;
    private String type = null;
    private String status = null;
    private String rechargable = null;
    private int balance = 0;
    private String balanceExpiration = null;
    private int freeMinutes = 0;
    private int freeMoney = 0;
    private int currentLanguage = 0;
    private int currentPin = 0;

    public AccountQueryResponseVO(String data){
        try{
            String[] d = StringUtil.separar(data, ",");
            this.recordType = d[0];
            this.recordId = d[1];
            this.resultCode = Integer.parseInt(d[2]);
            this.type = d[3];
            this.status = d[4];
            this.rechargable = d[5];
            this.balance = Integer.parseInt(d[6]);
            this.balanceExpiration = d[7];
            this.freeMinutes = Integer.parseInt(d[8]);
            this.freeMoney = Integer.parseInt(d[9]);
            this.currentLanguage = Integer.parseInt(d[10]);
            this.currentPin = Integer.parseInt(d[11]);
        }catch(Exception e){
            this.resultCode = -1;
        }
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getBalanceExpiration() {
        return balanceExpiration;
    }

    public void setBalanceExpiration(String balanceExpiration) {
        this.balanceExpiration = balanceExpiration;
    }

    public int getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(int currentLanguage) {
        this.currentLanguage = currentLanguage;
    }

    public int getCurrentPin() {
        return currentPin;
    }

    public void setCurrentPin(int currentPin) {
        this.currentPin = currentPin;
    }

    public int getFreeMinutes() {
        return freeMinutes;
    }

    public void setFreeMinutes(int freeMinutes) {
        this.freeMinutes = freeMinutes;
    }

    public int getFreeMoney() {
        return freeMoney;
    }

    public void setFreeMoney(int freeMoney) {
        this.freeMoney = freeMoney;
    }

    public String getRechargable() {
        return rechargable;
    }

    public void setRechargable(String rechargable) {
        this.rechargable = rechargable;
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

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
