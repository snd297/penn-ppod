/*
 * from http://ptrthomas.wordpress.com/2009/01/24/how-to-start-and-stop-jetty-revisited/
 */
package edu.upenn.cis.ppod.demo;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class StopDemoServer {

	public static void main(String[] args) throws Exception {
		int port = 8081;
		if (args.length > 0) {
			port = Integer.valueOf(args[0]);

		}
		Socket s = new Socket(
				InetAddress.getByName("127.0.0.1"),
				port);
		OutputStream out = s.getOutputStream();
		System.out.println("*** sending jetty stop request");
		out.write(("\r\n").getBytes());
		out.flush();
		s.close();
		System.out.println("*** done");
		out.write(("\r\n").getBytes());
		out.flush();
	}
}
