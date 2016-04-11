// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.designer.hdfsbrowse.util;

/**
 * DOC ycbai class global comment. Detailled comment
 */
public enum EHDFSRepositoryToComponent {

    DISTRIBUTION("DISTRIBUTION", "DISTRIBUTION"), //$NON-NLS-1$ //$NON-NLS-2$

    DB_VERSION("DB_VERSION", "DB_VERSION"), //$NON-NLS-1$ //$NON-NLS-2$

    HADOOP_CUSTOM_JARS("HADOOP_CUSTOM_JARS", "HADOOP_CUSTOM_JARS"), //$NON-NLS-1$ //$NON-NLS-2$

    HADOOP_CUSTOM_JARS_FOR_SPARK("HADOOP_CUSTOM_JARS_FOR_SPARK", "HADOOP_CUSTOM_JARS_FOR_SPARK"), //$NON-NLS-1$ //$NON-NLS-2$

    HADOOP_CUSTOM_JARS_FOR_SPARKSTREAMING("HADOOP_CUSTOM_JARS_FOR_SPARKSTREAMING", "HADOOP_CUSTOM_JARS_FOR_SPARKSTREAMING"), //$NON-NLS-1$ //$NON-NLS-2$

    USE_YARN("USE_YARN", "USE_YARN"), //$NON-NLS-1$//$NON-NLS-2$

    AUTHENTICATION_MODE("AUTHENTICATION_MODE", "AUTHENTICATION_MODE"), //$NON-NLS-1$ //$NON-NLS-2$

    FS_DEFAULT_NAME("FS_DEFAULT_NAME", "FS_DEFAULT_NAME"), //$NON-NLS-1$ //$NON-NLS-2$

    SET_SCHEDULER_ADDRESS("SET_SCHEDULER_ADDRESS", "SET_SCHEDULER_ADDRESS"), //$NON-NLS-1$//$NON-NLS-2$

    RESOURCEMANAGER_SCHEDULER_ADDRESS("RESOURCEMANAGER_SCHEDULER_ADDRESS", "RESOURCEMANAGER_SCHEDULER_ADDRESS"), //$NON-NLS-1$//$NON-NLS-2$

    SET_JOBHISTORY_ADDRESS("SET_JOBHISTORY_ADDRESS", "SET_JOBHISTORY_ADDRESS"), //$NON-NLS-1$//$NON-NLS-2$

    JOBHISTORY_ADDRESS("JOBHISTORY_ADDRESS", "JOBHISTORY_ADDRESS"), //$NON-NLS-1$//$NON-NLS-2$

    SET_STAGING_DIRECTORY("SET_STAGING_DIRECTORY", "SET_STAGING_DIRECTORY"), //$NON-NLS-1$//$NON-NLS-2$

    STAGING_DIRECTORY("STAGING_DIRECTORY", "STAGING_DIRECTORY"), //$NON-NLS-1$//$NON-NLS-2$

    USE_DATANODE_HOSTNAME("USE_DATANODE_HOSTNAME", "USE_DATANODE_HOSTNAME"), //$NON-NLS-1$//$NON-NLS-2$

    USE_KRB("USE_KRB", "USE_KRB"), //$NON-NLS-1$ //$NON-NLS-2$

    NAMENODE_PRINCIPAL("NAMENODE_PRINCIPAL", "NAMENODE_PRINCIPAL"), //$NON-NLS-1$ //$NON-NLS-2$

    JOBTRACKER_PRINCIPAL("JOBTRACKER_PRINCIPAL", "JOBTRACKER_PRINCIPAL"), //$NON-NLS-1$ //$NON-NLS-2$

    RESOURCEMANAGER_PRINCIPAL("RESOURCEMANAGER_PRINCIPAL", "RESOURCEMANAGER_PRINCIPAL"), //$NON-NLS-1$ //$NON-NLS-2$

    JOBHISTORY_PRINCIPAL("JOBHISTORY_PRINCIPAL", "JOBHISTORY_PRINCIPAL"), //$NON-NLS-1$ //$NON-NLS-2$

    USE_KEYTAB("USE_KEYTAB", "USE_KEYTAB"), //$NON-NLS-1$//$NON-NLS-2$

    KEYTAB_PRINCIPAL("KEYTAB_PRINCIPAL", "PRINCIPAL"), //$NON-NLS-1$//$NON-NLS-2$

    KEYTAB_PATH("KEYTAB_PATH", "KEYTAB_PATH"), //$NON-NLS-1$//$NON-NLS-2$

    USERNAME("USERNAME", "USERNAME"), //$NON-NLS-1$ //$NON-NLS-2$

    GROUP("GROUP", "GROUP"), //$NON-NLS-1$ //$NON-NLS-2$

    FILENAME("FILENAME", "FILENAME"), //$NON-NLS-1$ //$NON-NLS-2$

    FILETYPE("FILETYPE", "TYPEFILE"), //$NON-NLS-1$//$NON-NLS-2$

    KEYCOLUMN("KEYCOLUMN", "KEYCOLUMN"), //$NON-NLS-1$ //$NON-NLS-2$

    VALUECOLUMN("VALUECOLUMN", "VALUECOLUMN"), //$NON-NLS-1$ //$NON-NLS-2$

    ROWSEPARATOR("ROWSEPARATOR", "ROWSEPARATOR"), //$NON-NLS-1$ //$NON-NLS-2$

    FIELDSEPARATOR("FIELDSEPARATOR", "FIELDSEPARATOR"), //$NON-NLS-1$ //$NON-NLS-2$

    HEADER("HEADER", "HEADER"), //$NON-NLS-1$ //$NON-NLS-2$

    LOCAL("LOCAL", "LOCAL"), //$NON-NLS-1$ //$NON-NLS-2$

    MAPREDUCE("MAPREDUCE", "MAPREDUCE"), //$NON-NLS-1$ //$NON-NLS-2$

    PIG_VERSION("PIG_VERSION", "PIG_VERSION"), //$NON-NLS-1$ //$NON-NLS-2$

    MAPRED_JOB_TRACKER("MAPRED_JOB_TRACKER", "JOBTRACKER"), //$NON-NLS-1$ //$NON-NLS-2$

    RESOURCE_MANAGER("RESOURCE_MANAGER", "RESOURCE_MANAGER"), //$NON-NLS-1$ //$NON-NLS-2$

    MAPRED_RESOURCE_MANAGER("MAPRED_RESOURCE_MANAGER", "RESOURCE_MANAGER"), //$NON-NLS-1$ //$NON-NLS-2$

    FIELD_SEPARATOR_CHAR("FIELD_SEPARATOR_CHAR", "FIELD_SEPARATOR_CHAR"), //$NON-NLS-1$ //$NON-NLS-2$

    LOAD("LOAD", "LOAD"), //$NON-NLS-1$ //$NON-NLS-2$

    INPUT_FILENAME("INPUT_FILENAME", "INPUT_FILENAME"), //$NON-NLS-1$ //$NON-NLS-2$

    HADOOP_ADVANCED_PROPERTIES("HADOOP_ADVANCED_PROPERTIES", "HADOOP_ADVANCED_PROPERTIES"), //$NON-NLS-1$ //$NON-NLS-2$
    
    USE_CLOUDERA_NAVIGATOR("USE_CLOUDERA_NAVIGATOR","USE_CLOUDERA_NAVIGATOR"),//$NON-NLS-1$ //$NON-NLS-2$
    
    CLOUDERA_NAVIGATOR_USERNAME("CLOUDERA_NAVIGATOR_USERNAME","CLOUDERA_NAVIGATOR_USERNAME"),//$NON-NLS-1$ //$NON-NLS-2$
    
    CLOUDERA_NAVIGATOR_PASSWORD("CLOUDERA_NAVIGATOR_PASSWORD","CLOUDERA_NAVIGATOR_PASSWORD"),//$NON-NLS-1$ //$NON-NLS-2$
    
    CLOUDERA_NAVIGATOR_URL("CLOUDERA_NAVIGATOR_URL","CLOUDERA_NAVIGATOR_URL"),//$NON-NLS-1$ //$NON-NLS-2$
    
    CLOUDERA_NAVIGATOR_METADATA_URL("CLOUDERA_NAVIGATOR_METADATA_URL","CLOUDERA_NAVIGATOR_METADATA_URL"),//$NON-NLS-1$ //$NON-NLS-2$
    
    CLOUDERA_NAVIGATOR_CLIENT_URL("CLOUDERA_NAVIGATOR_CLIENT_URL","CLOUDERA_NAVIGATOR_CLIENT_URL"),//$NON-NLS-1$ //$NON-NLS-2$
    
    CLOUDERA_NAVIGATOR_AUTOCOMMIT("CLOUDERA_NAVIGATOR_AUTOCOMMIT","CLOUDERA_NAVIGATOR_AUTOCOMMIT"),//$NON-NLS-1$ //$NON-NLS-2$
    
    CLOUDERA_NAVIGATOR_DISABLE_SSL_VALIDATION("CLOUDERA_NAVIGATOR_DISABLE_SSL_VALIDATION","CLOUDERA_NAVIGATOR_DISABLE_SSL_VALIDATION"),//$NON-NLS-1$ //$NON-NLS-2$
    
    CLOUDERA_NAVIGATOR_DIE_ON_ERROR("CLOUDERA_NAVIGATOR_DIE_ON_ERROR","CLOUDERA_NAVIGATOR_DIE_ON_ERROR");//$NON-NLS-1$ //$NON-NLS-2$

    ;

    public final String repositoryValue;

    public final String parameterName;

    EHDFSRepositoryToComponent(String repositoryValue, String parameterName) {
        this.repositoryValue = repositoryValue;
        this.parameterName = parameterName;
    }

    public String getRepositoryValue() {
        return repositoryValue;
    }

    public String getParameterName() {
        return parameterName;
    }

}
