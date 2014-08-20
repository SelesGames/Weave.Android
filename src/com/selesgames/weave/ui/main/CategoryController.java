package com.selesgames.weave.ui.main;

import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.News;

public interface CategoryController {

    void onNewsFocussed(Feed feed, News news);
    
    void onNewsUnfocussed();

}
