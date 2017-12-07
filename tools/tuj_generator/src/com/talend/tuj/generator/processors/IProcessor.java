package com.talend.tuj.generator.processors;

import com.talend.tuj.generator.components.IComponent;

public interface IProcessor {
    boolean shouldBeProcessed(IComponent component);
    void process(IComponent component);
}
