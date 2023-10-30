package me.falu.peepopractice.core.category.properties;

import me.falu.peepopractice.core.category.PracticeCategory;

public class BaseProperties {
    private PracticeCategory category;

    public PracticeCategory getCategory() {
        return this.category;
    }

    public BaseProperties setCategory(PracticeCategory category) {
        if (this.category == null) {
            this.category = category;
        }
        return this;
    }
}
