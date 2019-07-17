package com.kkalletla.SpringBootGradle;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EC2FlipState {

    public enum EC2Status{running, stopped}

    Logger logger = Logger.getLogger(EC2FlipState.class);

    public String flipState(String instanceID){

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        //DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instanceID);

        try {
            DescribeInstanceStatusRequest describeInstanceRequest = new DescribeInstanceStatusRequest().withInstanceIds(instanceID);

            DescribeInstanceStatusResult describeInstanceResult = ec2.describeInstanceStatus(describeInstanceRequest);
            List<InstanceStatus> state = describeInstanceResult.getInstanceStatuses();

            System.out.println(state.size());
            if (state.size() > 0) {
                //System.out.println("In if");
                describeInstanceResult = ec2.describeInstanceStatus(describeInstanceRequest);
                state = describeInstanceResult.getInstanceStatuses();
                String status = state.get(0).getInstanceState().getName();
                //System.out.println(status);
                stopInstance(instanceID);
                return EC2Status.stopped.toString();
            }
            else{
                //System.out.println("In Else");
                startInstance(instanceID);
                return EC2Status.running.toString();
            }


        }catch (AmazonEC2Exception e){
            e.printStackTrace();
            return "Error Occured while performing Start/Stop";
        }
    }

    public void startInstance(String instance_id)
    {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DryRunSupportedRequest<StartInstancesRequest> dry_request =
                () -> {
                    StartInstancesRequest request = new StartInstancesRequest()
                            .withInstanceIds(instance_id);

                    return request.getDryRunRequest();
                };

        DryRunResult dry_response = ec2.dryRun(dry_request);

        if(!dry_response.isSuccessful()) {
            System.out.printf(
                    "Failed dry run to start instance %s", instance_id);

            throw dry_response.getDryRunResponse();
        }

        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.startInstances(request);

        logger.info("Successfully started instance "+ instance_id);
    }

    public void stopInstance(String instance_id)
    {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DryRunSupportedRequest<StopInstancesRequest> dry_request =
                () -> {
                    StopInstancesRequest request = new StopInstancesRequest()
                            .withInstanceIds(instance_id);

                    return request.getDryRunRequest();
                };

        DryRunResult dry_response = ec2.dryRun(dry_request);

        if(!dry_response.isSuccessful()) {
            System.out.printf(
                    "Failed dry run to stop instance %s", instance_id);
            throw dry_response.getDryRunResponse();
        }

        StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.stopInstances(request);

        logger.info("Successfully stop instance "+ instance_id);
    }

    public void reboot(String instance_id){

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        RebootInstancesRequest request = new RebootInstancesRequest()
                .withInstanceIds(instance_id);

        RebootInstancesResult response = ec2.rebootInstances(request);

        System.out.printf(
                "Successfully rebooted instance %s", instance_id);
    }
}
