/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.data;

import java.util.function.Predicate;

import com.google.common.base.Predicates;

public class ModelProperty<T> extends io.github.fabricators_of_create.porting_lib.model.ModelProperty<T> implements Predicate<T> {
    
    public ModelProperty() {
        this(Predicates.alwaysTrue());
    }
    
    public ModelProperty(Predicate<T> pred) {
        super(pred);
    }

    @Override
    public boolean test(T t) {
        return super.test(t);
    }
}
