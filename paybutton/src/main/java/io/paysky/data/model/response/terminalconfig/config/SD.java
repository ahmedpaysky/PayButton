package io.paysky.data.model.response.terminalconfig.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 * Created by PaySky-3 on 8/10/2017.
 */

@Root
public class SD
{
    @Attribute(name = "PID",required = false)
    private int PID;

    @Text(required = false)
    private String text;

    @Attribute(name = "PName",required = false)
    private String PName;

    public String getText()
    {
        return text;
    }


    public void setText(String text)
    {
        this.text = text;
    }

    public int getPID ()
    {
        return PID;
    }

    public void setPID (int PID)
    {
        this.PID = PID;
    }

    public String getPName ()
    {
        return PName;
    }

    public void setPName (String PName)
    {
        this.PName = PName;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [text = "+ text +", PID = "+PID+", PName = "+PName+"]";
    }
}