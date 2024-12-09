package com.kzics.quirksmha.quirks;

import com.kzics.quirksmha.abilities.BulletLaserAbility;
import com.kzics.quirksmha.abilities.Quirk;

public class BulletLaserQuirk extends Quirk {
    public BulletLaserQuirk() {
        super("Bullet Laser");
    }

    @Override
    protected void initializeAttributes() {
        
    }

    @Override
    protected void initializeAbilities() {
        abilities.add(new BulletLaserAbility());
    }

    @Override
    protected void adjustAttributesByLevel() {

    }
}
