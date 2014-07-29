package com.selesgames.weave.model;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "Feeds")
public class CategoryList {

    @ElementList(name = "Category", inline = true)
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }

}
