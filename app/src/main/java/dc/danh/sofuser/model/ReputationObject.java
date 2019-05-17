package dc.danh.sofuser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReputationObject {
    @SerializedName("items")
    @Expose
    public List<ReputationItem> items;

    public List<ReputationItem> getItems() {
        return items;
    }

    public void setItems(List<ReputationItem> items) {
        this.items = items;
    }
}
