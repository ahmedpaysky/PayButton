package io.paysky.data.model.response.terminalconfig.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

/**
 * Created by PaySky-3 on 8/10/2017.
 */

public class COL
{
    @Text(required = false)
    private String text;

    @Attribute(name = "ID",required = false)
    private String ID;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}