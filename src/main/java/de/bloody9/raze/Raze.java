package de.bloody9.raze;

import de.bloody9.core.Bot;
import de.bloody9.core.models.objects.BotInitObject;
import de.bloody9.feature.application.ApplicationFeature;
import de.bloody9.feature.formula.listener.FormulaFeature;
import de.bloody9.feature.rolesection.RoleSectionFeature;

public class Raze extends Bot {

    public static Raze INSTANCE;

    public Raze(String[] args) {
        this(enterArgs(args));
    }

    public Raze(BotInitObject initObject) {
        super(initObject);
    }

    @Override
    public void preInit(BotInitObject initObject) {
        super.preInit(initObject);
        INSTANCE = this;
    }

    @Override
    public void addFeatures() {
        super.addFeatures();
        features.add(new RoleSectionFeature());
        features.add(new FormulaFeature());
        features.add(new ApplicationFeature());
    }
}