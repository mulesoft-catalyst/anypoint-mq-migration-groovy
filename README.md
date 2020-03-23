# Anypoint MQ and Exchange Creation (using Groovy)

### Why?
When large number of Queues and Exchanges have to be setup quickly and accurately. 

### How?

Using Anypoint MQ admin APIs and leveraging Groovy code to read the queues and exchanges from property file and create them in Anypoint Platform.


### Usage

* Create environment specific properties file <environment>_cloudhub.properties 
	* ex:- dev_cloudhub.properties, qa_cloudhub.properties

* update the following fields in the above properties
```
username="CLOUDHUB_USERNAME"   
password="CLOUDHUB_PASSWORD"
organizationID="ORGID"
regionID="us-west-2"
environmentID="ENVID"   // To get the environment id , navigate to Access Management, select Environments and click the specific environment, copy it from the url 

queues=[
    	"queue1",   //List all the queues as comma seperated values 
    	"queue2", 
	]
exchanges=[
	"exchange1",    //List all the exchanges as comma seperated values
    ]
 bindings=[
        "exchange1:queue1",      // Follow the format exchange:queue to bind the queue to the corresponding exchanges
        "exchange1:queue1", 
 ]
 )
```

* To run the groovy code execute the following command
	* ```code(groovy AnypointMQAdminClient.groovy env_cloudhub.properties)```


#### Development


##### Dependencies
Groovy Runtime

### Contribution

Ben Currier and PK Reddy


