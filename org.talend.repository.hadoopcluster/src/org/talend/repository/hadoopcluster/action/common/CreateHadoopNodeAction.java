package org.talend.repository.hadoopcluster.action.common;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.ui.actions.metadata.AbstractCreateAction;
import org.talend.repository.ProjectManager;
import org.talend.repository.hadoopcluster.i18n.Messages;
import org.talend.repository.hadoopcluster.util.HCRepositoryUtil;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.StableRepositoryNode;

/**
 * 
 * created by ycbai on 2013-1-24 Detailled comment
 * 
 */
public abstract class CreateHadoopNodeAction extends AbstractCreateAction {

    protected static final int DEFAULT_WIZARD_WIDTH = 700;

    protected static final int DEFAULT_WIZARD_HEIGHT = 400;

    protected boolean creation = true;

    public CreateHadoopNodeAction() {
        super();
        this.setText(getCreateLabel());
        this.setToolTipText(getEditLabel());
        this.setImageDescriptor(ImageProvider.getImageDesc(getNodeImage()));
    }

    public CreateHadoopNodeAction(boolean isToolbar) {
        this();
        setToolbar(isToolbar);
    }

    @Override
    protected void doRun() {
        if (repositoryNode == null) {
            repositoryNode = getCurrentRepositoryNode();
        }

        if (isToolbar()) {
            if (repositoryNode != null && repositoryNode.getContentType() != getNodeType()) {
                repositoryNode = null;
            }
            if (repositoryNode == null) {
                repositoryNode = getRepositoryNodeForDefault(getNodeType());
            }

        }

        IWizard wizard = getWizard(PlatformUI.getWorkbench(), creation, repositoryNode, getExistingNames());
        if (isToolbar()) {
            init(repositoryNode);
        }
        WizardDialog wizardDialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
        wizardDialog.setPageSize(getWizardWidth(), getWizardHeight());
        wizardDialog.create();
        wizardDialog.open();
    }

    @Override
    protected void init(RepositoryNode node) {
        ERepositoryObjectType nodeType = (ERepositoryObjectType) node.getProperties(EProperties.CONTENT_TYPE);
        if (nodeType == null) {
            return;
        }

        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        if (HCRepositoryUtil.isHadoopClusterNode(node)
                || (node instanceof StableRepositoryNode && getNodeType().equals(nodeType))) {
            if (factory.isUserReadOnlyOnCurrentProject() || !ProjectManager.getInstance().isInCurrentMainProject(node)
                    || (node.getObject() != null && factory.getStatus(node.getObject()) == ERepositoryStatus.DELETED)) {
                setEnabled(false);
                return;
            }
            this.setText(getCreateLabel());
            collectChildNames(node);
            creation = true;
            setEnabled(true);
            return;
        }

        if (!nodeType.equals(getNodeType())) {
            return;
        }

        switch (node.getType()) {
        case SIMPLE_FOLDER:
            if (node.getObject() != null && node.getObject().getProperty().getItem().getState().isDeleted()) {
                setEnabled(false);
                return;
            }
            break;
        case SYSTEM_FOLDER:
            if (factory.isUserReadOnlyOnCurrentProject() || !ProjectManager.getInstance().isInCurrentMainProject(node)) {
                setEnabled(false);
                return;
            }
            this.setText(getCreateLabel());
            collectChildNames(node);
            creation = true;
            break;
        case REPOSITORY_ELEMENT:
            if (factory.isPotentiallyEditable(node.getObject())) {
                this.setText(getEditLabel());
                collectSiblingNames(node);
            } else {
                this.setText(getOpenLabel());
            }
            collectSiblingNames(node);
            creation = false;
            break;
        default:
            return;
        }

        setEnabled(true);
    }

    protected int getWizardWidth() {
        return DEFAULT_WIZARD_WIDTH;
    }

    protected int getWizardHeight() {
        return DEFAULT_WIZARD_HEIGHT;
    }

    protected String getCreateLabel() {
        return Messages.getString("CreateHadoopNodeAction.createLabel", getNodeType().getKey()); //$NON-NLS-1$
    }

    protected String getEditLabel() {
        return Messages.getString("CreateHadoopNodeAction.editLabel", getNodeType().getKey()); //$NON-NLS-1$
    }

    protected String getOpenLabel() {
        return Messages.getString("CreateHadoopNodeAction.openLabel", getNodeType().getKey()); //$NON-NLS-1$
    }

    protected abstract ERepositoryObjectType getNodeType();

    protected abstract IImage getNodeImage();

    protected abstract IWizard getWizard(IWorkbench workbench, boolean isCreate, RepositoryNode node, String[] existingNames);

}
