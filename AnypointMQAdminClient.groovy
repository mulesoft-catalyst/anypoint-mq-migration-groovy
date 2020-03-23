
//  Command 
// groovy AnypointMQAdminClient.groovy env_cloudhub.properties
// To get the environment id , navigate to Access Management, select Environments and click the specific environment, copy it from the url 
package com.organization

@Grab(group = 'org.apache.httpcomponents', module = 'httpclient', version = '4.5.3')

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder

class AnypointMQAdminClient {

  static String HOST = "https://anypoint.mulesoft.com"

    static void main(String[] args) {

        def props

        if (args) {
            props = new ConfigSlurper().parse(new File(args[0]).toURI().toURL())
        } else {
            props = new ConfigSlurper().parse(new File("cloudhub.properties").toURI().toURL())
        }

        def envID = props.environmentID

        def token = authenticate(props.username, props.password)

		createQueues(props, token, envID)

        createExchanges(props, token, envID)

        bindQueuesExchange(props, token, envID)
		

    }

    static authenticate(String username, String password) {

      // build JSON
        def map = [:]
        map["username"] = username
        map["password"] = password
        def jsonBody = new JsonBuilder(map).toString()

        // build HTTP POST
        def url = HOST + '/accounts/login'
        def post = new HttpPost(url)

        post.addHeader("Content-Type", "application/json")
        post.setEntity(new StringEntity(jsonBody))

        // execute
        def client = HttpClientBuilder.create().build()
        def response = client.execute(post)

        // read and print response
        def bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
        def jsonResponse = bufferedReader.getText()
        println "response: \n" + jsonResponse

        // parse and return token
        def slurper = new JsonSlurper()
        def resultMap = slurper.parseText(jsonResponse)

        return resultMap["access_token"]

    }
	
	 static retrieveDestinations(ConfigObject props, String token, String envID) {

        def orgID = props.organizationID
        def regionID = props.regionID

        // build HTTP GET
        def getDestinationsURL = HOST + '/mq/admin/api/v1/organizations/' + orgID + '/environments/' + envID + '/regions/' + regionID + '/destinations'
        def getDestinations = new HttpGet(getDestinationsURL)

        // set token
        getDestinations.setHeader("Authorization", "Bearer " + token)

        // execute
        def client = HttpClientBuilder.create().build()
        def response = client.execute(getDestinations)

        // parse and print results
        def bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
        def jsonResponse = bufferedReader.getText()
        println "response: \n" + jsonResponse

    }


    static createQueues(ConfigObject props, String token, String envID) {

        def orgID = props.organizationID
        def regionID = props.regionID

        def queues = props.queues

        queues.each { queueID ->

            def putQueueURL = HOST + '/mq/admin/api/v1/organizations/' + orgID + '/environments/' + envID + '/regions/' + regionID + '/destinations/queues/' + queueID
            def putQueue = new HttpPut(putQueueURL)

            putQueue.addHeader("Content-Type", "application/json")
            putQueue.addHeader("Authorization", "Bearer " + token)

            def queueMap = [:]
            queueMap["defaultTtl"] = 1208736000
			// 7 days in milliseconds 604800000
			// 13.99 ttl in milliseconds 1208736000
            queueMap["defaultLockTtl"] = 120000
            queueMap["encrypted"] = false
            queueMap["fifo"] = false

            def putQueueJSONBody = new JsonBuilder(queueMap).toString()
            putQueue.setEntity(new StringEntity(putQueueJSONBody))

            def client = HttpClientBuilder.create().build()
            def response = client.execute(putQueue)

            def bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
            def jsonResponse = bufferedReader.getText()
            println "response: \n" + jsonResponse

        }

    }

    static createExchanges(ConfigObject props, String token, String envID) {

        def orgID = props.organizationID
        def regionID = props.regionID

        def exchanges = props.exchanges

        exchanges.each { exchangeID ->

            def putExchangeURL = HOST + '/mq/admin/api/v1/organizations/' + orgID + '/environments/' + envID + '/regions/' + regionID + '/destinations/exchanges/' + exchangeID
            def putExchange = new HttpPut(putExchangeURL)

            putExchange.addHeader("Content-Type", "application/json")
            putExchange.addHeader("Authorization", "Bearer " + token)

            def exchangeMap = [:]
            exchangeMap["encrypted"] = false

            def putExchangeJSONBody = new JsonBuilder(exchangeMap).toString()
            putExchange.setEntity(new StringEntity(putExchangeJSONBody))

            def client = HttpClientBuilder.create().build()
            def response = client.execute(putExchange)

            def bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
            def jsonResponse = bufferedReader.getText()
            println "response: \n" + jsonResponse

        }

    }

    static bindQueuesExchange(ConfigObject props, String token, String envID) {

        def orgID = props.organizationID
        def regionID = props.regionID

        def bindings = props.bindings

        bindings.each { binding ->

            def exchangeID = binding.split(':')[0]
            def queueID = binding.split(':')[1]

            def putBindingURL = HOST + '/mq/admin/api/v1/organizations/' + orgID + '/environments/' + envID + '/regions/' + regionID + '/bindings/exchanges/' + exchangeID + '/queues/' + queueID
            def putBinding = new HttpPut(putBindingURL)

            putBinding.addHeader("Content-Type", "application/json")
            putBinding.addHeader("Authorization", "Bearer " + token)

            def client = HttpClientBuilder.create().build()
            def response = client.execute(putBinding)

            def bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
            def jsonResponse = bufferedReader.getText()
            println "response: \n" + jsonResponse

        }

    }


}
