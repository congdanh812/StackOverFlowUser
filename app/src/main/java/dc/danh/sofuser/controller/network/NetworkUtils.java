package dc.danh.sofuser.controller.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import dc.danh.sofuser.R;

public class NetworkUtils {
    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;

        NetworkInfo info = cm.getActiveNetworkInfo();
        boolean result = info != null && info.isConnectedOrConnecting();
        if (!result) {
            Toast.makeText(mContext, mContext.getString(R.string.msg_no_network), Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}
