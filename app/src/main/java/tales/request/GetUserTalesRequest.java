package tales.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import tales.model.TaleList;


/**
 * Created by Aleksandr Subbotin on 28.09.2015.
 */
public class GetUserTalesRequest extends SpringAndroidSpiceRequest<TaleList> {

    private String user;

    public GetUserTalesRequest(String user) {
        super(TaleList.class);
        this.user = user;
    }

    @Override
    public TaleList loadDataFromNetwork() throws Exception {

        String url = "http://10.0.2.2:8080/dsyer/tales";
        System.out.println("UURRLL: "+ url);

        return getRestTemplate().getForObject(url, TaleList.class);
    }

    /**
     * This method generates a unique cache key for this request. In this case
     * our cache key depends just on the keyword.
     * @return
     */
    public String createCacheKey() {
        return "Users." + user;
    }
}