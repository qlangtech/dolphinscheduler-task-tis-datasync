package com.qlangtech.tis.plugin.dolphinscheduler.task;

import com.qlangtech.tis.cloud.ITISCoordinator;
import com.qlangtech.tis.datax.IDataXTaskRelevant;
import com.qlangtech.tis.datax.executor.BasicTISInitializeProcessor;
import com.qlangtech.tis.datax.executor.BasicTISTableDumpProcessor;
import com.qlangtech.tis.datax.powerjob.ExecPhase;
import com.qlangtech.tis.extension.model.UpdateCenter;
import com.qlangtech.tis.extension.model.UpdateCenterResource;
import com.qlangtech.tis.manage.common.Config;
import com.qlangtech.tis.plugin.dolphinscheduler.task.impl.DS4TISConfig;
import com.qlangtech.tis.plugin.dolphinscheduler.task.impl.DSTaskContext;
import com.qlangtech.tis.sql.parser.meta.NodeType;
import com.qlangtech.tis.utils.TisMetaProps;
import org.apache.commons.io.FileUtils;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.plugin.task.api.AbstractRemoteTask;
import org.apache.dolphinscheduler.plugin.task.api.TaskConstants;
import org.apache.dolphinscheduler.plugin.task.api.TaskException;
import org.apache.dolphinscheduler.plugin.task.api.TaskExecutionContext;
import org.apache.dolphinscheduler.plugin.task.api.model.Property;
import org.apache.dolphinscheduler.plugin.task.api.parameters.AbstractParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * https://github.com/apache/dolphinscheduler/blob/3.2.2/dolphinscheduler-task-plugin/dolphinscheduler-task-datasync/pom.xml
 *
 * @author: 百岁（baisui@qlangtech.com）
 **/
public class TISDatasyncTask extends AbstractRemoteTask {
    private static final Logger logger = LoggerFactory.getLogger(TISDatasyncTask.class);
    private TISDatasyncParameters parameters;

    protected TISDatasyncTask(TaskExecutionContext taskExecutionContext) {
        super(taskExecutionContext);
    }

    @Override
    public void init() {
        Map<String, Property> prepareParams = this.taskRequest.getPrepareParamsMap();
//        String keyTISAddress = "tisAddress";
//        String keyTISHTTPHost = "tisHTTPHost";
        Config.tisHttpHost
                = Objects.requireNonNull(prepareParams.get(Config.KEY_TIS_HTTP_Host)
                , "param " + Config.KEY_TIS_HTTP_Host + " relevant property shall be config").getValue();
        DS4TISConfig.tisHost = Objects.requireNonNull(prepareParams.get(Config.KEY_TIS_ADDRESS)
                , "param " + Config.KEY_TIS_ADDRESS + " relevant property shall be config").getValue();
        logger.info("execute init");
        this.parameters = JSONUtils.parseObject(taskRequest.getTaskParams(), TISDatasyncParameters.class);
        DSTaskContext tskContext = createDsTaskContext();
        File dsHome = tskContext.getDSServerHome();
        // 下载 http://mirror.qlangtech.com/4.0.0-rc1/tis/tis-data.tar.gz
        // UpdateCenterResource.getTISTarPkg("tis-data.tar.gz");
        File initializedToken = new File(dsHome, "tis_initialized");
        File initialingToken = new File(dsHome, "tis_initialing");
        try {
            if (!initializedToken.exists()) {
                FileUtils.touch(initialingToken);
                try (RandomAccessFile raf = new RandomAccessFile(initialingToken, "rw")) {
                    FileChannel channel = raf.getChannel();
                    try (FileLock fileLock = channel.lock()) {
                        if (!initializedToken.exists()) {
                            // data目录拷贝到本地
                            UpdateCenter.copyDataTarToLocal(dsHome, Optional.empty());
                            UpdateCenter.copyTarToLocal(IDataXTaskRelevant.KEY_TIS_DATAX_EXECUTOR + ".tar.gz", dsHome, Optional.empty());
                            FileUtils.touch(initializedToken);
                        }
                    }
                }
            }
            IDataXTaskRelevant.dataXExecutorDir
                    .set(new File(dsHome, IDataXTaskRelevant.KEY_TIS_DATAX_EXECUTOR));
            Config.setDataDir((new File(dsHome, "data")).getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // 如果只需要本地写入日志，则不需要连接远程Grpc服务了
        if (tskContext.isDisableGrpcRemoteServerConnect()) {
            ITISCoordinator.disableRemoteServer();
        }
    }

    @Override
    public List<String> getApplicationIds() throws TaskException {
        return Collections.emptyList();
    }

    @Override
    public void cancelApplication() throws TaskException {

    }

    @Override
    public void submitApplication() throws TaskException {
        try {
            final NodeType nodeType = NodeType.parse(this.parameters.getDestinationLocationArn());
            final DSTaskContext taskContext = createDsTaskContext();

            switch (nodeType) {
                case START: {
                    BasicTISInitializeProcessor initialize = new BasicTISInitializeProcessor();
                    initialize.initializeProcess(taskContext);
                    break;
                }

                case DUMP: {
                    BasicTISTableDumpProcessor dumpProcessor = new BasicTISTableDumpProcessor();
                    dumpProcessor.processSync(taskContext, ExecPhase.Mapper);
                    dumpProcessor.processPostTask(taskContext);
                    break;
                }
                case JOINER_SQL:
                default:
                    throw new IllegalStateException("illegal nodeType:" + nodeType);
            }

            this.setExitStatusCode(TaskConstants.EXIT_CODE_SUCCESS);
        } catch (Exception e) {
            throw new TaskException(e.getMessage(), e);
        }
    }

    private DSTaskContext createDsTaskContext() {
        return new DSTaskContext(parameters, this.taskRequest);
    }

    @Override
    public void trackApplicationStatus() throws TaskException {

    }

    @Override
    public AbstractParameters getParameters() {
        logger.info("getParameters:" + this.parameters.checkParameters());
        return this.parameters;
    }
}
