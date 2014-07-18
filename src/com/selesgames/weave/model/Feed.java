package com.selesgames.weave.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name = "Feed", strict = false)
public class Feed {

    @Attribute(name = "Name")
    private String name;

    @Attribute(name = "IconUrl", required = false)
    private String iconUrl;

    @Text(required = false)
    private String contentUrl;

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getContentUrl() {
        return contentUrl;
    }

}
