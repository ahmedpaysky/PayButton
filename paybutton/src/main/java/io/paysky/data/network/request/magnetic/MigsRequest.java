package io.paysky.data.network.request.magnetic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by PaySky-3 on 8/8/2017.
 */

public class MigsRequest implements Serializable {

    private int id;
    @Expose
    @SerializedName("posModel")
    private PointOfSale pointOfSale;

    public String getSuccessTransactionNumber() {
        return successTransactionNumber;
    }

    public void setSuccessTransactionNumber(String successTransactionNumber) {
        this.successTransactionNumber = successTransactionNumber;
    }



    private String successTransactionNumber;

    public PointOfSale getPointOfSale() {
        return this.pointOfSale;
    }

    public void setPointOfSale(PointOfSale pointOfSale) {
        this.pointOfSale = pointOfSale;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
