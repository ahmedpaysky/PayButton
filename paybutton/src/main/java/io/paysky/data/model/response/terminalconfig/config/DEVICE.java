package io.paysky.data.model.response.terminalconfig.config;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by PaySky-3 on 8/9/2017.
 */
@Root(name = "DEVICE")
public class DEVICE
{
    @Element(name = "Parameters",required = false)
    private Parameters Parameters;

    @ElementList(name = "TABLE",inline=true,required = false)
    private List<TABLE> TABLE;

    public Parameters getParameters() {
        return Parameters;
    }

    public void setParameters(Parameters parameters) {
        Parameters = parameters;
    }

    public List<TABLE> getTABLE() {
        return TABLE;
    }

    public void setTABLE(List<TABLE> TABLE) {
        this.TABLE = TABLE;
    }


    public String getCurrencyCode(){
        String code = "";
        for(SD sd :Parameters.getSD()){
            if(sd.getPName() != null)
            if(sd.getPName().equals("Currency")){
                code =   sd.getText();
            }
        }

        return code;
    }

    public String getHostid(){
        String code = "";

        for(TABLE td :TABLE){
            if(td.getNAME() != null)
            if(td.getNAME().equals("HostList")){
                for(ROW row :td.getROW()) {
                    for(COL col :row.getCOL()) {
                        if(col.getText() !=null && !col.getText().equals("")){
                            code = col.getText();
                        }
                    }
                }
            }
        }
        return code;
    }
}
