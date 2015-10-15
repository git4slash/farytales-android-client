package tales.request;

import android.util.Log;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import java.net.URI;

/**
 * Created by Aleksandr Subbotin on 15.10.2015.
 */
public class DeleteTaleRequest extends SpringAndroidSpiceRequest<Void> {
    private final String request;

    public DeleteTaleRequest(String request) {
        super(Void.class);
        this.request = request;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        Log.d(this.getClass().getCanonicalName(), request);
        getRestTemplate().delete(URI.create(request));
        return null;
    }
}
