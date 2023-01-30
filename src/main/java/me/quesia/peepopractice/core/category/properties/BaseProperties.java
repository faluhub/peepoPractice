package me.quesia.peepopractice.core.category.properties;

import me.quesia.peepopractice.core.category.PracticeCategory;

public class BaseProperties {
    private PracticeCategory category;

    public BaseProperties setCategory(PracticeCategory category) {
        if (this.category == null) {
            this.category = category;
        }
        return this;
    }

    public PracticeCategory getCategory() {
        return this.category;
    }
}
