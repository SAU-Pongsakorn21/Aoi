package sau.comsci.com.aoi.utils;

import java.util.List;

/**
 * Created by KorPai on 2/5/2560.
 */

public interface FavoriteCallback {
    void onSuccessResponse(List<String> name, List<String> detail, List<String> image,List<String> vdo,List<String> user,List<String> type,List<String> id);
}
