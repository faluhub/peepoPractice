package me.falu.peepopractice.core.category.properties;

import lombok.Getter;
import me.falu.peepopractice.core.category.PracticeCategory;

@Getter
public class BaseProperties {
    private PracticeCategory category;

    public BaseProperties setCategory(PracticeCategory category) {
        if (this.category == null) {
            this.category = category;
        }
        return this;
    }
}
