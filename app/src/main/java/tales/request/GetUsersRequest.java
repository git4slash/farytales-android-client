package tales.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import tales.model.UserList;


/**
 * Created by Aleksandr Subbotin on 23.09.2015.
 */
public class GetUsersRequest extends SpringAndroidSpiceRequest<UserList> {

    private final String request;

    public GetUsersRequest(String request) {
        super(UserList.class);
        this.request = request;
    }

    @Override
    public UserList loadDataFromNetwork() throws Exception {


        return getRestTemplate().getForObject(request, UserList.class);
    }

    /**
     * This method generates a unique cache key for this request. In this case
     * our cache key depends just on the keyword.
     * @return
     */
    public String createCacheKey() {
        return "Get accounts from server: " + request;
    }
}
