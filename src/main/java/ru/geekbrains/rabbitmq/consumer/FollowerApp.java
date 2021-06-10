package ru.geekbrains.rabbitmq.consumer;

import com.rabbitmq.client.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FollowerApp {
    private static final String EXCHANGE_NAME = "Blog1";
    private static List<String> topics = new ArrayList<String>();
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();
        System.out.println("My queue name: " + queueName);
        System.out.println("Enter 'set_topic {name}'");

        while(true){
            String text = writeTopic();
            if (text.startsWith("set_topic")) {
                addTopic(getTopic(text));
                System.out.println(" [*] Waiting for messages with topics " + topics.toString());
                channel.queueBind(queueName, EXCHANGE_NAME, getTopic(text));
            }
            if(text.startsWith("delete_topic")){
                deleteTopic(getTopic(text));
                System.out.println(" [*] Waiting for messages with topics " + topics.toString());
                channel.queueUnbind(queueName, EXCHANGE_NAME, getTopic(text));
            }


        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey()+" "+ message + "'");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        }

    }

    public static String writeTopic(){
        Scanner sc = new Scanner(System.in);
        String text=sc.nextLine();
        while (!text.startsWith("set_topic")&&!text.startsWith("delete_topic")){
            System.out.println("Enter 'set_topic {name}' or 'delete_topic {name}'");
              text = sc.nextLine();
        }
        return text;
    }

    public static String getTopic(String text){
        String[] tokens = text.split("\\s");
        String topic = tokens[1];
        return topic;
    }

    public static void addTopic (String topic){
        if (!topics.contains(topic)) {
            topics.add(topic);
        }
    }

    public static void deleteTopic(String topic){
        if (topics.contains(topic)) {
            topics.remove(topic);
        }
    }

}
