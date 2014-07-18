package com.selesgames.weave.model;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "Category")
public class Category {

    @Attribute(name = "Type")
    private String type;

    @ElementList(name = "Feed", inline = true, required = false)
    private List<Feed> feeds;

    public String getType() {
        return type;
    }

    public List<Feed> getFeeds() {
        return feeds;
    }

}
