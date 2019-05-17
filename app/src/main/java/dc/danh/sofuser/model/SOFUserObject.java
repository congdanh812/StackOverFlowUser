package dc.danh.sofuser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SOFUserObject {
    @SerializedName("items")
    @Expose
    public List<SOFUserItem> items;

    @SerializedName("has_more")
    @Expose
    public Boolean has_more;

    @SerializedName("quota_max")
    @Expose
    public Integer quota_max;

    @SerializedName("quota_remaining")
    @Expose
    public Integer quota_remaining;

    public List<SOFUserItem> getItems() {
        return items;
    }

    public void setItems(List<SOFUserItem> items) {
        this.items = items;
    }

    public Boolean getHas_more() {
        return has_more;
    }

    public void setHas_more(Boolean has_more) {
        this.has_more = has_more;
    }

    public Integer getQuota_max() {
        return quota_max;
    }

    public void setQuota_max(Integer quota_max) {
        this.quota_max = quota_max;
    }

    public Integer getQuota_remaining() {
        return quota_remaining;
    }

    public void setQuota_remaining(Integer quota_remaining) {
        this.quota_remaining = quota_remaining;
    }
}

