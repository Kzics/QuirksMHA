package com.kzics.quirksmha.quirks;

import com.kzics.quirksmha.abilities.ForcePushAbility;
import com.kzics.quirksmha.abilities.Quirk;

public class ForcePushQuirk extends Quirk {
    public ForcePushQuirk() {
        super("Force Push");
    }

    @Override
    protected void initializeAttributes() {

    }

    @Override
    protected void initializeAbilities() {
        abilities.add(new ForcePushAbility());
    }

    @Override
    protected void adjustAttributesByLevel() {

    }
}
