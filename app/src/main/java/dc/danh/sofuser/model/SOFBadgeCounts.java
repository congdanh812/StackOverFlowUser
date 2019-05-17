package dc.danh.sofuser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SOFBadgeCounts {
    @SerializedName("bronze")
    @Expose
    public Integer bronze;

    @SerializedName("silver")
    @Expose
    public Integer silver;

    @SerializedName("gold")
    @Expose
    public Integer gold;

    public Integer getBronze() {
        return bronze;
    }

    public void setBronze(Integer bronze) {
        this.bronze = bronze;
    }

    public Integer getSilver() {
        return silver;
    }

    public void setSilver(Integer silver) {
        this.silver = silver;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }
}
