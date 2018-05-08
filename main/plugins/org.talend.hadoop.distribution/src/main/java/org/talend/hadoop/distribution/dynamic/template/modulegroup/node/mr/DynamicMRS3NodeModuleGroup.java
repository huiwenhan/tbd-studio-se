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
package org.talend.hadoop.distribution.dynamic.template.modulegroup.node.mr;

import java.util.HashSet;
import java.util.Set;

import org.talend.hadoop.distribution.DistributionModuleGroup;
import org.talend.hadoop.distribution.condition.common.MRLinkedNodeCondition;
import org.talend.hadoop.distribution.constants.MRConstant;
import org.talend.hadoop.distribution.dynamic.adapter.DynamicPluginAdapter;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicModuleGroupConstant;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.AbstractNodeModuleGroup;

public class DynamicMRS3NodeModuleGroup extends AbstractNodeModuleGroup {

    public DynamicMRS3NodeModuleGroup(DynamicPluginAdapter pluginAdapter) {
        super(pluginAdapter);
    }

    public Set<DistributionModuleGroup> getModuleGroups(String distribution, String version) throws Exception {
        Set<DistributionModuleGroup> hs = new HashSet<>();

        DynamicPluginAdapter pluginAdapter = getPluginAdapter();

        String sparkS3MrRequiredRuntimeId = pluginAdapter
                .getRuntimeModuleGroupIdByTemplateId(DynamicModuleGroupConstant.SPARK_S3_MRREQUIRED_MODULE_GROUP.getModuleName());
        checkRuntimeId(sparkS3MrRequiredRuntimeId);

        DistributionModuleGroup dmg = new DistributionModuleGroup(sparkS3MrRequiredRuntimeId, true,
                new MRLinkedNodeCondition(distribution, version, MRConstant.MR_MRCONFIGURATION_LINKEDPARAMETER).getCondition());
        hs.add(dmg);
        return hs;
    }

}
