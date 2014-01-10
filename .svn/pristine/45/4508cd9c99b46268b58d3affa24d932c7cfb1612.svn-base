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
public class ReplenishResponseVO implements java.io.Serializable{
    private String recordType = null;
    private String recordId = null;
    private int resultCode = 0;
    private int replenishAmount = 0;
    private int bonus = 0;

    public ReplenishResponseVO(String data){
        try{
            String[] d = StringUtil.separar(data, ",");
            this.recordType = d[0];
            this.recordId = d[1];
            this.resultCode = Integer.parseInt(d[2]);
            this.replenishAmount = Integer.parseInt(d[3]);
            this.bonus = Integer.parseInt(d[4]);
        }catch(Exception e){
            this.resultCode = -1;
        }
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
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

    public int getReplenishAmount() {
        return replenishAmount;
    }

    public void setReplenishAmount(int replenishAmount) {
        this.replenishAmount = replenishAmount;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
    
}
