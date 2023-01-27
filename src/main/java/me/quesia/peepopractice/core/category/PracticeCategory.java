package me.quesia.peepopractice.core.category;

import java.util.ArrayList;
import java.util.List;

public class PracticeCategory {
    private final String id;
    private final List<CategorySettings> settings;

    public PracticeCategory(String id) {
        this.id = id;
        this.settings = new ArrayList<>();

        PracticeCategories.ALL.add(this);
    }

    public String getId() {
        return this.id;
    }

    public List<CategorySettings> getSettings() {
        return this.settings;
    }

    public PracticeCategory addSetting(CategorySettings setting) {
        this.settings.add(setting);
        return this;
    }
}
