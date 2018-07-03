package io.paysky.data.model.response.terminalconfig.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * Created by PaySky-3 on 8/9/2017.
 */


public class TABLE
{
    @Attribute(name = "HOST",required = false)
    private String HOST;

    @Attribute(name = "NAME",required = false)
    private String NAME;
    @Attribute(name = "TID",required = false)
    private String TID;

    @Attribute(name = "TMSCONF",required = false)
    private String TMSCONF;

    @Attribute(name = "COL",required = false)
    private String COL;
    @Attribute(name = "ROW",required = false)
    private String rawatt;



    @Attribute(name = "TMSSW",required = false)
    private String TMSSW;



    @ElementList(name = "COLUMN",inline=true,required = false)
    private List<COLUMN> COLUMN;
    @ElementList(name = "ROW",inline=true,required = false)
    private List<ROW> ROW;

    public String getHOST() {
        return HOST;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    public String getTMSCONF() {
        return TMSCONF;
    }

    public void setTMSCONF(String TMSCONF) {
        this.TMSCONF = TMSCONF;
    }

    public String getCOL() {
        return COL;
    }

    public void setCOL(String COL) {
        this.COL = COL;
    }

    public String getRawatt() {
        return rawatt;
    }

    public void setRawatt(String rawatt) {
        this.rawatt = rawatt;
    }

    public String getTMSSW() {
        return TMSSW;
    }

    public void setTMSSW(String TMSSW) {
        this.TMSSW = TMSSW;
    }

    public List<COLUMN> getCOLUMN() {
        return COLUMN;
    }

    public void setCOLUMN(List<COLUMN> COLUMN) {
        this.COLUMN = COLUMN;
    }

    public List<ROW> getROW() {
        return ROW;
    }

    public void setROW(List<ROW> ROW) {
        this.ROW = ROW;
    }
}