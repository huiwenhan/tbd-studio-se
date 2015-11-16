// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.hadoop.distribution.cdh4mr2.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.core.hadoop.version.EHadoopDistributions;
import org.talend.core.hadoop.version.EHadoopVersion4Drivers;
import org.talend.hadoop.distribution.EHadoopVersion;
import org.talend.hadoop.distribution.cdh4mr2.CDH4MR2Distribution;
import org.talend.hadoop.distribution.component.HBaseComponent;
import org.talend.hadoop.distribution.component.HCatalogComponent;
import org.talend.hadoop.distribution.component.HDFSComponent;
import org.talend.hadoop.distribution.component.HadoopComponent;
import org.talend.hadoop.distribution.component.HiveComponent;
import org.talend.hadoop.distribution.component.ImpalaComponent;
import org.talend.hadoop.distribution.component.MRComponent;
import org.talend.hadoop.distribution.component.PigComponent;
import org.talend.hadoop.distribution.component.SparkBatchComponent;
import org.talend.hadoop.distribution.component.SparkStreamingComponent;
import org.talend.hadoop.distribution.component.SqoopComponent;

/**
 * Test class for the {@link CDH510MR1Distribution} distribution.
 *
 */
public class CDH4MR2DistributionTest {

    private final static String DEFAULT_YARN_APPLICATION_CLASSPATH = "$HADOOP_CONF_DIR,$HADOOP_COMMON_HOME/*,$HADOOP_COMMON_HOME/lib/*,$HADOOP_HDFS_HOME/*,$HADOOP_HDFS_HOME/lib/*,$HADOOP_MAPRED_HOME/*,$HADOOP_MAPRED_HOME/lib/*,$YARN_HOME/*,$YARN_HOME/lib/*,$HADOOP_YARN_HOME/*,$HADOOP_YARN_HOME/lib/*,$HADOOP_COMMON_HOME/share/hadoop/common/*,$HADOOP_COMMON_HOME/share/hadoop/common/lib/*,$HADOOP_HDFS_HOME/share/hadoop/hdfs/*,$HADOOP_HDFS_HOME/share/hadoop/hdfs/lib/*,$HADOOP_YARN_HOME/share/hadoop/yarn/*,$HADOOP_YARN_HOME/share/hadoop/yarn/lib/*"; //$NON-NLS-1$

    @Test
    public void testCDH4MR2Distribution() throws Exception {
        HadoopComponent distribution = new CDH4MR2Distribution();
        assertNotNull(distribution.getDistributionName());
        assertNotNull(distribution.getVersionName(null));
        assertEquals(EHadoopDistributions.CLOUDERA.getName(), distribution.getDistribution());
        assertEquals(EHadoopVersion4Drivers.CLOUDERA_CDH4_YARN.getVersionValue(), distribution.getVersion());
        assertEquals(EHadoopVersion.HADOOP_2, distribution.getHadoopVersion());
        assertTrue(distribution.doSupportKerberos());
        assertTrue(distribution.doSupportUseDatanodeHostname());
        assertFalse(distribution.doSupportGroup());
        assertTrue(distribution.doSupportOldImportMode());
        assertTrue(((HDFSComponent) distribution).doSupportSequenceFileShortType());
        assertFalse(((MRComponent) distribution).isExecutedThroughWebHCat());
        assertFalse(((MRComponent) distribution).doSupportCrossPlatformSubmission());
        assertTrue(((MRComponent) distribution).doSupportImpersonation());
        assertEquals(DEFAULT_YARN_APPLICATION_CLASSPATH, ((MRComponent) distribution).getYarnApplicationClasspath());
        assertFalse(((HBaseComponent) distribution).doSupportNewHBaseAPI());
        assertFalse(((SqoopComponent) distribution).doJavaAPISupportStorePasswordInFile());
        assertTrue(((SqoopComponent) distribution).doJavaAPISqoopImportSupportDeleteTargetDir());
        assertTrue(((SqoopComponent) distribution).doJavaAPISqoopImportAllTablesSupportExcludeTable());
        assertFalse(((PigComponent) distribution).doSupportHCatalog());
        assertFalse(((PigComponent) distribution).pigVersionPriorTo_0_12());
        assertTrue(((PigComponent) distribution).doSupportHBase());
        assertFalse(((PigComponent) distribution).doSupportTezForPig());
        assertTrue(((HiveComponent) distribution).doSupportEmbeddedMode());
        assertTrue(((HiveComponent) distribution).doSupportStandaloneMode());
        assertTrue(((HiveComponent) distribution).doSupportHive1());
        assertTrue(((HiveComponent) distribution).doSupportHive2());
        assertFalse(((HiveComponent) distribution).doSupportTezForHive());
        assertTrue(((HiveComponent) distribution).doSupportHBaseForHive());
        assertTrue(((HiveComponent) distribution).doSupportSSL());
        assertFalse(((HiveComponent) distribution).doSupportORCFormat());
        assertTrue(((HiveComponent) distribution).doSupportAvroFormat());
        assertTrue(((HiveComponent) distribution).doSupportParquetFormat());
        assertFalse(((HiveComponent) distribution).doSupportStoreAsParquet());
        assertFalse(((HiveComponent) distribution).doSupportClouderaNavigator());
        assertFalse(distribution instanceof SparkBatchComponent);
        assertFalse(distribution instanceof SparkStreamingComponent);
        assertFalse(distribution instanceof HCatalogComponent);
        assertFalse(distribution instanceof ImpalaComponent);
    }
}