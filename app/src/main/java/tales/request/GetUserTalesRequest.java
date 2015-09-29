package tales.request;

import android.util.Log;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import tales.model.TaleList;

/**
 * Created by Aleksandr Subbotin on 28.09.2015.
 */
public class GetUserTalesRequest extends SpringAndroidSpiceRequest<TaleList> {

    private String request;

    public GetUserTalesRequest(String request) {
        super(TaleList.class);
        this.request = request;
    }

    @Override
    public TaleList loadDataFromNetwork() throws Exception {
        Log.d(this.getClass().getCanonicalName(), request);
        return getRestTemplate().getForObject(request, TaleList.class);
    }

    /**
     * This method generates a unique cache key for this request. In this case
     * our cache key depends just on the keyword.
     * @return
     */
    public String createCacheKey() {
        return "Tales from: " + request;
    }
}