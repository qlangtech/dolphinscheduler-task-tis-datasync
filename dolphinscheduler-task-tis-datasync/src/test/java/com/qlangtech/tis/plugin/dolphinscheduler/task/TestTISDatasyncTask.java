package com.qlangtech.tis.plugin.dolphinscheduler.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qlangtech.tis.datax.executor.BasicTISTableDumpProcessor;
import com.qlangtech.tis.datax.executor.ITaskExecutorContext;
import com.qlangtech.tis.datax.powerjob.ExecPhase;
import com.qlangtech.tis.plugin.dolphinscheduler.task.impl.DS4TISConfig;
import com.qlangtech.tis.plugin.dolphinscheduler.task.impl.DSTaskContext;
import com.qlangtech.tis.test.TISEasyMock;
import junit.framework.TestCase;
import org.apache.dolphinscheduler.plugin.task.api.TaskExecutionContext;
import org.apache.dolphinscheduler.plugin.task.api.model.Property;
import org.easymock.EasyMock;

/**
 * @author: 百岁（baisui@qlangtech.com）
 * @create: 2024-08-28 15:36
 **/
public class TestTISDatasyncTask extends TestCase implements TISEasyMock {

    @Override
    protected void setUp() throws Exception {
        DS4TISConfig.tisHost = "192.168.28.116";
    }

    public void testTISTableDumpProcessor() throws Exception {

        TISDatasyncParameters parameters = new TISDatasyncParameters();
        parameters.varPool = JSONArray.parseArray(
                "[{\"prop\":\"app\",\"direct\":\"OUT\",\"type\":\"VARCHAR\",\"value\":\"mysql\"},{\"prop\":\"dryRun\",\"direct\":\"OUT\",\"type\":\"BOOLEAN\",\"value\":\"false\"},{\"prop\":\"execTimeStamp\",\"direct\":\"OUT\",\"type\":\"LONG\",\"value\":\"1724823048532\"},{\"prop\":\"javaMemorySpec\",\"direct\":\"OUT\",\"type\":\"VARCHAR\",\"value\":\"-Xms1351m -Xmx1801m\"},{\"prop\":\"preTaskId\",\"direct\":\"OUT\",\"type\":\"INTEGER\",\"value\":\"173\"},{\"prop\":\"taskid\",\"direct\":\"OUT\",\"type\":\"INTEGER\",\"value\":\"186\"},{\"prop\":\"pluginCfgsMetas\",\"direct\":\"OUT\",\"type\":\"VARCHAR\",\"value\":\"UEsDBBQACAgIABavHFkAAAAAAAAAAAAAAAAUAAQATUVUQS1JTkYvTUFOSUZFU1QuTUb+ygAAhZPLitswFIb3Br+DyTpWrIttJasUSqHQKaVT6KIUI9saxyBbGkkZxm/fIztpmkno7Cz5/79z0Tlx9FUMcpf43lXCmGqEUxwNk3tWuwSXhPGcsiynOYmj+KSV40tlrDYujoRzcqiVrA7aeTBsCcIFR4QjjIs4Urqr2t7uko02fgMnt4FAcRSi3XfY4+j7EKQVvZoWZSu8cPpoG1m19fg33UaPTqtwt8iUOI7NoTLaApdnPLtx+8kEsrT1dKnGqGPXjw8SVHHUKV0L9W2+evTawn9hNnM7No0e0LMSY+dlc0CARgH9ivrBKPQxfH6XAuDoNY6SQe1JRljGCccUmpjndLtu6407aHOHtCSBWjeDHud0PwngNJDEhF7POEpKzDClW5bnAactBHyXB5wz8oZXYJyXjBBarN+tFDiXYn/a3odi71RaXlDeitE9aTtAmrVwctYD52whOC8YjBf9X/RBjKKT6PMHY5Y6TlED5yowX8NIGvNFOP+g2/5p+gGz5LwYzC65eY44+ufpd8mvFYRK50JTSGHQY2rbenDpotozlCG8DytBGS4Y5xldrVfzOCfXtrcGysoyZyQnbDGkrUvnUq+VwDmLOebljfglv0/mBFb0JA7tBs6p49fCooQNIyehM6r3qRewu6kDl5fd9AYPnMVYUk4JXv0OO/MHUEsHCBP1QoX7AQAAMAQAAFBLAwQUAAgICAAWrxxZAAAAAAAAAAAAAAAACQAAAHRhc2tfeHh4eA==\"}]"
                , Property.class
        );
        JSONObject taskParams = JSONObject.parseObject(   "{\"localParams\":[{\"prop\":\"xxx\",\"direct\":\"IN\",\"type\":\"VARCHAR\",\"value\":\"\"}],\"resourceList\":[],\"destinationLocationArn\":\"table\",\"sourceLocationArn\":\"{\\n\\t\\\"table\\\":\\\"base\\\",\\n\\t\\\"exec\\\":{\\n\\t\\t\\\"execEpochMilli\\\":0,\\n\\t\\t\\\"resType\\\":\\\"DataApp\\\",\\n\\t\\t\\\"dataXName\\\":\\\"mysql\\\",\\n\\t\\t\\\"taskSerializeNum\\\":0,\\n\\t\\t\\\"allRowsApproximately\\\":-1,\\n\\t\\t\\\"jobInfo\\\":[\\n\\t\\t\\t{\\n\\t\\t\\t\\t\\\"dataXInfo\\\":\\\"base_0.json/order/base\\\",\\n\\t\\t\\t\\t\\\"taskSerializeNum\\\":0\\n\\t\\t\\t}\\n\\t\\t],\\n\\t\\t\\\"taskId\\\":-1\\n\\t},\\n\\t\\\"dataxName\\\":\\\"mysql\\\"\\n}\",\"name\":\"mysql@base\"}");
      //  "{\\\"localParams\\\":[{\\\"prop\\\":\\\"xxx\\\",\\\"direct\\\":\\\"IN\\\",\\\"type\\\":\\\"VARCHAR\\\",\\\"value\\\":\\\"\\\"}],\\\"resourceList\\\":[],\\\"destinationLocationArn\\\":\\\"table\\\",\\\"sourceLocationArn\\\":\\\"{\\\\n\\\\t\\\\\\\"table\\\\\\\":\\\\\\\"base\\\\\\\",\\\\n\\\\t\\\\\\\"exec\\\\\\\":{\\\\n\\\\t\\\\t\\\\\\\"execEpochMilli\\\\\\\":0,\\\\n\\\\t\\\\t\\\\\\\"resType\\\\\\\":\\\\\\\"DataApp\\\\\\\",\\\\n\\\\t\\\\t\\\\\\\"dataXName\\\\\\\":\\\\\\\"mysql\\\\\\\",\\\\n\\\\t\\\\t\\\\\\\"taskSerializeNum\\\\\\\":0,\\\\n\\\\t\\\\t\\\\\\\"allRowsApproximately\\\\\\\":-1,\\\\n\\\\t\\\\t\\\\\\\"jobInfo\\\\\\\":[\\\\n\\\\t\\\\t\\\\t{\\\\n\\\\t\\\\t\\\\t\\\\t\\\\\\\"dataXInfo\\\\\\\":\\\\\\\"base_0.json/order/base\\\\\\\",\\\\n\\\\t\\\\t\\\\t\\\\t\\\\\\\"taskSerializeNum\\\\\\\":0\\\\n\\\\t\\\\t\\\\t}\\\\n\\\\t\\\\t],\\\\n\\\\t\\\\t\\\\\\\"taskId\\\\\\\":-1\\\\n\\\\t},\\\\n\\\\t\\\\\\\"dataxName\\\\\\\":\\\\\\\"mysql\\\\\\\"\\\\n}\\\",\\\"name\\\":\\\"mysql@base\\\"}";
       // System.out.println(sourceLoc);
        parameters.setDestinationLocationArn(taskParams.getString("destinationLocationArn"));
        parameters.setSourceLocationArn(taskParams.getString("sourceLocationArn"));
        TaskExecutionContext taskRequest = new TaskExecutionContext();


        DSTaskContext taskContext = new DSTaskContext(parameters, taskRequest);


        // JSONObject instanceParam = null;
        //  EasyMock.expect(taskContext.getInstanceParams()).andReturn(instanceParam);
        BasicTISTableDumpProcessor dumpProcessor = new BasicTISTableDumpProcessor();
        replay();
        dumpProcessor.processSync(taskContext, ExecPhase.Mapper);
        verifyAll();
    }
}
