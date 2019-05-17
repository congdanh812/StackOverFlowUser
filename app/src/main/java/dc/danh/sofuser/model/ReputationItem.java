package dc.danh.sofuser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReputationItem {
    @SerializedName("reputation_history_type")
    @Expose
    public String reputation_history_type;

    @SerializedName("reputation_change")
    @Expose
    public Integer reputation_change;

    @SerializedName("post_id")
    @Expose
    public Long post_id;

    @SerializedName("creation_date")
    @Expose
    public Long creation_date;

    @SerializedName("user_id")
    @Expose
    public Integer user_id;

    public String getReputation_history_type() {
        return reputation_history_type;
    }

    public void setReputation_history_type(String reputation_history_type) {
        this.reputation_history_type = reputation_history_type;
    }

    public Integer getReputation_change() {
        return reputation_change;
    }

    public void setReputation_change(Integer reputation_change) {
        this.reputation_change = reputation_change;
    }

    public Long getPost_id() {
        return post_id;
    }

    public void setPost_id(Long post_id) {
        this.post_id = post_id;
    }

    public Long getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Long creation_date) {
        this.creation_date = creation_date;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}

