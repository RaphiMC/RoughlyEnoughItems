/*
 * Copyright (c) 2018, 2019, 2020 shedaniel
 * Licensed under the MIT License (the "License").
 */

package me.shedaniel.rei.api;

import me.shedaniel.math.api.Rectangle;

/**
 * The supplier for the + button area.
 */
public interface ButtonAreaSupplier {
    
    /**
     * Declares the button bounds
     *
     * @param bounds the bounds of the recipe display
     * @return the bounds of the button
     */
    Rectangle get(Rectangle bounds);
    
    /**
     * Declares the button text
     *
     * @return the text of the button
     */
    default String getButtonText() {
        return "+";
    }
    
}
