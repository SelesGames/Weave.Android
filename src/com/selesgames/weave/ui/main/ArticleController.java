package com.selesgames.weave.ui.main;

import com.selesgames.weave.model.Article;
import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.News;

public interface ArticleController {

    void onArticleLoaded(Feed feed, News news, Article article);

}
