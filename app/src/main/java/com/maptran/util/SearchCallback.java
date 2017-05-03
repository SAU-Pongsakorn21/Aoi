package com.maptran.util;

import java.util.List;

/**
 * Created by KorPai on 19/4/2560.
 */

public interface SearchCallback {

    void onSuccessResponse(List<String> name, List<String> detail, List<String> image,List<String> user,List<String> type,List<String> id);
}
