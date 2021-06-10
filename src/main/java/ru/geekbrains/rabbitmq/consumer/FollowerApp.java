package ru.geekbrains.rabbitmq.consumer;

import com.rabbitmq.client.*;

import java.util.Scanner;

public class FollowerApp {
    private static final String EXCHANGE_NAME = "Blog";
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queueName = channel.queueDeclare().getQueue();
        System.out.println("My queue name: " + queueName);
        String topic = " ";
        System.out.println("Enter topic");

         //while(true){
             String text = writeTopic();
         //}
            if(text.startsWith("send_topic")) {
                String[] token = text.split("\\s");
                topic = token[1];
            }
        System.out.println(" [*] Waiting for messages");
        channel.queueBind(queueName, EXCHANGE_NAME, topic);


        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }

    public static String writeTopic (){
        Scanner sc = new Scanner(System.in);
        String readCode=sc.nextLine();
        return readCode;
    }




//        //channel.queueBind(queueName, EXCHANGE_NAME, "");
//        //channel.queueBind(queueName, EXCHANGE_NAME, "java");
//        channel.queueDeclare(EXCHANGE_NAME, false, false, false, null);
//        System.out.println(" [*] Waiting for messages");
//
//        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            String message = new String(delivery.getBody(), "UTF-8");
//            System.out.println(" [x] Received '" + message + "'");
//            System.out.println(Thread.currentThread().getName());
//        };
//
//        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
//    }
}
