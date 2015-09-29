package tales.request;

import android.util.Log;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import tales.model.Tale;


/**
 * Created by Aleksandr Subbotin on 25.09.2015.
 */
public class PostTaleRequest extends SpringAndroidSpiceRequest<Tale> {

    private Tale tale;
    private final String request;
    
    public PostTaleRequest(String request, Tale tale) {
        super(Tale.class);
        this.tale = tale;
        this.request = request;
    }

    @Override
    public Tale loadDataFromNetwork() throws Exception {
        Log.d(this.getClass().getCanonicalName(), request);
        return getRestTemplate().postForObject(request,
                tale, Tale.class);
    }
}
