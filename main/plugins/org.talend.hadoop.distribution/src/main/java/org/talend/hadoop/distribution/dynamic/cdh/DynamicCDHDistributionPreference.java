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
package org.talend.hadoop.distribution.dynamic.cdh;

import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.talend.hadoop.distribution.dynamic.pref.AbstractDynamicDistributionPreference;


/**
 * DOC cmeng  class global comment. Detailled comment
 */
public class DynamicCDHDistributionPreference extends AbstractDynamicDistributionPreference {

    private static final String PREF_OVERRIDE_DEFAULT_SETUP = "distribution.dynamic.repository.cdh.overrideDefaultSetup"; //$NON-NLS-1$

    private static final boolean PREF_OVERRIDE_DEFAULT_SETUP_DEFAULT = false;

    private static final String PREF_REPOSITORY = "distribution.dynamic.repository.cdh.repository"; //$NON-NLS-1$

    private static final String PREF_REPOSITORY_DEFAULT = "https://talend-update.talend.com/nexus/content/repositories/cdh-releases-rcs/"; //$NON-NLS-1$

    private static final String PREF_ANONYMOUS = "distribution.dynamic.repository.cdh.isAnonymous"; //$NON-NLS-1$

    private static final boolean PREF_ANONYMOUS_DEFAULT = false;

    private static final String PREF_USERNAME = "distribution.dynamic.repository.cdh.username"; //$NON-NLS-1$

    private static final String PREF_USERNAME_DEFAULT = "studio-dl-client"; //$NON-NLS-1$

    private static final String PREF_PASSWORD = "distribution.dynamic.repository.cdh.password"; //$NON-NLS-1$

    private static final String PREF_PASSWORD_DEFAULT = "studio-dl-client"; //$NON-NLS-1$

    /**
     * Use preference factory to get new instance
     * 
     * @param store
     */
    public DynamicCDHDistributionPreference(ScopedPreferenceStore store) {
        super(store);
    }

    @Override
    protected String getPrefKeyOverrideDefaultSetup() {
        return PREF_OVERRIDE_DEFAULT_SETUP;
    }

    @Override
    protected String getPrefKeyRepository() {
        return PREF_REPOSITORY;
    }

    @Override
    protected String getPrefKeyAnonymous() {
        return PREF_ANONYMOUS;
    }

    @Override
    protected String getPrefKeyUsername() {
        return PREF_USERNAME;
    }

    @Override
    protected String getPrefKeyPassword() {
        return PREF_PASSWORD;
    }

    @Override
    protected boolean getPrefDefaultOverrideDefaultSetup() {
        return PREF_OVERRIDE_DEFAULT_SETUP_DEFAULT;
    }

    @Override
    protected String getPrefDefaultRepository() {
        return PREF_REPOSITORY_DEFAULT;
    }

    @Override
    protected boolean getPrefDefaultAnonymous() {
        return PREF_ANONYMOUS_DEFAULT;
    }

    @Override
    protected String getPrefDefaultUsername() {
        return PREF_USERNAME_DEFAULT;
    }

    @Override
    protected String getPrefDefaultPassword() {
        return PREF_PASSWORD_DEFAULT;
    }

}
