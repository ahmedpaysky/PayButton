package io.paysky.data.model.response.terminalconfig.config;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by PaySky-3 on 8/10/2017.
 */

@Root
public class Parameters
{
    @ElementList(inline=true,name = "SD",required = false)
    private List<SD> SD;

    public List<SD> getSD() {
        return SD;
    }

    public void setSD(List<SD> SD) {
        this.SD = SD;
    }
}

