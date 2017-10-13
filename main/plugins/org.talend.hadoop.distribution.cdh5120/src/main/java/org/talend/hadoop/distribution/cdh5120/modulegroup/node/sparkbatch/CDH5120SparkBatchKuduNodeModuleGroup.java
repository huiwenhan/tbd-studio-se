// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.hadoop.distribution.cdh5120.modulegroup.node.sparkbatch;

import java.util.HashSet;
import java.util.Set;

import org.talend.hadoop.distribution.DistributionModuleGroup;
import org.talend.hadoop.distribution.cdh5120.CDH5120Constant;
import org.talend.hadoop.distribution.condition.BasicExpression;
import org.talend.hadoop.distribution.condition.BooleanOperator;
import org.talend.hadoop.distribution.condition.ComponentCondition;
import org.talend.hadoop.distribution.condition.EqualityOperator;
import org.talend.hadoop.distribution.condition.LinkedNodeExpression;
import org.talend.hadoop.distribution.condition.MultiComponentCondition;
import org.talend.hadoop.distribution.condition.RawExpression;
import org.talend.hadoop.distribution.condition.SimpleComponentCondition;
import org.talend.hadoop.distribution.constants.SparkBatchConstant;
import org.talend.hadoop.distribution.utils.ModuleGroupsUtils;

public class CDH5120SparkBatchKuduNodeModuleGroup {

    private static MultiComponentCondition getComponentCondition(String supportedSparkVersion) {
        ComponentCondition cc1 = new SimpleComponentCondition(new LinkedNodeExpression( //
                SparkBatchConstant.SPARK_BATCH_SPARKCONFIGURATION_LINKEDPARAMETER, //
                "SUPPORTED_SPARK_VERSION", //$NON-NLS-1$
                EqualityOperator.EQ, //
                supportedSparkVersion));
        ComponentCondition cc2 = new SimpleComponentCondition(new BasicExpression(
                "KUDU_VERSION", EqualityOperator.EQ, "KUDU_1.4.0")); //$NON-NLS-1$//$NON-NLS-2$
        return new MultiComponentCondition(cc1, BooleanOperator.AND, cc2);
    }

    public static Set<DistributionModuleGroup> getModuleGroups(String distribution, String version, String condition) {

        Set<DistributionModuleGroup> dmg = new HashSet<>();

        ComponentCondition cc22 = getComponentCondition("SPARK_2_2_0"); //$NON-NLS-1$
        if (condition != null) {
            ComponentCondition c = new SimpleComponentCondition(new RawExpression(condition));
            cc22 = new MultiComponentCondition(cc22, BooleanOperator.AND, c);
        }

        dmg.addAll(ModuleGroupsUtils.getModuleGroups(distribution, version, cc22.getConditionString(),
                CDH5120Constant.SPARK2_KUDU_MRREQUIRED_MODULE_GROUP.getModuleName(), true));
        return dmg;
    }
}