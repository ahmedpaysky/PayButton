package io.paysky.data.model.response.terminalconfig.config;

/**
 * Created by PaySky-3 on 8/9/2017.
 */

public class ConfigModel {
    String Nodeid;
    String DNSNODE;
    String ipnode;
    String HTTPType;
    String port;
    String ConnectionTimeOut;
    String ReciveTimeOut;

    public String getNodeid() {
        return Nodeid;
    }

    public void setNodeid(String nodeid) {
        Nodeid = nodeid;
    }

    public String getDNSNODE() {
        return DNSNODE;
    }

    public void setDNSNODE(String DNSNODE) {
        this.DNSNODE = DNSNODE;
    }

    public String getIpnode() {
        return ipnode;
    }

    public void setIpnode(String ipnode) {
        this.ipnode = ipnode;
    }

    public String getHTTPType() {
        return HTTPType;
    }

    public void setHTTPType(String HTTPType) {
        this.HTTPType = HTTPType;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getConnectionTimeOut() {
        return ConnectionTimeOut;
    }

    public void setConnectionTimeOut(String connectionTimeOut) {
        ConnectionTimeOut = connectionTimeOut;
    }

    public String getReciveTimeOut() {
        return ReciveTimeOut;
    }

    public void setReciveTimeOut(String reciveTimeOut) {
        ReciveTimeOut = reciveTimeOut;
    }

    public ConfigModel setFristTimeConfig(){
        ConfigModel configModel = new ConfigModel();
        configModel.setConnectionTimeOut("12");
       configModel.setDNSNODE("grey.paysky.io");
  //   configModel.setIpnode("197.50.37.116");

        configModel.setHTTPType("https");
        configModel.setReciveTimeOut("12");
//        configModel.setPort("4006");
        configModel.setPort("443");
        configModel.setNodeid("5166");
        return configModel;
    }

    public String getFullPath(){
        String x;
        if(getIpnode() != null){
            x = getHTTPType()+"://"+getIpnode()+":"+getPort()+"/Cube/mPOSHosting.svc/api/";

        }else {
            x =getHTTPType()+"://"+getDNSNODE()+":"+getPort()+"/Cube/mPOSHosting.svc/api/";

        }

        return  x;
    }

    public String getRealyIp(){
        String x = "";
        if(getIpnode() != null){
            x = getHTTPType()+"://"+getIpnode()+":"+getPort();

        }else {
            x =getHTTPType()+"://"+getDNSNODE()+":"+getPort();
        }

        return  x;
    }
}
