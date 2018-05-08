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
package org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkbatch;

import java.util.HashSet;
import java.util.Set;

import org.talend.hadoop.distribution.DistributionModuleGroup;
import org.talend.hadoop.distribution.condition.common.SparkBatchLinkedNodeCondition;
import org.talend.hadoop.distribution.constants.SparkBatchConstant;
import org.talend.hadoop.distribution.dynamic.adapter.DynamicPluginAdapter;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicModuleGroupConstant;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.AbstractNodeModuleGroup;


public class DynamicSparkBatchAzureNodeModuleGroup extends AbstractNodeModuleGroup {
    
    public DynamicSparkBatchAzureNodeModuleGroup(DynamicPluginAdapter pluginAdapter) {
        super(pluginAdapter);
    }

    public Set<DistributionModuleGroup> getModuleGroups(String distribution, String version) throws Exception {
        Set<DistributionModuleGroup> hs = new HashSet<>();

        DynamicPluginAdapter pluginAdapter = getPluginAdapter();

        String sparkAzureMrRequiredRuntimeId = pluginAdapter
                .getRuntimeModuleGroupIdByTemplateId(
                        DynamicModuleGroupConstant.SPARK_AZURE_MRREQUIRED_MODULE_GROUP.getModuleName());
        checkRuntimeId(sparkAzureMrRequiredRuntimeId);

        DistributionModuleGroup dmg = new DistributionModuleGroup(sparkAzureMrRequiredRuntimeId, true,
                new SparkBatchLinkedNodeCondition(distribution, version,
                        SparkBatchConstant.SPARK_BATCH_AZURE_SPARKCONFIGURATION_LINKEDPARAMETER).getCondition());
        hs.add(dmg);
        return hs;
    }
}
