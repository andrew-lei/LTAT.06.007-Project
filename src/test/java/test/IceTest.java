package test;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.ice.Agent;
import org.ice4j.ice.IceMediaStream;
import org.ice4j.ice.harvest.StunCandidateHarvester;

public class IceTest {
    public static void main(String[] args) throws Exception {
        Agent agent = new Agent(); // A simple ICE Agent


        /*** Setup the STUN servers: ***/
        String[] hostnames = new String[]{"jitsi.org", "numb.viagenie.ca", "stun.ekiga.net"};
        // Look online for actively working public STUN Servers. You can find
        // free servers.
        // Now add these URLS as Stun Servers with standard 3478 port for STUN
        // servrs.
        for (String hostname : hostnames) {
            try {
                // InetAddress qualifies a url to an IP Address, if you have an
                // error here, make sure the url is reachable and correct
                TransportAddress ta = new TransportAddress(InetAddress.getByName(hostname), 3478, Transport.TCP);
                // Currently Ice4J only supports UDP and will throw an Error
                // otherwise
                agent.addCandidateHarvester(new StunCandidateHarvester(ta));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*
         * Now you have your Agent setup. The agent will now be able to know its
         * IP Address and Port once you attempt to connect. You do need to setup
         * Streams on the Agent to open a flow of information on a specific
         * port.
         */

        IceMediaStream stream = agent.createMediaStream("audio");
        int port = 5000; // Choose any port
        try {
            agent.createComponent(stream, Transport.UDP, port, port, port + 100);
            // The three last arguments are: preferredPort, minPort, maxPort
        } catch (BindException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        /*
         * Now we have our port and we have our stream to allow for information
         * to flow. The issue is that once we have all the information we need
         * each computer to get the remote computer's information. Of course how
         * do you get that information if you can't connect? There might be a
         * few ways, but the easiest with just ICE4J is to POST the information
         * to your public sever and retrieve the information. I even use a
         * simple PHP server I wrote to store and spit out information.
         */
        String toSend = null;
        try {
            toSend = SdpUtils.createSDPDescription(agent);
            // Each computersends this information
            // This information describes all the possible IP addresses and
            // ports
        } catch (Throwable e) {
            e.printStackTrace();
        }

        /*The String "toSend" should be sent to a server. You need to write a PHP, Java or any server.
         * It should be able to have this String posted to a database.
         * Each program checks to see if another program is requesting a call.
         * If it is, they can both post this "toSend" information and then read eachother's "toSend" SDP string.
         * After you get this information about the remote computer do the following for ice4j to build the connection:*/

        String remoteReceived = ""; // This information was grabbed from the server, and shouldn't be empty.
        SdpUtils.parseSDP(agent, remoteReceived); // This will add the remote information to the agent.
        //Hopefully now your Agent is totally setup. Now we need to start the connections:
        System.out.println(remoteReceived);

        //agent.addStateChangeListener(new StateListener()); // We will define this class soon
        // You need to listen for state change so that once connected you can then use the socket.
        agent.startConnectivityEstablishment(); // This will do all the work for you to connect

    }
}
