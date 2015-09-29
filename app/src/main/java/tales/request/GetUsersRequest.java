package tales.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import tales.model.UserList;


/**
 * Created by Aleksandr Subbotin on 23.09.2015.
 */
public class GetUsersRequest extends SpringAndroidSpiceRequest<UserList> {

    private String cacheStr = "usersReq";

    public GetUsersRequest(String user) {
        super(UserList.class);
        this.cacheStr = user;
    }

    public GetUsersRequest() {
        super(UserList.class);
    }

    @Override
    public UserList loadDataFromNetwork() throws Exception {

        String url = "http://10.0.2.2:8080/users";
        System.out.println("UURRLL: "+ url);

        return getRestTemplate().getForObject(url, UserList.class);
    }

    /**
     * This method generates a unique cache key for this request. In this case
     * our cache key depends just on the keyword.
     * @return
     */
    public String createCacheKey() {
        return "Users." + cacheStr;
    }
}
