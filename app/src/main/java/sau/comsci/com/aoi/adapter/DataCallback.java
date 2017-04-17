package sau.comsci.com.aoi.adapter;

import java.util.List;

/**
 * Created by KorPai on 15/4/2560.
 */

public interface DataCallback {
    void onSuccessResponse(List<String> place, List<String> detail, List<String> image);
}
