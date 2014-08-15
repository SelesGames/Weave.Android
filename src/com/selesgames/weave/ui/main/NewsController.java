package com.selesgames.weave.ui.main;

import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.News;

public interface NewsController {
    
    void onNewsSelected(Feed feed, News news);

}
