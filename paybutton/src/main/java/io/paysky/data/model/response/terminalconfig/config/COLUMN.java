package io.paysky.data.model.response.terminalconfig.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

/**
 * Created by PaySky-3 on 8/10/2017.
 */



public class COLUMN
{
    @Attribute(name = "NAME",required = false)
    private String NAME;
    @Attribute(name = "COLNUM",required = false)
    private String COLNUM;


    @Text(required = false)
    private String text;

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getCOLNUM() {
        return COLNUM;
    }

    public void setCOLNUM(String COLNUM) {
        this.COLNUM = COLNUM;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
