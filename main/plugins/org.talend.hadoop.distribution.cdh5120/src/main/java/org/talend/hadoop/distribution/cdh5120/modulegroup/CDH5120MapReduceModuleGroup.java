// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.hadoop.distribution.cdh5120.modulegroup;

import java.util.HashSet;
import java.util.Set;

import org.talend.hadoop.distribution.DistributionModuleGroup;
import org.talend.hadoop.distribution.cdh5120.CDH5120Constant;
import org.talend.hadoop.distribution.condition.BasicExpression;
import org.talend.hadoop.distribution.condition.ComponentCondition;
import org.talend.hadoop.distribution.condition.EqualityOperator;
import org.talend.hadoop.distribution.condition.SimpleComponentCondition;
import org.talend.hadoop.distribution.constants.SparkBatchConstant;

public class CDH5120MapReduceModuleGroup {

    private final static ComponentCondition conditionNavigator = new SimpleComponentCondition(
            new BasicExpression(SparkBatchConstant.USE_CLOUDERA_NAVIGATOR, EqualityOperator.EQ, "true"));

    public static Set<DistributionModuleGroup> getModuleGroups() {
        Set<DistributionModuleGroup> hs = new HashSet<>();
        hs.add(new DistributionModuleGroup(CDH5120Constant.HDFS_MODULE_GROUP.getModuleName()));
        hs.add(new DistributionModuleGroup(CDH5120Constant.MAPREDUCE_MODULE_GROUP.getModuleName()));
        hs.add(new DistributionModuleGroup(CDH5120Constant.MAPREDUCE_PARQUET_MODULE_GROUP.getModuleName()));
        hs.add(new DistributionModuleGroup(CDH5120Constant.MAPREDUCE_PARQUET_MRREQUIRED_MODULE_GROUP.getModuleName(), true, null));
        hs.add(new DistributionModuleGroup(CDH5120Constant.MAPREDUCE_AVRO_MRREQUIRED_MODULE_GROUP.getModuleName(), true, null));
        hs.add(new DistributionModuleGroup(CDH5120Constant.TALEND_CLOUDERA_CDH_5_12_NAVIGATOR.getModuleName(), false, conditionNavigator));
        return hs;
    }

}
