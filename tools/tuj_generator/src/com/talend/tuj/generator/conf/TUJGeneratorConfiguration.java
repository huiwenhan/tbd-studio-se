package com.talend.tuj.generator.conf;

import com.talend.tuj.generator.exception.UnknownDistributionException;
import com.talend.tuj.generator.utils.DistributionType;

import java.util.HashMap;
import java.util.Map;

public class TUJGeneratorConfiguration extends HashMap<String ,String> {
    public TUJGeneratorConfiguration(int initialCapacity, float loadFactor) {
        throw new NoSuchMethodError();
    }

    public TUJGeneratorConfiguration(int initialCapacity) {
        throw new NoSuchMethodError();
    }

    public TUJGeneratorConfiguration() {}

    public TUJGeneratorConfiguration(Map<? extends String, ? extends String> m) {
        throw new NoSuchMethodError();
    }

    public DistributionType getDistributionName() throws UnknownDistributionException {
        try{
            return DistributionType.valueOf(get("distributionName"));
        }catch (IllegalArgumentException e){
            throw new UnknownDistributionException(get("distributionName") + " is not a known distribution");
        }
    }
}
