package com.netcracker.odstc.logviewer.containers;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HierarchyContainer {//Можно сделать интерфейсы моделей, сделать тут их имплементацию и можно будет кастовать это к любой модели
    private EAVObject original;
    private HierarchyContainer parent;
    private List<HierarchyContainer> children;

    public HierarchyContainer(EAVObject original) {
        this();
        this.original = original;
    }

    public HierarchyContainer() {
        children = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HierarchyContainer that = (HierarchyContainer) o;
        return Objects.equals(original.getObjectId(), that.original.getObjectId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(original.getObjectId());
    }

    public void addChildren(HierarchyContainer eavObject) {
        children.add(eavObject);
    }

    public EAVObject getOriginal() {
        return original;
    }

    public void setOriginal(EAVObject original) {
        this.original = original;
    }

    public HierarchyContainer getParent() {
        return parent;
    }

    public void setParent(HierarchyContainer parent) {
        this.parent = parent;
    }

    public List<HierarchyContainer> getChildren() {
        return children;
    }

    public void setChildren(List<HierarchyContainer> children) {
        this.children = children;
    }
}
