Spring Boot managed LMAX Disruptor Example project
==================
This project uses [disruptor-spring-manager](https://github.com/zjw2011/lemon-disruptor.git) to create disruptor spring beans and perform message transactions. 
The project uses spring boot to load up the application as a JMS listener. Integration with IBM Websphere MQ is required to run this project. Ofcourse you can make minor modifications to get this running against ActiveMQ etc.      

The example uses 2 disruptor beans. One to process billing records and another to process data streams. Both disruptors are configured as spring beans.

Software pre-requisite
--------
1. JDK 8+
2. Maven 3+
3. Git      
4. IBM Websphere MQ 7.5 server and client jars
5. Spring boot 2.1.x  