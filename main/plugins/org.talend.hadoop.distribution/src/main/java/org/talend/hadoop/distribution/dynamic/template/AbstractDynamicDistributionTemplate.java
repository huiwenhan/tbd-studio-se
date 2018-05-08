package org.talend.hadoop.distribution.dynamic.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.core.runtime.dynamic.IDynamicPluginConfiguration;
import org.talend.hadoop.distribution.AbstractDistribution;
import org.talend.hadoop.distribution.ComponentType;
import org.talend.hadoop.distribution.DistributionModuleGroup;
import org.talend.hadoop.distribution.NodeComponentTypeBean;
import org.talend.hadoop.distribution.component.HBaseComponent;
import org.talend.hadoop.distribution.component.HCatalogComponent;
import org.talend.hadoop.distribution.component.HDFSComponent;
import org.talend.hadoop.distribution.component.HadoopComponent;
import org.talend.hadoop.distribution.component.HiveComponent;
import org.talend.hadoop.distribution.component.HiveOnSparkComponent;
import org.talend.hadoop.distribution.component.ImpalaComponent;
import org.talend.hadoop.distribution.component.MRComponent;
import org.talend.hadoop.distribution.component.MapRDBComponent;
import org.talend.hadoop.distribution.component.PigComponent;
import org.talend.hadoop.distribution.component.SparkBatchComponent;
import org.talend.hadoop.distribution.component.SparkStreamingComponent;
import org.talend.hadoop.distribution.component.SqoopComponent;
import org.talend.hadoop.distribution.condition.ComponentCondition;
import org.talend.hadoop.distribution.constants.HDFSConstant;
import org.talend.hadoop.distribution.constants.MRConstant;
import org.talend.hadoop.distribution.constants.PigOutputConstant;
import org.talend.hadoop.distribution.constants.SparkBatchConstant;
import org.talend.hadoop.distribution.constants.SparkStreamingConstant;
import org.talend.hadoop.distribution.dynamic.adapter.DynamicPluginAdapter;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicHBaseModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicHCatalogModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicHDFSModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicHiveModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicHiveOnSparkModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicImpalaModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicMapReduceModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicPigModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicPigOutputModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicSparkBatchModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicSparkStreamingModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicSqoopModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicWebHDFSModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.mr.DynamicMRS3NodeModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.pigoutput.DynamicPigOutputNodeModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.spark.DynamicSparkDynamoDBNodeModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkbatch.DynamicGraphFramesNodeModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkbatch.DynamicSparkBatchAzureNodeModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkbatch.DynamicSparkBatchKuduNodeModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkbatch.DynamicSparkBatchParquetNodeModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkbatch.DynamicSparkBatchS3NodeModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkstreaming.DynamicSparkStreamingFlumeNodeModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkstreaming.DynamicSparkStreamingKafkaAssemblyModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkstreaming.DynamicSparkStreamingKafkaAvroModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkstreaming.DynamicSparkStreamingKafkaClientModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkstreaming.DynamicSparkStreamingKinesisNodeModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkstreaming.DynamicSparkStreamingParquetNodeModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkstreaming.DynamicSparkStreamingS3NodeModuleGroup;

public abstract class AbstractDynamicDistributionTemplate extends AbstractDistribution implements IDynamicDistributionTemplate {

    private Map<ComponentType, Set<DistributionModuleGroup>> moduleGroups;

    private Map<NodeComponentTypeBean, Set<DistributionModuleGroup>> nodeModuleGroups;

    private Map<ComponentType, ComponentCondition> displayConditions;

    private DynamicPluginAdapter pluginAdapter;

    private String versionId;

    private String versionDisplay;

    public AbstractDynamicDistributionTemplate(DynamicPluginAdapter pluginAdapter) throws Exception {
        this.pluginAdapter = pluginAdapter;
        IDynamicPluginConfiguration configuration = pluginAdapter.getPluginConfiguration();
        versionId = configuration.getId();
        versionDisplay = configuration.getName();

        // Used to add a module group import for the components that have a HADOOP_DISTRIBUTION parameter, aka. the
        // components that have the distribution list.
        moduleGroups = buildModuleGroupsMap(pluginAdapter);

        // Used to add a module group import for a specific node. The given node must have a HADOOP_LIBRARIES parameter.
        nodeModuleGroups = buildNodeModuleGroupsMap(pluginAdapter);

        displayConditions = new HashMap<>();
    }

    protected Map<ComponentType, Set<DistributionModuleGroup>> buildModuleGroupsMap(DynamicPluginAdapter pluginAdapter)
            throws Exception {
        Map<ComponentType, Set<DistributionModuleGroup>> moduleGroupsMap = new HashMap<>();

        if (this instanceof HDFSComponent) {
            moduleGroupsMap.put(ComponentType.HDFS, buildModuleGroups4HDFS(pluginAdapter));
        }
        if (this instanceof HBaseComponent) {
            moduleGroupsMap.put(ComponentType.HBASE, buildModuleGroups4HBase(pluginAdapter));
        }
        if (this instanceof HCatalogComponent) {
            moduleGroupsMap.put(ComponentType.HCATALOG, buildModuleGroups4HCatalog(pluginAdapter));
        }
        if (this instanceof HiveComponent) {
            moduleGroupsMap.put(ComponentType.HIVE, buildModuleGroups4Hive(pluginAdapter));
        }
        if (this instanceof HiveOnSparkComponent) {
            moduleGroupsMap.put(ComponentType.HIVEONSPARK, buildModuleGroups4HiveOnSpark(pluginAdapter));
        }
        if (this instanceof ImpalaComponent) {
            moduleGroupsMap.put(ComponentType.IMPALA, buildModuleGroups4Impala(pluginAdapter));
        }
        if (this instanceof MapRDBComponent) {
            moduleGroupsMap.put(ComponentType.MAPRDB, buildModuleGroups4MapRDB(pluginAdapter));
        }
        if (this instanceof MRComponent) {
            moduleGroupsMap.put(ComponentType.MAPREDUCE, buildModuleGroups4MapReduce(pluginAdapter));
        }
        if (this instanceof PigComponent) {
            moduleGroupsMap.put(ComponentType.PIG, buildModuleGroups4Pig(pluginAdapter));
            moduleGroupsMap.put(ComponentType.PIGOUTPUT, buildModuleGroups4PigOutput(pluginAdapter));
        }
        if (this instanceof SqoopComponent) {
            moduleGroupsMap.put(ComponentType.SQOOP, buildModuleGroups4Sqoop(pluginAdapter));
        }
        if (this instanceof SparkBatchComponent) {
            moduleGroupsMap.put(ComponentType.SPARKBATCH, buildModuleGroups4Sparkbatch(pluginAdapter));
        }
        if (this instanceof SparkStreamingComponent) {
            moduleGroupsMap.put(ComponentType.SPARKSTREAMING, buildModuleGroups4SparkStreaming(pluginAdapter));
        }

        return moduleGroupsMap;
    }

    protected Map<NodeComponentTypeBean, Set<DistributionModuleGroup>> buildNodeModuleGroupsMap(
            DynamicPluginAdapter pluginAdapter) throws Exception {
        Map<NodeComponentTypeBean, Set<DistributionModuleGroup>> nodeModuleGroupsMap = new HashMap<>();
        IDynamicPluginConfiguration configuration = pluginAdapter.getPluginConfiguration();
        String version = configuration.getId();
        String distribution = configuration.getDistribution();

        buildNodeModuleGroups4HDFS(pluginAdapter, nodeModuleGroupsMap, version, distribution);

        buildNodeModuleGroups4MapReduce(pluginAdapter, nodeModuleGroupsMap, version, distribution);

        buildNodeModuleGroups4Pig(pluginAdapter, nodeModuleGroupsMap, version, distribution);

        buildNodeModuleGroups4SparkBatch(pluginAdapter, nodeModuleGroupsMap, version, distribution);

        buildNodeModuleGroups4SparkStreaming(pluginAdapter, nodeModuleGroupsMap, version, distribution);

        return nodeModuleGroupsMap;
    }

    @Override
    public List<String> getServices() {
        List<String> services = new ArrayList<>();
        if (this instanceof HadoopComponent) {
            services.add(HadoopComponent.class.getName());
        }
        if (this instanceof HDFSComponent) {
            services.add(HDFSComponent.class.getName());
        }
        if (this instanceof HBaseComponent) {
            services.add(HBaseComponent.class.getName());
        }
        if (this instanceof HCatalogComponent) {
            services.add(HCatalogComponent.class.getName());
        }
        if (this instanceof HiveComponent) {
            services.add(HiveComponent.class.getName());
        }
        if (this instanceof HiveOnSparkComponent) {
            services.add(HiveOnSparkComponent.class.getName());
        }
        if (this instanceof ImpalaComponent) {
            services.add(ImpalaComponent.class.getName());
        }
        if (this instanceof MapRDBComponent) {
            services.add(MapRDBComponent.class.getName());
        }
        if (this instanceof MRComponent) {
            services.add(MRComponent.class.getName());
        }
        if (this instanceof PigComponent) {
            services.add(PigComponent.class.getName());
        }
        if (this instanceof SqoopComponent) {
            services.add(SqoopComponent.class.getName());
        }
        if (this instanceof SparkBatchComponent) {
            services.add(SparkBatchComponent.class.getName());
        }
        if (this instanceof SparkStreamingComponent) {
            services.add(SparkStreamingComponent.class.getName());
        }
        return services;
    }

    protected Set<DistributionModuleGroup> buildModuleGroups4HDFS(DynamicPluginAdapter pluginAdapter) throws Exception {
        return new DynamicHDFSModuleGroup(pluginAdapter).getModuleGroups();
    }

    protected Set<DistributionModuleGroup> buildModuleGroups4HBase(DynamicPluginAdapter pluginAdapter) throws Exception {
        return new DynamicHBaseModuleGroup(pluginAdapter).getModuleGroups();
    }

    protected Set<DistributionModuleGroup> buildModuleGroups4HCatalog(DynamicPluginAdapter pluginAdapter) throws Exception {
        return new DynamicHCatalogModuleGroup(pluginAdapter).getModuleGroups();
    }

    protected Set<DistributionModuleGroup> buildModuleGroups4MapReduce(DynamicPluginAdapter pluginAdapter) throws Exception {
        return new DynamicMapReduceModuleGroup(pluginAdapter).getModuleGroups();
    }

    protected Set<DistributionModuleGroup> buildModuleGroups4Pig(DynamicPluginAdapter pluginAdapter) throws Exception {
        return new DynamicPigModuleGroup(pluginAdapter).getModuleGroups();
    }

    protected Set<DistributionModuleGroup> buildModuleGroups4PigOutput(DynamicPluginAdapter pluginAdapter) throws Exception {
        return new DynamicPigOutputModuleGroup(pluginAdapter).getModuleGroups();
    }

    protected Set<DistributionModuleGroup> buildModuleGroups4Sqoop(DynamicPluginAdapter pluginAdapter) throws Exception {
        return new DynamicSqoopModuleGroup(pluginAdapter).getModuleGroups();
    }

    protected Set<DistributionModuleGroup> buildModuleGroups4Hive(DynamicPluginAdapter pluginAdapter) throws Exception {
        return new DynamicHiveModuleGroup(pluginAdapter).getModuleGroups();
    }

    protected Set<DistributionModuleGroup> buildModuleGroups4Impala(DynamicPluginAdapter pluginAdapter) throws Exception {
        return new DynamicImpalaModuleGroup(pluginAdapter).getModuleGroups();
    }

    protected Set<DistributionModuleGroup> buildModuleGroups4Sparkbatch(DynamicPluginAdapter pluginAdapter) throws Exception {
        return new DynamicSparkBatchModuleGroup(pluginAdapter).getModuleGroups();
    }

    protected Set<DistributionModuleGroup> buildModuleGroups4SparkStreaming(DynamicPluginAdapter pluginAdapter) throws Exception {
        return new DynamicSparkStreamingModuleGroup(pluginAdapter).getModuleGroups();
    }

    protected Set<DistributionModuleGroup> buildModuleGroups4HiveOnSpark(DynamicPluginAdapter pluginAdapter) throws Exception {
        return new DynamicHiveOnSparkModuleGroup(pluginAdapter).getModuleGroups();
    }

    protected Set<DistributionModuleGroup> buildModuleGroups4MapRDB(DynamicPluginAdapter pluginAdapter) throws Exception {
        return Collections.EMPTY_SET;
    }

    protected void buildNodeModuleGroups4SparkStreaming(DynamicPluginAdapter pluginAdapter,
            Map<NodeComponentTypeBean, Set<DistributionModuleGroup>> nodeModuleGroupsMap, String version, String distribution)
            throws Exception {
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.PARQUET_INPUT_COMPONENT),
                new DynamicSparkStreamingParquetNodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version));
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.PARQUET_OUTPUT_COMPONENT),
                new DynamicSparkStreamingParquetNodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version));
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.PARQUET_STREAM_INPUT_COMPONENT),
                new DynamicSparkStreamingParquetNodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version));
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.S3_CONFIGURATION_COMPONENT),
                new DynamicSparkStreamingS3NodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version));
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkBatchConstant.AZURE_CONFIGURATION_COMPONENT),
                new DynamicSparkBatchAzureNodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version));

        // Kinesis
        Set<DistributionModuleGroup> kinesisNodeModuleGroups = new DynamicSparkStreamingKinesisNodeModuleGroup(pluginAdapter)
                .getModuleGroups(distribution, version);
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.KINESIS_INPUT_COMPONENT),
                kinesisNodeModuleGroups);
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.KINESIS_INPUT_AVRO_COMPONENT),
                kinesisNodeModuleGroups);
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.KINESIS_OUTPUT_COMPONENT),
                kinesisNodeModuleGroups);

        // Flume
        Set<DistributionModuleGroup> flumeNodeModuleGroups = new DynamicSparkStreamingFlumeNodeModuleGroup(pluginAdapter)
                .getModuleGroups(distribution, version);
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.FLUME_INPUT_COMPONENT),
                flumeNodeModuleGroups);
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.FLUME_OUTPUT_COMPONENT),
                flumeNodeModuleGroups);

        // Kafka
        Set<DistributionModuleGroup> kafkaAssemblyModuleGroups = new DynamicSparkStreamingKafkaAssemblyModuleGroup(pluginAdapter)
                .getModuleGroups(distribution, version);
        Set<DistributionModuleGroup> kafkaAvroModuleGroups = new DynamicSparkStreamingKafkaAvroModuleGroup(pluginAdapter)
                .getModuleGroups(distribution, version);
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.KAFKA_INPUT_COMPONENT),
                kafkaAssemblyModuleGroups);
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.KAFKA_AVRO_INPUT_COMPONENT),
                kafkaAvroModuleGroups);
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.KAFKA_OUTPUT_COMPONENT),
                new DynamicSparkStreamingKafkaClientModuleGroup(pluginAdapter).getModuleGroups(distribution, version));
        // DynamoDB ...
        Set<DistributionModuleGroup> dynamoDBNodeModuleGroups = new DynamicSparkDynamoDBNodeModuleGroup(pluginAdapter)
                .getModuleGroups(distribution, version, "USE_EXISTING_CONNECTION == 'false'");
        Set<DistributionModuleGroup> dynamoDBConfigurationModuleGroups = new DynamicSparkDynamoDBNodeModuleGroup(pluginAdapter)
                .getModuleGroups(distribution, version, null);
        // ... in Spark streaming
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.DYNAMODB_INPUT_COMPONENT),
                dynamoDBNodeModuleGroups);
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.DYNAMODB_OUTPUT_COMPONENT),
                dynamoDBNodeModuleGroups);
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKSTREAMING, SparkStreamingConstant.DYNAMODB_CONFIGURATION_COMPONENT),
                dynamoDBConfigurationModuleGroups);
    }

    protected void buildNodeModuleGroups4SparkBatch(DynamicPluginAdapter pluginAdapter,
            Map<NodeComponentTypeBean, Set<DistributionModuleGroup>> nodeModuleGroupsMap, String version, String distribution)
            throws Exception {
        nodeModuleGroupsMap.put(new NodeComponentTypeBean(ComponentType.SPARKBATCH, SparkBatchConstant.PARQUET_INPUT_COMPONENT),
                new DynamicSparkBatchParquetNodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version));
        nodeModuleGroupsMap.put(new NodeComponentTypeBean(ComponentType.SPARKBATCH, SparkBatchConstant.PARQUET_OUTPUT_COMPONENT),
                new DynamicSparkBatchParquetNodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version));
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKBATCH, SparkBatchConstant.S3_CONFIGURATION_COMPONENT),
                new DynamicSparkBatchS3NodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version));
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKBATCH, SparkBatchConstant.AZURE_CONFIGURATION_COMPONENT),
                new DynamicSparkBatchAzureNodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version));
        nodeModuleGroupsMap.put(new NodeComponentTypeBean(ComponentType.SPARKBATCH, SparkBatchConstant.MATCH_PREDICT_COMPONENT),
                new DynamicGraphFramesNodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version));
        // DynamoDB ...
        Set<DistributionModuleGroup> dynamoDBNodeModuleGroups = new DynamicSparkDynamoDBNodeModuleGroup(pluginAdapter)
                .getModuleGroups(distribution, version, "USE_EXISTING_CONNECTION == 'false'");
        Set<DistributionModuleGroup> dynamoDBConfigurationModuleGroups = new DynamicSparkDynamoDBNodeModuleGroup(pluginAdapter)
                .getModuleGroups(distribution, version, null);
        // ... in Spark batch
        nodeModuleGroupsMap.put(new NodeComponentTypeBean(ComponentType.SPARKBATCH, SparkBatchConstant.DYNAMODB_INPUT_COMPONENT),
                dynamoDBNodeModuleGroups);
        nodeModuleGroupsMap.put(new NodeComponentTypeBean(ComponentType.SPARKBATCH, SparkBatchConstant.DYNAMODB_OUTPUT_COMPONENT),
                dynamoDBNodeModuleGroups);
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKBATCH, SparkBatchConstant.DYNAMODB_CONFIGURATION_COMPONENT),
                dynamoDBConfigurationModuleGroups);
        // Kudu ...
        Set<DistributionModuleGroup> kuduNodeModuleGroups = new DynamicSparkBatchKuduNodeModuleGroup(pluginAdapter)
                .getModuleGroups(distribution, version, "USE_EXISTING_CONNECTION == 'false'");
        Set<DistributionModuleGroup> kuduConfigurationModuleGroups = new DynamicSparkBatchKuduNodeModuleGroup(pluginAdapter)
                .getModuleGroups(distribution, version, null);
        // ... in Spark batch
        nodeModuleGroupsMap.put(new NodeComponentTypeBean(ComponentType.SPARKBATCH, SparkBatchConstant.KUDU_INPUT_COMPONENT),
                kuduNodeModuleGroups);
        nodeModuleGroupsMap.put(new NodeComponentTypeBean(ComponentType.SPARKBATCH, SparkBatchConstant.KUDU_OUTPUT_COMPONENT),
                kuduNodeModuleGroups);
        nodeModuleGroupsMap.put(
                new NodeComponentTypeBean(ComponentType.SPARKBATCH, SparkBatchConstant.KUDU_CONFIGURATION_COMPONENT),
                kuduConfigurationModuleGroups);
    }

    protected void buildNodeModuleGroups4Pig(DynamicPluginAdapter pluginAdapter,
            Map<NodeComponentTypeBean, Set<DistributionModuleGroup>> nodeModuleGroupsMap, String version, String distribution)
            throws Exception {
        nodeModuleGroupsMap.put(new NodeComponentTypeBean(ComponentType.PIG, PigOutputConstant.PIGSTORE_COMPONENT),
                new DynamicPigOutputNodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version));
    }

    protected void buildNodeModuleGroups4MapReduce(DynamicPluginAdapter pluginAdapter,
            Map<NodeComponentTypeBean, Set<DistributionModuleGroup>> nodeModuleGroupsMap, String version, String distribution)
            throws Exception {
        nodeModuleGroupsMap.put(new NodeComponentTypeBean(ComponentType.MAPREDUCE, MRConstant.S3_INPUT_COMPONENT),
                new DynamicMRS3NodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version));
        nodeModuleGroupsMap.put(new NodeComponentTypeBean(ComponentType.MAPREDUCE, MRConstant.S3_OUTPUT_COMPONENT),
                new DynamicMRS3NodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version));
    }

    protected void buildNodeModuleGroups4HDFS(DynamicPluginAdapter pluginAdapter,
            Map<NodeComponentTypeBean, Set<DistributionModuleGroup>> nodeModuleGroupsMap, String version, String distribution)
            throws Exception {
        // WebHDFS/ADLS
        DynamicWebHDFSModuleGroup cdh5xWebHDFSModuleGroup = new DynamicWebHDFSModuleGroup(pluginAdapter);
        Set<DistributionModuleGroup> webHDFSNodeModuleGroups = cdh5xWebHDFSModuleGroup.getModuleGroups(distribution, version);
        for (String hdfsComponent : HDFSConstant.hdfsComponents) {
            nodeModuleGroupsMap.put(new NodeComponentTypeBean(ComponentType.HDFS, hdfsComponent), webHDFSNodeModuleGroups);
        }
    }

    @Override
    public String getVersion() {
        return versionId;
    }

    public String getVersionDisplay() {
        return versionDisplay;
    }

    protected DynamicPluginAdapter getPluginAdapter() {
        return pluginAdapter;
    }

    protected Map<ComponentType, Set<DistributionModuleGroup>> getModuleGroupsMap() {
        return moduleGroups;
    }

    protected Map<NodeComponentTypeBean, Set<DistributionModuleGroup>> getNodeModuleGroupsMap() {
        return nodeModuleGroups;
    }

    protected Map<ComponentType, ComponentCondition> getDisplayConditionsMap() {
        return displayConditions;
    }

    @Override
    public boolean isDynamicDistribution() {
        return true;
    }
}
