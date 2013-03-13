// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.oozie.scheduler.controller;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.client.WorkflowJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.CorePlugin;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.hadoop.IOozieService;
import org.talend.core.model.components.ComponentCategory;
import org.talend.core.model.process.IContext;
import org.talend.core.model.process.IProcess2;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.utils.JavaResourcesHelper;
import org.talend.core.prefs.ITalendCorePrefConstants;
import org.talend.designer.core.model.components.EOozieParameterName;
import org.talend.designer.core.ui.AbstractMultiPageTalendEditor;
import org.talend.designer.core.ui.editor.cmd.PropertyChangeCommand;
import org.talend.designer.hdfsbrowse.controllers.HDFSBrowseDialog;
import org.talend.designer.hdfsbrowse.model.EHadoopFileTypes;
import org.talend.designer.hdfsbrowse.model.HDFSConnectionBean;
import org.talend.designer.hdfsbrowse.model.IHDFSNode;
import org.talend.oozie.scheduler.actions.SaveJobBeforeRunAction;
import org.talend.oozie.scheduler.constants.JobSubmissionType;
import org.talend.oozie.scheduler.constants.OozieJobProcessStatus;
import org.talend.oozie.scheduler.constants.TOozieCommonConstants;
import org.talend.oozie.scheduler.constants.TOozieOutputMessages;
import org.talend.oozie.scheduler.exceptions.OozieJobDeployException;
import org.talend.oozie.scheduler.exceptions.OozieJobException;
import org.talend.oozie.scheduler.jobdeployer.OozieJobDeployer;
import org.talend.oozie.scheduler.jobsubmission.RemoteJobSubmission;
import org.talend.oozie.scheduler.jobsubmission.ScheduledJobSubmission;
import org.talend.oozie.scheduler.jobsubmission.model.JobContext;
import org.talend.oozie.scheduler.jobsubmission.model.JobContext.Timeunit;
import org.talend.oozie.scheduler.jobsubmission.model.JobSubmission;
import org.talend.oozie.scheduler.jobsubmission.model.JobSubmissionException;
import org.talend.oozie.scheduler.ui.ExecuteJobComposite;
import org.talend.oozie.scheduler.ui.SelectRepositoryDialog;
import org.talend.oozie.scheduler.ui.TOozieSchedulerDialog;
import org.talend.oozie.scheduler.ui.TOozieSettingDialog;
import org.talend.oozie.scheduler.utils.TOozieCommonUtils;
import org.talend.oozie.scheduler.utils.TOozieParamUtils;
import org.talend.oozie.scheduler.utils.TOozieStringUtils;
import org.talend.oozie.scheduler.views.OozieJobTrackerListener;
import org.talend.oozie.scheduler.views.TOozieView;

/**
 * Created by Marvin Wang on Mar. 31, 2012 for doing some action from the widgets of
 * {@link ExecuteJobCompositeController}, all businesses are handled here.
 */
public class ExecuteJobCompositeController {

    private final static int FROM_PREFERNCES = 0;

    private final static int FROM_REPOSITORY = 1;

    private ExecuteJobComposite executeJobComposite;

    private TOozieSchedulerDialog schedulingDialog;

    private TOozieSettingDialog settingDialog;

    private boolean isSettingDone = false;// To check if the values of setting are done.

    boolean isRunBtnEnabled = false;// Identify if the "Run" button is enabled.

    boolean isScheduleBtnEnabled = false;// Identify if the "Schedule" button is enabled.

    boolean isKillBtnEnabled = false;// Identify if the "Kill" button is enabled.

    private RemoteJobSubmission remoteJobSubmission;

    private ScheduledJobSubmission scheduledJobSubmission;

    TOozieJobTraceManager traceManager;

    public ExecuteJobCompositeController(ExecuteJobComposite executeJobComposite) {
        traceManager = TOozieJobTraceManager.getInstance();
        this.executeJobComposite = executeJobComposite;
    }

    public void initValues() {
        Text pathText = executeJobComposite.getPathText();
        Text outputText = executeJobComposite.getOutputTxt();

        if (!pathText.isDisposed() && !outputText.isDisposed()) {
            final IProcess2 process = OozieJobTrackerListener.getProcess();
            if (process != null) {

                String jobIdInOozie = CorePlugin.getDefault().getPreferenceStore()
                        .getString(TOozieCommonConstants.OOZIE_PREFIX_FOR_PREFERENCE + process.getLabel());
                String appPath = (String) process.getElementParameter(EOozieParameterName.HADOOP_APP_PATH.getName()).getValue();
                pathText.setText(appPath);
                // update output value for each editor.

                // String outputValue = tracesForOozie.get(jobIdInOozie);
                initRepositoryValues();
                String outputValue = traceManager.getTrace(jobIdInOozie);
                updateOutputTextContents(outputValue == null ? "" : outputValue, process.getLabel());

                updatePathTxtEnabledOrNot();
                updateOutputTxtEnabledOrNot();
                // updateAllEnabledOrNot();
                // updateOutputTxtValue();
            } else {
                pathText.setText("");
                outputText.setText("");
                updatePathTxtEnabledOrNot();
                updateOutputTxtEnabledOrNot();
            }
        }
    }

    private void initRepositoryValues() {
        if (isFromRepository()) {
            Text reponsitoryText = executeJobComposite.getRepositoryText();
            reponsitoryText.setText(executeJobComposite.getRepositoryTextValue());
        }
    }

    /**
     * When clicking the "Schedule" button, this action will open a dialog to provide some configurations for
     * scheduling. If <code>Window.OK == Dialog.open</code>, will invoke the method.
     */
    public void doScheduleAction(IContext context) {
        Shell shell = executeJobComposite.getShell();
        schedulingDialog = new TOozieSchedulerDialog(shell);
        initScheduling(schedulingDialog);
        if (Window.OK == schedulingDialog.open()) {
            if (!isJobReadOnly()) {
                saveJobBeforeRun();
            }
            JobContext jobContext = initJobContextForOozie(JobSubmissionType.SCHEDULED, context);
            updateAllEnabledOrNot(OozieJobProcessStatus.PREP, OozieJobTrackerListener.getProcess().getLabel());
            doScheduleJob(jobContext);
        }
    }

    protected void initScheduling(TOozieSchedulerDialog schedulingDialog) {
        schedulingDialog.setFrequencyValue("1");
        schedulingDialog.setTimeUnitItemValues(TOozieCommonUtils.getTimeUnitItems());
    }

    private void doScheduleJob(final JobContext jobContext) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                final String jobName = jobContext.getJobName();
                Job runJob = new Job(jobName) {

                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        StringBuffer output = new StringBuffer();
                        monitor.beginTask("Scheduling job...", 100);
                        try {
                            monitor.worked(10);
                            updateAllEnabledOrNot(OozieJobProcessStatus.DEPLOYING, jobName);
                            deployJobOnHadoop(output, jobContext);
                            monitor.worked(30);
                            // updateAllEnabledOrNot(OozieJobProcessStatus.PREP,
                            // OozieJobTrackerListener.getProcess().getLabel());

                            scheduledJobSubmission = new ScheduledJobSubmission();
                            OozieClient oozieClient = new OozieClient(getOozieEndPoint());
                            String jobIdInOozie = scheduledJobSubmission.submit(jobContext);

                            output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_RUNNING));
                            traceManager.putTrace(jobIdInOozie, output.toString());
                            updateOutputTextContents(output.toString(), jobName);
                            updateAllEnabledOrNot(OozieJobProcessStatus.RUNNING, jobName);

                            CorePlugin.getDefault().getPreferenceStore()
                                    .putValue(TOozieCommonConstants.OOZIE_PREFIX_FOR_PREFERENCE + jobName, jobIdInOozie);
                            outputScheduleJobLogs(oozieClient, jobIdInOozie, jobContext, output);
                        } catch (JobSubmissionException e) {
                            ExceptionHandler.process(e);
                        } catch (InterruptedException e) {
                            ExceptionHandler.process(e);
                        } catch (URISyntaxException e) {
                            ExceptionHandler.process(e);
                        } catch (OozieJobDeployException e) {
                            output.append(outputLogWithPrefixDate(e.getMessage()));
                            output.append(outputLogWithPrefixDate(e.getCause().getMessage()));
                            updateOutputTextContents(output.toString(), jobName);
                        }
                        return Status.OK_STATUS;
                    }
                };
                runJob.setUser(true);
                runJob.schedule();
            }
        });
    }

    protected void checkJobStatus(String jobIdInOozie, JobSubmission jobSubmission, StringBuffer output, JobContext jobContext) {
        try {
            final String jobName = jobContext.getJobName();
            switch (jobSubmission.status(jobIdInOozie, getOozieEndPoint())) {
            case RUNNING:
                output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_RUNNING));
                break;
            case SUCCEEDED:
                output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_JOB_COMPLETE));
                traceManager.putTrace(jobIdInOozie, output.toString());
                updateOutputTextContents(output.toString(), jobName);
                break;
            case KILLED:
                output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_JOB_KILL));

                traceManager.putTrace(jobIdInOozie, output.toString());
                afterDoKillAction(jobIdInOozie, output);
                updateOutputTextContents(output.toString(), jobName);
                break;
            case FAILED:
                output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_JOB_FAILD));

                break;
            case PREP:
                output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_JOB_PREPARE));

                break;
            case SUSPENDED:
                output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_JOB_SUSPENDED));

                break;
            }

        } catch (JobSubmissionException e) {
            ExceptionHandler.process(e);
        } catch (OozieJobException e) {
            ExceptionHandler.process(e);
        }
    }

    private void outputScheduleJobLogs(final OozieClient oozieClient, final String jobIdInOozie, final JobContext jobContext,
            StringBuffer output) {
        try {
            final String jobName = jobContext.getJobName();
            // JobSubmission.Status status = scheduledJobSubmission.status(scheduledJobHandle,
            // jobContext.getOozieEndPoint());
            while (JobSubmission.Status.PREP == scheduledJobSubmission.status(jobIdInOozie, getOozieEndPoint())) {
                // output.append("Job is preparing to run...");
                // output.append(TOozieOutputMessages.LINE_BREAK_CHAR);
                // output.append(oozieClient.getCoordJobInfo(jobIdInOozie));
                // output.append(TOozieOutputMessages.LINE_BREAK_CHAR);
                // updateOutputTextContents(output.toString(), jobName);
                Thread.sleep(1000 * 2);
            }

            while (JobSubmission.Status.RUNNING == scheduledJobSubmission.status(jobIdInOozie, getOozieEndPoint())) {
                // output.append("Job is running...");
                // output.append(TOozieOutputMessages.LINE_BREAK_CHAR);
                // output.append(oozieClient.getJobInfo(jobIdInOozie));
                // output.append(TOozieOutputMessages.LINE_BREAK_CHAR);
                // updateOutputTextContents(output.toString(), jobName);
                Thread.sleep(1000 * 2);
            }

            switch (scheduledJobSubmission.status(jobIdInOozie, getOozieEndPoint())) {
            case SUCCEEDED:
                output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_JOB_COMPLETE));
                break;
            case KILLED:
                output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_JOB_KILL));
                break;
            case FAILED:
                output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_JOB_FAILD));
                break;
            case PREP:
                output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_JOB_PREPARE));
                break;
            case SUSPENDED:
                output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_JOB_SUSPENDED));
                break;
            }

            // output.append(Messages.getString("MSG_output_complete", new Object[] { jobIdFromOozie,
            // oozieClient.getJobInfo(jobIdFromOozie).getStatus() }));
            // output.append(TOozieOutputMessages.LINE_BREAK_CHAR);
            traceManager.putTrace(jobIdInOozie, output.toString());
            updateAllEnabledOrNot(OozieJobProcessStatus.SUCCEEDED, jobName);
            updateOutputTextContents(output.toString(), jobName);
        }
        // catch (OozieClientException e) {
        // ExceptionHandler.process(e);
        // }
        catch (InterruptedException e) {
            ExceptionHandler.process(e);
        } catch (JobSubmissionException e) {
            traceManager.putTrace(jobIdInOozie, output.toString());
            updateAllEnabledOrNot(OozieJobProcessStatus.KILLED, jobContext.getJobName());
            ExceptionHandler.process(e);
        }
    }

    /**
     * When clicking the "Run" button, this action will be invoked.<br>
     * Workflow job state valid transitions: <li>--> PREP <li>PREP --> RUNNING | KILLED <li>RUNNING --> SUSPENDED |
     * SUCCEEDED | KILLED | FAILED <li>SUSPENDED --> RUNNING | KILLED
     * 
     * @param iContext
     */
    public void doRunAction(IContext iContext) {
        JobContext jobContext = initJobContextForOozie(JobSubmissionType.REMOTE, iContext);
        if (!isJobReadOnly()) {
            saveJobBeforeRun();
        }
        String jobIdInOozie = null;
        jobIdInOozie = CorePlugin.getDefault().getPreferenceStore()
                .getString(TOozieCommonConstants.OOZIE_PREFIX_FOR_PREFERENCE + jobContext.getJobName());
        if (jobIdInOozie != null) {
            traceManager.removeTrace(jobIdInOozie);
        }
        // updateAllEnabledOrNot(OozieJobProcessStatus.INIT);
        doRunJob(jobContext);
    }

    private String doRunJob(final JobContext jobContext) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                final String jobName = jobContext.getJobName();
                Job runJob = new Job(jobName) {

                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        StringBuffer output = new StringBuffer("");
                        IStatus status = Status.OK_STATUS;
                        try {
                            monitor.beginTask(TOozieOutputMessages.MSG_OUTPUT_STARTUP, 100);
                            // startupRemoteJob(output);
                            monitor.worked(10);

                            monitor.subTask(TOozieOutputMessages.MSG_OUTPUT_DEPLOYING);
                            updateAllEnabledOrNot(OozieJobProcessStatus.DEPLOYING, jobName);
                            deployJobOnHadoop(output, jobContext);
                            monitor.worked(30);

                            status = runRemoteJob(monitor, jobContext, output);

                            return status;
                        } catch (InterruptedException e) {
                            updateAllEnabledOrNot(OozieJobProcessStatus.FAILED, jobName);
                            output.append(outputLogWithPrefixDate(e.getMessage()));
                            updateOutputTextContents(output.toString(), jobName);
                            ExceptionHandler.process(e);
                        } catch (JobSubmissionException e) {
                            updateAllEnabledOrNot(OozieJobProcessStatus.FAILED, jobName);
                            output.append(outputLogWithPrefixDate(e.getMessage()));
                            output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_ERROR_ERROR_SETTINGS));
                            if (e.getCause() instanceof OozieClientException) {
                                OozieClientException ooException = (OozieClientException) e.getCause();
                                output.append(outputLogWithPrefixDate(ooException.getErrorCode() + ". "
                                        + ooException.getMessage()));
                            } else {
                                output.append(outputLogWithPrefixDate(e.getCause().getMessage()));
                            }
                            updateOutputTextContents(output.toString(), jobName);
                        } catch (URISyntaxException e) {
                            updateAllEnabledOrNot(OozieJobProcessStatus.FAILED, jobName);
                            output.append(outputLogWithPrefixDate(e.getMessage()));
                            updateOutputTextContents(output.toString(), jobName);
                        } catch (OozieJobDeployException e) {
                            updateAllEnabledOrNot(OozieJobProcessStatus.FAILED, jobName);

                            output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_DEPLOY_FAILED));
                            output.append(outputLogWithPrefixDate(e.getMessage()));
                            output.append(outputLogWithPrefixDate(e.getCause().getMessage()));
                            updateOutputTextContents(output.toString(), jobName);
                        }
                        return Status.OK_STATUS;
                    }
                };
                runJob.setUser(true);
                runJob.schedule();
            }
        });
        return null;
    }

    /**
     * Deploys job on Hadoop, about the detail steps refer to
     * {@link OozieJobDeployer#deployJob(IProcess2, IProgressMonitor)}.
     * 
     * @param output
     * @param jobContext
     * @throws OozieJobDeployException
     */
    private void deployJobOnHadoop(StringBuffer output, JobContext jobContext) throws OozieJobDeployException {
        output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_DEPLOYING));
        updateOutputTextContents(output.toString(), jobContext.getJobName());

        OozieJobDeployer.deployJob(OozieJobTrackerListener.getProcess(), new NullProgressMonitor());
        output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_DEPLOY_COMPLETE));
        updateOutputTextContents(output.toString(), jobContext.getJobName());
    }

    /**
     * Submits a job to run remotely on Hadoop,
     * 
     * @param monitor
     * @param jobContext
     * @param output
     * @return
     * @throws JobSubmissionException
     * @throws InterruptedException
     * @throws URISyntaxException
     * @throws OozieClientException
     */
    private IStatus runRemoteJob(IProgressMonitor monitor, JobContext jobContext, StringBuffer output)
            throws JobSubmissionException, InterruptedException, URISyntaxException {
        String jobName = jobContext.getJobName();

        remoteJobSubmission = new RemoteJobSubmission();
        String jobIdInOozie = remoteJobSubmission.submit(jobContext);
        monitor.subTask(TOozieOutputMessages.MSG_OUTPUT_RUNNING);

        output.append(outputLogWithPrefixDate(TOozieOutputMessages.MSG_OUTPUT_RUNNING));
        updateOutputTextContents(output.toString(), jobName);
        updateAllEnabledOrNot(OozieJobProcessStatus.RUNNING, jobName);

        CorePlugin.getDefault().getPreferenceStore()
                .putValue(TOozieCommonConstants.OOZIE_PREFIX_FOR_PREFERENCE + jobName, jobIdInOozie);
        if (monitor.isCanceled()) {
            output.append(">> The running job is canceled!");
            updateOutputTextContents(output.toString(), jobName);
            return Status.CANCEL_STATUS;
        }
        monitor.worked(10);
        monitor.subTask("Logging...");

        traceManager.putTrace(jobIdInOozie, output.toString());
        while (jobIdInOozie != null && !"".equals(jobIdInOozie)
                && JobSubmission.Status.RUNNING == remoteJobSubmission.status(jobIdInOozie, getOozieEndPoint())) {

            Thread.sleep(1000 * 2);
        }
        checkJobStatus(jobIdInOozie, remoteJobSubmission, output, jobContext);
        updateAllEnabledOrNot(
                TOozieCommonUtils.convertToOozieJobProcessStatus(remoteJobSubmission.status(jobIdInOozie, getOozieEndPoint())),
                jobName);
        monitor.worked(50);

        return Status.OK_STATUS;
    }

    /**
     * Updates the contents of the widget "Output" using logs and status.
     * 
     * @param output
     */
    private void updateOutputTextContents(final String output, final String oozieJobName) {
        if (OozieJobTrackerListener.getProcess() != null) {
            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    IViewReference[] viewRef = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .getViewReferences();
                    if (viewRef != null && viewRef.length > 0) {
                        for (IViewReference element : viewRef) {
                            if (element.getView(true) instanceof TOozieView) {
                                if (OozieJobTrackerListener.getProcess() != null
                                        && OozieJobTrackerListener.getProcess().getLabel().equals(oozieJobName)) {
                                    TOozieView view = (TOozieView) element.getView(true);
                                    executeJobComposite = view.getExecuteJobComposite();
                                    Text outputTxt = executeJobComposite.getOutputTxt();
                                    if (!outputTxt.isDisposed()) {
                                        outputTxt.setText("");
                                        outputTxt.setText(output);
                                        int lines = outputTxt.getLineCount();
                                        outputTxt.setTopIndex(lines);
                                    }
                                }
                            }
                        }
                    }
                }

            });
        }
    }

    protected void updateAllEnabledOrNot(final OozieJobProcessStatus status, final String oozieJobName) {
        if (OozieJobTrackerListener.getProcess() != null) {
            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    IViewReference[] viewRef = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .getViewReferences();
                    if (viewRef != null && viewRef.length > 0) {
                        for (IViewReference element : viewRef) {
                            if (element.getView(true) instanceof TOozieView
                                    && OozieJobTrackerListener.getProcess().getLabel().equals(oozieJobName)) {
                                TOozieView view = (TOozieView) element.getView(true);
                                executeJobComposite = view.getExecuteJobComposite();
                                Button runBtn = executeJobComposite.getRunBtn();
                                Button scheduleBtn = executeJobComposite.getScheduleBtn();
                                Button killBtn = executeJobComposite.getKillBtn();
                                Button selectBtn = executeJobComposite.getSelectbtn();
                                Text pathTxt = executeJobComposite.getPathText();
                                Text outputTxt = executeJobComposite.getOutputTxt();
                                Combo combo = executeJobComposite.getServerCombo();
                                switch (status) {
                                case INIT:
                                    break;
                                case DEPLOYING:
                                    runBtn.setEnabled(false);
                                    scheduleBtn.setEnabled(false);
                                    killBtn.setEnabled(false);
                                    selectBtn.setEnabled(false);
                                    pathTxt.setEnabled(false);
                                    combo.setEnabled(false);
                                    outputTxt.setEnabled(true);
                                    break;
                                case PREP:
                                    runBtn.setEnabled(false);
                                    scheduleBtn.setEnabled(false);
                                    killBtn.setEnabled(true);
                                    selectBtn.setEnabled(false);
                                    pathTxt.setEnabled(false);
                                    combo.setEnabled(false);
                                    outputTxt.setEnabled(true);
                                    break;
                                case RUNNING:
                                    runBtn.setEnabled(false);
                                    scheduleBtn.setEnabled(false);
                                    killBtn.setEnabled(true);
                                    selectBtn.setEnabled(false);
                                    pathTxt.setEnabled(false);
                                    combo.setEnabled(false);
                                    outputTxt.setEnabled(true);
                                    break;
                                case SUCCEEDED:
                                    runBtn.setEnabled(true);
                                    scheduleBtn.setEnabled(true);
                                    killBtn.setEnabled(false);
                                    selectBtn.setEnabled(true);
                                    pathTxt.setEnabled(true);
                                    if (GlobalServiceRegister.getDefault().isServiceRegistered(IOozieService.class)) {
                                        combo.setEnabled(true);
                                    }
                                    outputTxt.setEnabled(true);
                                    break;
                                case KILLED:
                                    runBtn.setEnabled(true);
                                    scheduleBtn.setEnabled(true);
                                    killBtn.setEnabled(false);
                                    selectBtn.setEnabled(true);
                                    pathTxt.setEnabled(true);
                                    if (GlobalServiceRegister.getDefault().isServiceRegistered(IOozieService.class)) {
                                        combo.setEnabled(true);
                                    }
                                    outputTxt.setEnabled(true);
                                    break;
                                case FAILED:
                                    runBtn.setEnabled(true);
                                    scheduleBtn.setEnabled(true);
                                    killBtn.setEnabled(false);
                                    selectBtn.setEnabled(true);
                                    pathTxt.setEnabled(true);
                                    if (GlobalServiceRegister.getDefault().isServiceRegistered(IOozieService.class)) {
                                        combo.setEnabled(true);
                                    }
                                    outputTxt.setEnabled(true);
                                    break;
                                case SUSPENDED:
                                    runBtn.setEnabled(true);
                                    scheduleBtn.setEnabled(false);
                                    killBtn.setEnabled(true);
                                    selectBtn.setEnabled(true);
                                    pathTxt.setEnabled(false);
                                    if (GlobalServiceRegister.getDefault().isServiceRegistered(IOozieService.class)) {
                                        combo.setEnabled(true);
                                    }
                                    outputTxt.setEnabled(true);
                                    break;
                                }

                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Workflow job state valid transitions: <li>--> PREP <li>PREP --> RUNNING | KILLED <li>RUNNING --> SUSPENDED |
     * SUCCEEDED | KILLED | FAILED <li>SUSPENDED --> RUNNING | KILLED
     * 
     * @param status
     */
    // protected void updateAllEnabledOrNot(final OozieJobProcessStatus status) {
    //
    // if (!executeJobComposite.isDisposed())
    // return;
    //
    // }

    public void doKillAction() {
        try {

            OozieClient oozieClient = new OozieClient(getOozieEndPoint());
            StringBuffer output = new StringBuffer("");
            String jobIdInOozie = CorePlugin
                    .getDefault()
                    .getPreferenceStore()
                    .getString(
                            TOozieCommonConstants.OOZIE_PREFIX_FOR_PREFERENCE + OozieJobTrackerListener.getProcess().getLabel());
            if (!StringUtils.isEmpty(jobIdInOozie)) {
                if (oozieClient.getJobInfo(jobIdInOozie).getStatus() == WorkflowJob.Status.RUNNING) {
                    output.append(executeJobComposite.getOutputTxt().getText());
                    oozieClient.kill(jobIdInOozie);
                    // updateJobIdInOozieForJob(null);
                    // System.out.println("Kill Job!");
                    // output.append(TOozieOutputMessages.MSG_OUTPUT_KILL);
                    // output.append(TOozieOutputMessages.LINE_BREAK_CHAR);
                    executeJobComposite.getKillBtn().setEnabled(false);
                    // if (multiPageTalendEditor != null)
                    // updateOutputTextContents(output.toString());
                } else if (oozieClient.getJobInfo(jobIdInOozie).getStatus() == WorkflowJob.Status.SUCCEEDED) {
                    System.out.println("Job is succeeded!");
                    executeJobComposite.getRunBtn().setEnabled(true);
                    executeJobComposite.getKillBtn().setEnabled(false);
                    // todo add schedule
                } else if (oozieClient.getJobInfo(jobIdInOozie).getStatus() == WorkflowJob.Status.KILLED) {
                    updateAllEnabledOrNot(OozieJobProcessStatus.KILLED, OozieJobTrackerListener.getProcess().getLabel());
                }
                // afterDoKillAction(jobIdInOozie);
            }
        } catch (OozieClientException e) {
            ExceptionHandler.process(e);
        }
    }

    private void afterDoKillAction(String killedJobId, StringBuffer output) throws OozieJobException {

        OozieClient oozieClient = new OozieClient(getOozieEndPoint());
        oozieClient.setDebugMode(0);
        try {
            List<WorkflowAction> actions = oozieClient.getJobInfo(killedJobId).getActions();
            if (actions != null && actions.size() > 0) {
                for (WorkflowAction action : actions) {
                    if (action.getErrorMessage() != null) {
                        output.append(outputLogWithPrefixDate(action.getErrorMessage()));
                    }
                }
            }
        } catch (OozieClientException e) {
            throw new OozieJobException("Job info can not be retrieved!", e);
        }
    }

    private AbstractMultiPageTalendEditor getEditor() {
        if (OozieJobTrackerListener.getProcess() == null) {
            return null;
        }
        return (AbstractMultiPageTalendEditor) OozieJobTrackerListener.getProcess().getEditor();
    }

    public CommandStack getCommandStack() {
        return getEditor() == null ? null : (CommandStack) (getEditor().getTalendEditor().getAdapter(CommandStack.class));
    }

    protected void saveJobBeforeRun() {
        if (OozieJobTrackerListener.getProcess() != null) {
            SaveJobBeforeRunAction runAction = new SaveJobBeforeRunAction(OozieJobTrackerListener.getProcess());
            runAction.run();
        }
    }

    /**
     * Format outputing message beginning with date like "[yyyy-MM-dd HH:mm:ss]Starting job remote running...". About
     * details refer to {@link TOozieStringUtils#outputLogWithPrefixDate(String)}.
     * 
     * @param message output message like "Starting job remote running...".
     * @return
     */
    private String outputLogWithPrefixDate(String message) {
        return TOozieStringUtils.outputLogWithPrefixDate(message);
    }

    protected String getJobIdInOozie() {
        String jobIdInOozie = null;
        if (OozieJobTrackerListener.getProcess() != null) {
            IProcess2 process = OozieJobTrackerListener.getProcess();
            jobIdInOozie = CorePlugin.getDefault().getPreferenceStore()
                    .getString(TOozieCommonConstants.OOZIE_PREFIX_FOR_PREFERENCE + process.getLabel());
        }
        return jobIdInOozie;
    }

    /**
     * 
     * @return
     */
    private JobContext initJobContextForOozie(JobSubmissionType jobSubType, IContext context) {
        JobContext jobContext = new JobContext();

        IProcess2 process = OozieJobTrackerListener.getProcess();
        if (ComponentCategory.CATEGORY_4_MAPREDUCE.getName().equals(process.getComponentsType())) {
            String fsForMapReduceJob = (String) process.getElementParameter("NAMENODE").getValue();//$NON-NLS-1$
            jobContext.set("-fs", fsForMapReduceJob); //$NON-NLS-1$

            String jtForMapReduceJob = (String) process.getElementParameter("JOBTRACKER").getValue();//$NON-NLS-1$
            jobContext.set("-jt", jtForMapReduceJob);//$NON-NLS-1$
        }

        // Job name.
        String jobName = process.getLabel();
        jobContext.setJobName(jobName);

        // Job path on HDFS
        String path = executeJobComposite.getPathValue();
        // IProcess2 process = OozieJobTrackerListener.getProcess();
        // String name = JavaResourcesHelper.getJobFolderName(process.getLabel(), process.getVersion()) + "." +
        // process.getLabel();
        // jobContext.setJobPathOnHDFS(path + "/" + name);
        jobContext.setJobPathOnHDFS(path);

        // Oozie end point
        String oozieEPValue = getOozieEndPoint();
        jobContext.setOozieEndPoint(oozieEPValue);

        // Name node end point
        String nameNodeEPValue = getNameNode();
        jobContext.setNameNodeEndPoint(nameNodeEPValue);

        // Job tracker end point
        String jobTrackerEPValue = getJobTracker();
        jobContext.setJobTrackerEndPoint(jobTrackerEPValue);

        // APP path
        // IPath appPath = new Path(nameNodeEPValue).append(path);
        // mhirt : bug fix on linux Path changes // to /. FIXME : Do a better coding for 5.1.1
        String appPath = (nameNodeEPValue != null) ? nameNodeEPValue.concat(path) : path;
        jobContext.set(OozieClient.APP_PATH, appPath);

        // User Name for acessing hadoop
        String userNameValue = getUserNameForHadoop();
        jobContext.set(OozieClient.USER_NAME, userNameValue);

        // TOS job
        String tosJobFQCN = getTOSJobFQCNValue();
        jobContext.setJobFQClassName(tosJobFQCN);
        jobContext.setTosContextPath(context.getName());

        switch (jobSubType) {
        case LOCAL:
            break;
        case REMOTE:
            break;
        case SCHEDULED:
            // Frequency
            int fequency = getFrequencyValue();
            jobContext.setFrequency(fequency);
            // Time unit
            Timeunit timeUnit = getTimeUnitValue();
            jobContext.setTimeUnit(timeUnit);
            // Start Time
            Date startTime = getStartTimeValue();
            jobContext.setStartTime(startTime);
            // End Time
            Date endTime = getEndTimeValue();
            jobContext.setEndTime(endTime);
            break;
        default:
        }

        return jobContext;
    }

    private Timeunit getTimeUnitValue() {
        int selectIndex = schedulingDialog.getSelectedTimeUnitIndex();
        return TOozieCommonUtils.lookupTimeUnit(selectIndex);
        // return Timeunit.MINUTE;
    }

    private int getFrequencyValue() {
        String frequencyValue = schedulingDialog.getFrequencyValue();
        int intValue = Integer.valueOf(frequencyValue).intValue();
        return intValue;
    }

    private Date getStartTimeValue() {
        // Calendar instance = Calendar.getInstance();
        // Date start = instance.getTime();
        Date startDate = schedulingDialog.getStartDate();
        return startDate;
    }

    private Date getEndTimeValue() {
        // Calendar instance = Calendar.getInstance();
        // instance.roll(3, 1);
        // Date end = instance.getTime();
        // return end;
        Date endDate = schedulingDialog.getEndDate();
        return endDate;
    }

    private boolean isFromRepository() {
        return TOozieParamUtils.isFromRepository();
    }

    private String getNameNode() {
        return TOozieParamUtils.getParamValue(ITalendCorePrefConstants.OOZIE_SHCEDULER_NAME_NODE_ENDPOINT);
    }

    private String getJobTracker() {
        return TOozieParamUtils.getParamValue(ITalendCorePrefConstants.OOZIE_SHCEDULER_JOB_TRACKER_ENDPOINT);
    }

    private String getOozieEndPoint() {
        return TOozieParamUtils.getParamValue(ITalendCorePrefConstants.OOZIE_SHCEDULER_OOZIE_ENDPOINT);
    }

    private String getUserNameForHadoop() {
        return TOozieParamUtils.getParamValue(ITalendCorePrefConstants.OOZIE_SCHEDULER_USER_NAME);
    }

    private String getHadoopDistribution() {
        return TOozieParamUtils.getParamValue(ITalendCorePrefConstants.OOZIE_SHCEDULER_HADOOP_DISTRIBUTION);
    }

    private String getHadoopVersion() {
        return TOozieParamUtils.getParamValue(ITalendCorePrefConstants.OOZIE_SHCEDULER_HADOOP_VERSION);
    }

    private String getTOSJobFQCNValue() {
        IProcess2 process = OozieJobTrackerListener.getProcess();
        return JavaResourcesHelper.getCurrentProjectName() + "."
                + JavaResourcesHelper.getJobFolderName(process.getLabel(), process.getVersion()) + "." + process.getLabel();
    }

    public void doSelectComboAction() {
        Combo selectCombo = executeJobComposite.getServerCombo();
        Text repositaryText = executeJobComposite.getRepositoryText();
        Button editBtn = executeJobComposite.getBtnEdit();
        // Text pathText = executeJobComposite.getPathText();

        hideSettingBtn();
        if (selectCombo.getSelectionIndex() == FROM_REPOSITORY && executeJobComposite.getRepositoryTextValue().length() == 0) {
            repositaryText.setText("");
            editBtn.setEnabled(false);
        }
        if (selectCombo.getSelectionIndex() == FROM_PREFERNCES) {
            executeJobComposite.setRepositoryValue("");
            repositaryText.setText("");
            updateBtnButtonEnabledOrNot();
        }
    }

    private void hideSettingBtn() {
        Combo selectCombo = executeJobComposite.getServerCombo();
        Text repositaryText = executeJobComposite.getRepositoryText();
        Button selectButton = executeJobComposite.getSelectbtn();
        Button settingButton = executeJobComposite.getSettingBtn();

        repositaryText.setVisible(selectCombo.getSelectionIndex() == FROM_REPOSITORY);
        selectButton.setVisible(selectCombo.getSelectionIndex() == FROM_REPOSITORY);
        settingButton.setVisible(selectCombo.getSelectionIndex() == FROM_PREFERNCES);
    }

    /**
     * Handles the action when clicking the button "Setting".
     */
    public void doSettingAction() {
        Shell shell = executeJobComposite.getShell();
        settingDialog = new TOozieSettingDialog(shell);
        initPreferenceSettingForJob(settingDialog);
        if (Window.OK == settingDialog.open()) {
            // To update the values of Oozie preference page
            updateOoziePreferencePageValues();
            updateAllEnabledOrNot();
        }
    }

    /**
     * Initializes the setup before opening scheduler setting dialog. Sets back the job setting when a job is opened.
     */
    protected void initPreferenceSettingForJob(TOozieSettingDialog settingDialog) {
        String hadoopDistributionValue = getHadoopDistribution();
        String hadoopVersionValue = getHadoopVersion();
        String nameNodeEPValue = getNameNode();
        String jobTrackerEPValue = getJobTracker();
        String oozieEPValue = getOozieEndPoint();
        String userNameValue = getUserNameForHadoop();

        settingDialog.setHadoopDistributionValue(hadoopDistributionValue);
        settingDialog.setHadoopVersionValue(hadoopVersionValue);
        settingDialog.setNameNodeEndPointValue(nameNodeEPValue);
        settingDialog.setJobTrackerEndPointValue(jobTrackerEPValue);
        settingDialog.setOozieEndPointValue(oozieEPValue);
        settingDialog.setUserNameValue(userNameValue);
    }

    public void doModifyPathAction() {
        if (OozieJobTrackerListener.getProcess() != null) {
            String path = executeJobComposite.getPathValue();
            IProcess2 process = OozieJobTrackerListener.getProcess();
            process.getElementParameter(EOozieParameterName.HADOOP_APP_PATH.getName()).setValue(path);
        }
        // checkWidgetsStatus();
        updateRunBtnEnabledOrNot();
        updateScheduleBtnEnabledOrNot();
        updateKillBtnEnabledOrNot();
        updatePathTxtEnabledOrNot();
        updateOutputTxtEnabledOrNot();
        updateRepositoryTextEnabledOrNot();
        updateBtnButtonEnabledOrNot();
    }

    /**
     * Checks if the current process is read-only.
     * 
     * @return <code>true</code> if it is read-only, otherwise, <code>false</code>.
     */
    private boolean isJobReadOnly() {
        boolean isReadOnly = false;
        isReadOnly = OozieJobTrackerListener.getProcess().isReadOnly();
        return isReadOnly;
    }

    /**
     * when clicking the browser button,the HDFS browser dialog will be open.
     */
    public void doSetPathAction() {
        HDFSConnectionBean connection = new HDFSConnectionBean();
        connection.setDistribution(getHadoopDistribution());
        connection.setDfVersion(getHadoopVersion());
        connection.setNameNodeURI(getNameNode());
        connection.setUserName(getUserNameForHadoop());
        HDFSBrowseDialog dial = new HDFSBrowseDialog(executeJobComposite.getShell(), EHadoopFileTypes.FOLDER, connection);
        if (dial.open() == Window.OK) {
            IHDFSNode result = dial.getResult();
            String path = result.getPath();
            executeJobComposite.getPathText().setText(path == null ? "" : path);
        }
    }

    /**
     * When clicking the button named "Monitoring", this method will be invoked.
     */
    public void doMonitoringBtnAction() {
        String oozieURL = getOozieEndPoint();
        if (oozieURL == null || "".equals(oozieURL) || !oozieURL.startsWith("http://")) {
            MessageDialog.openWarning(executeJobComposite.getShell(), TOozieOutputMessages.MSG_WARNING_URL_TITLE,
                    TOozieOutputMessages.MSG_WARNING_URL_NOTVALID);
        } else {
            try {
                IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();

                IWebBrowser browser = support.getExternalBrowser();
                // Open SWT brower in Eclipse.
                // IWebBrowser browser = support.createBrowser("id");
                browser.openURL(new URL(oozieURL));
            } catch (PartInitException e) {
            } catch (MalformedURLException e) {
            }
        }
    }

    /**
     * To store all values in Oozie preference, values include "Name node end point", "Job tracker end point",
     * "Oozie end point", and "User name for hadoop".
     */
    private void updateOoziePreferencePageValues() {
        String hadoopDistributionValue = settingDialog.getHadoopDistributionValue();
        String hadoopVersionValue = settingDialog.getHadoopVersionValue();
        String nameNodeEPValue = settingDialog.getNameNodeEndPointValue();
        String jobTrackerEPValue = settingDialog.getJobTrackerEndPointValue();
        String oozieEPValue = settingDialog.getOozieEndPointValue();
        String userNameValue = settingDialog.getUserNameValue();

        CorePlugin.getDefault().getPreferenceStore()
                .setValue(ITalendCorePrefConstants.OOZIE_SHCEDULER_HADOOP_DISTRIBUTION, hadoopDistributionValue);
        CorePlugin.getDefault().getPreferenceStore()
                .setValue(ITalendCorePrefConstants.OOZIE_SHCEDULER_HADOOP_VERSION, hadoopVersionValue);
        CorePlugin.getDefault().getPreferenceStore()
                .setValue(ITalendCorePrefConstants.OOZIE_SHCEDULER_NAME_NODE_ENDPOINT, nameNodeEPValue);
        CorePlugin.getDefault().getPreferenceStore()
                .setValue(ITalendCorePrefConstants.OOZIE_SHCEDULER_JOB_TRACKER_ENDPOINT, jobTrackerEPValue);
        CorePlugin.getDefault().getPreferenceStore()
                .setValue(ITalendCorePrefConstants.OOZIE_SHCEDULER_OOZIE_ENDPOINT, oozieEPValue);
        CorePlugin.getDefault().getPreferenceStore().setValue(ITalendCorePrefConstants.OOZIE_SCHEDULER_USER_NAME, userNameValue);
    }

    /**
     * Checks if the "Run" button is enabled. Return <code>true</code> if "Path" value is not <code>null</code> and
     * empty, and all preference values are done, and job is not running on hadoop. Otherwise, return <code>false</code>
     * .
     * 
     * @return
     */
    public boolean checkIfRunBtnEnabled() {
        String jobIdInOozie = getJobIdInOozie();
        isRunBtnEnabled = false;
        String pathValue = executeJobComposite.getPathValue();
        if (pathValue != null && !"".equals(pathValue) && isSettingDone()) {
            if (jobIdInOozie != null && !"".equals(jobIdInOozie)) {
                try {
                    JobSubmission.Status status = checkJobSubmissionStaus(jobIdInOozie);
                    switch (status) {
                    case RUNNING:
                        isRunBtnEnabled = false;
                        break;
                    case PREP:
                    case SUCCEEDED:
                    case KILLED:
                    case FAILED:
                    case SUSPENDED:
                        isRunBtnEnabled = true;
                    }
                } catch (JobSubmissionException e) {
                    isRunBtnEnabled = true;
                    // ExceptionHandler.process(e);
                }
            } else {
                isRunBtnEnabled = true;
            }
        }

        return isRunBtnEnabled;
    }

    /**
     * Checks if the "Schedule" button is enabled. Return <code>true</code> if "Path" value is not <code>null</code> and
     * empty, and all preference values are done, and job is not running on hadoop. Otherwise, return <code>false</code>
     * .
     * 
     * @return
     */
    public boolean checkIfScheduleBtnEnabled() {
        String jobIdInOozie = getJobIdInOozie();
        isScheduleBtnEnabled = false;
        String pathValue = executeJobComposite.getPathValue();
        if (pathValue != null && !"".equals(pathValue) && isSettingDone()) {
            if (jobIdInOozie != null && !"".equals(jobIdInOozie)) {
                try {
                    JobSubmission.Status status = checkJobSubmissionStaus(jobIdInOozie);
                    switch (status) {
                    case RUNNING:
                        isScheduleBtnEnabled = false;
                        break;
                    case PREP:
                    case SUCCEEDED:
                    case KILLED:
                    case FAILED:
                    case SUSPENDED:
                        isScheduleBtnEnabled = true;
                    }
                } catch (JobSubmissionException e) {
                    isScheduleBtnEnabled = true;
                    // ExceptionHandler.process(e);
                }
            } else {
                isScheduleBtnEnabled = true;
            }
        }
        return isScheduleBtnEnabled;
    }

    public boolean checkIfKillBtnEnabled() {
        String jobIdInOozie = getJobIdInOozie();
        if (jobIdInOozie != null && !"".equals(jobIdInOozie)) {
            try {
                JobSubmission.Status status = checkJobSubmissionStaus(jobIdInOozie);
                switch (status) {
                case PREP:
                case RUNNING:
                case SUSPENDED:
                    isKillBtnEnabled = true;
                    break;
                case SUCCEEDED:
                case FAILED:
                case KILLED:
                    isKillBtnEnabled = false;
                    break;
                }
            } catch (JobSubmissionException e) {
                isKillBtnEnabled = false;
                // ExceptionHandler.process(e);
            }
        } else {
            isKillBtnEnabled = false;
        }
        return isKillBtnEnabled;
    }

    /**
     * Updates all buttons status, enabled or not.
     */
    public void updateAllEnabledOrNot() {
        updateRunBtnEnabledOrNot();
        updateScheduleBtnEnabledOrNot();
        updateKillBtnEnabledOrNot();
        updatePathTxtEnabledOrNot();
        updateOutputTxtEnabledOrNot();
        updateRepositoryTextEnabledOrNot();
        updateBtnButtonEnabledOrNot();
        updateEditBtnFromRepositoryEnabledOrNot();
        updateComboEnabledOrNot();
    }

    private void updateComboEnabledOrNot() {
        Combo combo = executeJobComposite.getServerCombo();
        if (combo.isDisposed()) {
            return;
        }
        if (OozieJobTrackerListener.getProcess() == null
                || !GlobalServiceRegister.getDefault().isServiceRegistered(IOozieService.class)) {
            combo.select(FROM_PREFERNCES);
            combo.setEnabled(false);
        } else {
            if (executeJobComposite.getRepositoryTextValue().length() != 0) {
                combo.select(FROM_REPOSITORY);
            } else {
                combo.select(FROM_PREFERNCES);
            }
            combo.setEnabled(true);
        }
        hideSettingBtn();
    }

    protected void updateRunBtnEnabledOrNot() {
        Button runBtn = executeJobComposite.getRunBtn();
        if (runBtn.isDisposed()) {
            return;
        }
        if (OozieJobTrackerListener.getProcess() == null) {
            runBtn.setEnabled(false);
        } else {
            runBtn.setEnabled(checkIfRunBtnEnabled());
        }
    }

    protected void updateScheduleBtnEnabledOrNot() {
        Button scheduleBtn = executeJobComposite.getScheduleBtn();
        if (scheduleBtn.isDisposed()) {
            return;
        }
        if (OozieJobTrackerListener.getProcess() == null) {
            scheduleBtn.setEnabled(false);
        } else {
            scheduleBtn.setEnabled(checkIfScheduleBtnEnabled());
        }
    }

    protected void updateKillBtnEnabledOrNot() {
        Button killBtn = executeJobComposite.getKillBtn();
        if (killBtn.isDisposed()) {
            return;
        }
        if (OozieJobTrackerListener.getProcess() == null) {
            killBtn.setEnabled(false);
        } else {
            killBtn.setEnabled(checkIfKillBtnEnabled());
        }
    }

    protected void updatePathTxtEnabledOrNot() {
        Text pathTxt = executeJobComposite.getPathText();
        if (pathTxt.isDisposed()) {
            return;
        }
        if (OozieJobTrackerListener.getProcess() == null) {
            pathTxt.setEnabled(false);
        } else {
            pathTxt.setEnabled(true);
        }

    }

    protected void updateRepositoryTextEnabledOrNot() {
        Text repositoryTxt = executeJobComposite.getRepositoryText();
        if (repositoryTxt.isDisposed()) {
            return;
        }
        if (!isFromRepository()) {
            return;
        }
        if (OozieJobTrackerListener.getProcess() == null) {
            repositoryTxt.setEnabled(false);
        } else {
            if (executeJobComposite.getRepositoryTextValue().length() != 0) {
                initRepositoryValues();
            }
            repositoryTxt.setEnabled(true);
        }

    }

    protected void updateOutputTxtEnabledOrNot() {
        Text outputTxt = executeJobComposite.getOutputTxt();
        if (outputTxt.isDisposed()) {
            return;
        }
        if (OozieJobTrackerListener.getProcess() == null) {
            outputTxt.setEnabled(false);
        } else {
            outputTxt.setEnabled(true);
        }
    }

    protected void updateBtnButtonEnabledOrNot() {
        Button btnButton = executeJobComposite.getBtnEdit();
        if (btnButton.isDisposed()) {
            return;
        }
        if (OozieJobTrackerListener.getProcess() == null) {
            btnButton.setEnabled(false);
        } else {
            btnButton.setEnabled(true);
        }

    }

    protected void updateEditBtnFromRepositoryEnabledOrNot() {
        Button btnButton = executeJobComposite.getSelectbtn();
        if (btnButton.isDisposed()) {
            return;
        }
        if (OozieJobTrackerListener.getProcess() == null) {
            btnButton.setEnabled(false);
        } else {
            btnButton.setEnabled(true);
        }
    }

    public boolean isPathTxtEnabled() {
        boolean isEnabled = false;

        return isEnabled;
    }

    /**
     * Checks if the scheduler setting is done from preference page, returns <code>true</code> if done, otherwise,
     * returns <code>false</code> .
     * 
     * @return
     */
    protected boolean isSettingDone() {
        // Fetch the scheduler setting infos from preference page. If all are not null, set true.
        String distributionValue = getHadoopDistribution();
        String versionValue = getHadoopVersion();
        String nameNodeEPValue = getNameNode();
        String jobTrackerEPValue = getJobTracker();
        String oozieEPValue = getOozieEndPoint();

        if ("".equals(distributionValue) || "".equals(versionValue) || "".equals(nameNodeEPValue) || "".equals(jobTrackerEPValue)
                || "".equals(oozieEPValue)) {
            isSettingDone = false;
        } else {
            isSettingDone = true;
        }
        return isSettingDone;
    }

    /**
     * 
     * @return
     * @throws JobSubmissionException
     */
    protected JobSubmission.Status checkJobSubmissionStaus(String jobId) throws JobSubmissionException {
        RemoteJobSubmission remoteJobSub = new RemoteJobSubmission();
        JobSubmission.Status jobSubStatus = remoteJobSub.status(jobId, getOozieEndPoint());

        return jobSubStatus;
    }

    public void doSelectRepositoryAction() {
        Text repositoryText = executeJobComposite.getRepositoryText();
        SelectRepositoryDialog dialog = new SelectRepositoryDialog(new Shell(), ERepositoryObjectType.METADATA, "OOZIE");
        if (dialog.open() == SelectRepositoryDialog.OK) {
            String id = dialog.getResult().getObject().getId();
            repositoryText.setText(dialog.getResult().getObject().getLabel());
            repositoryText.setEnabled(true);
            if (getEditor() != null && !OozieJobTrackerListener.getProcess().isReadOnly()) {
                IProcess2 process = OozieJobTrackerListener.getProcess();
                getCommandStack().execute(
                        new PropertyChangeCommand(process, EOozieParameterName.REPOSITORY_CONNECTION_ID.getName(), id.trim()));
            }
            updateAllEnabledOrNot();
        }

    }
    /**
     * This method is called when job editor is closed.
     */
    // public void removeTrace(String jobIdInOozie) {
    // if (tracesForOozie != null)
    // tracesForOozie.remove(jobIdInOozie);
    // }
    //
    // public void clearAllTraces() {
    // if (tracesForOozie != null)
    // tracesForOozie.clear();
    // }
}
