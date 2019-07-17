package com.kkalletla.SpringBootGradle.controller;

import com.amazonaws.services.ec2.model.AmazonEC2Exception;
import com.kkalletla.SpringBootGradle.EC2FlipState;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Scanner;

@RestController
@PropertySource("application.properties")
public class DisplayStatus {

    private EC2FlipState ec2FlipState;

    @Autowired
    public DisplayStatus(EC2FlipState ec2FlipState) {
        this.ec2FlipState = ec2FlipState;
    }

    @Value("${instanceId}")
    String instanceId;

    Logger logger = Logger.getLogger(DisplayStatus.class);

    @RequestMapping("/flip-state")
    public String flipState(){
        Scanner scanner= new Scanner(System.in);
        /*System.out.println("Instance ID: ");*/
        //String instanceId = "i-0f0348404be6e612c";
        logger.debug("From Debug: "+instanceId);
        logger.info("From Info: "+instanceId);
        System.out.println(instanceId);
        String flip = ec2FlipState.flipState(instanceId);
        //System.out.println("The system state is flipped: "+flip);

        return "The machine state is: "+flip;
    }

    @RequestMapping("/reboot")
    public String reboot(){
        logger.info("From reboot: "+instanceId);
        try{
            ec2FlipState.reboot(instanceId);
        }catch (AmazonEC2Exception e){
            logger.error("Error while rebooting Instance: "+instanceId);
            return "Reboot Failed";
        }
        //System.out.println("The system state is flipped: "+flip);

        return "Reboot Successful";
    }
}
