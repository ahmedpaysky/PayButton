package io.paysky.data.model.response.terminalconfig.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * Created by PaySky-3 on 8/10/2017.
 */


public class ROW
{
    @Attribute(name = "ID",required = false)
    private String ID;

    @ElementList(name = "COL",inline=true,required = false)
    private List<COL> COL;


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


    public List<COL> getCOL() {
        return COL;
    }

    public void setCOL(List<COL> COL) {
        this.COL = COL;
    }
}

