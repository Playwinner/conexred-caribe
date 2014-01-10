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
public class LoginClaroResponseVO implements java.io.Serializable {

    private String recordType = null;
    private String recordId = null;
    private int resultCode = 0;

    public LoginClaroResponseVO(String data) {
        try {
            String[] d = StringUtil.separar(data, ",");
            this.recordType = d[0];
            this.recordId = d[1];
            this.resultCode = Integer.parseInt(d[2]);
        } catch (Exception e) {
            this.resultCode = -1;
        }
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
}
