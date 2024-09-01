package com.qlangtech.tis.plugin.dolphinscheduler.task;

import com.qlangtech.tis.datax.executor.BasicTISInitializeProcessor;
import com.qlangtech.tis.datax.executor.BasicTISTableDumpProcessor;
import com.qlangtech.tis.datax.powerjob.ExecPhase;
import com.qlangtech.tis.job.common.JobParams;
import com.qlangtech.tis.manage.common.Config;
import com.qlangtech.tis.plugin.dolphinscheduler.task.impl.DS4TISConfig;
import com.qlangtech.tis.plugin.dolphinscheduler.task.impl.DSTaskContext;
import com.qlangtech.tis.sql.parser.meta.NodeType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.plugin.task.api.AbstractRemoteTask;
import org.apache.dolphinscheduler.plugin.task.api.TaskConstants;
import org.apache.dolphinscheduler.plugin.task.api.TaskException;
import org.apache.dolphinscheduler.plugin.task.api.TaskExecutionContext;
import org.apache.dolphinscheduler.plugin.task.api.model.Property;
import org.apache.dolphinscheduler.plugin.task.api.parameters.AbstractParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        String keyTISHost = "tisHost";
        DS4TISConfig.tisHost = Objects.requireNonNull(prepareParams.get(keyTISHost)
                , "param " + keyTISHost + " relevant property shall be config").getValue();
        logger.info("execute init");
        this.parameters = JSONUtils.parseObject(taskRequest.getTaskParams(), TISDatasyncParameters.class);
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
            final DSTaskContext taskContext = new DSTaskContext(parameters, this.taskRequest);

            switch (nodeType) {
                case START: {
                    BasicTISInitializeProcessor initialize = new BasicTISInitializeProcessor();
                    initialize.initializeProcess(taskContext);
                    break;
                }

                case DUMP: {
                    BasicTISTableDumpProcessor dumpProcessor = new BasicTISTableDumpProcessor();
                    dumpProcessor.processSync(taskContext, ExecPhase.Mapper);
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

//            if ("start".equalsIgnoreCase(nodeType)) {
//
//
//                // 开始节点
////                Map<String, String> outputParams = new HashMap<>();
////                taskId = String.valueOf((int) (Math.random() * 1000));
////                logger.info("genereate taskId:" + taskId);
////                outputParams.put(JobParams.KEY_TASK_ID, taskId);
////                setTaskOutputParams(outputParams);
////                this.parameters.dealOutParam(outputParams);
//
//            } else if ("dump".equalsIgnoreCase(nodeType)) {
//                // 任务执行节点
//                //            Map<String, String> outputParams = new HashMap<>();
//                //            outputParams.put(JobParams.KEY_TASK_ID, String.valueOf((int) (Math.random() * 1000)));
//                //            setTaskOutputParams(outputParams);
//
//
//
//
//                //  Property tskId = this.parameters.getVarPoolMap().get(JobParams.KEY_TASK_ID);
//                //  logger.info("tskId:" + (tskId != null ? tskId.getValue() : " is null"));
//                // taskRequest.
//                logger.info("execute dump node");
//            } else if ("join".equalsIgnoreCase(nodeType)) {
//
//
//
//            } else {
//                throw new IllegalStateException("illegal node type:" + nodeType);
//            }
//
//
//        } catch (Exception e) {
//            throw new TaskException(e.getMessage(), e);
//        }

//        this.getAppIds();
//        this.getApplicationIds();
//        Map<String, Property> params = taskRequest.getParamsMap();
//        System.out.println("Params:");
//        params.forEach((key, val) -> {
//            System.out.println("  key:" + key + ",val:" + val);
//        });
//        System.out.println("DefinedParams:");
//        Map<String, String> definedParams = taskRequest.getDefinedParams();
//        definedParams.forEach((key, val) -> {
//            System.out.println("  key:" + key + ",val:" + val);
//        });
//
//        System.out.println("TaskInstanceId:" + taskRequest.getTaskInstanceId());
//
//
//        System.out.println("getExecutePath:" + taskRequest.getExecutePath());
//
//        System.out.println("getTaskInstanceId:" + taskRequest.getTaskInstanceId());
//
//        System.out.println("getTaskJson:" + taskRequest.getTaskJson());
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
