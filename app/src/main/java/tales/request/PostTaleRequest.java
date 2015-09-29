package tales.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import tales.model.Tale;


/**
 * Created by Aleksandr Subbotin on 25.09.2015.
 */
public class PostTaleRequest extends SpringAndroidSpiceRequest<Tale> {

    private Tale tale;

    public PostTaleRequest(Tale tale) {
        super(Tale.class);
        this.tale = tale;
    }

    @Override
    public Tale loadDataFromNetwork() throws Exception {
        return getRestTemplate().postForObject("http://10.0.2.2:8080",
                tale, Tale.class);
    }
}
