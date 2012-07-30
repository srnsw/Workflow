package au.gov.nsw.records.digitalarchive.action.wrapper;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

/**
 * The ClamAV client that communicate to the remote ClamAV instance to perform the 
 * virus scanning task on the files in the desired path.  
 * @author wisanup
 *
 */
public class ClamAVSocket extends  SimpleChannelUpstreamHandler {

	private static final Log log = LogFactory.getLog(ClamAVSocket.class);
	private ClientBootstrap bootstrap;
	private String path;

	private final CountDownLatch latch;
	private boolean isSuccess;
	private String lastLine;
	 
	private String host;
	private int port;
	
	/**
	 * Create an instance of ClamAVSocket
	 * @param host the host of ClamAV instance
	 * @param port the port of ClamAV instance
	 */
	public ClamAVSocket(String host, int port){
		
		this.host = host;
		this.port = port;
		
		this.isSuccess = false;
		this.latch = new CountDownLatch(1);
		this.lastLine = "";
		
		// Configure the client.
		bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// Configure the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {

				return Channels.pipeline( 
						new StringEncoder(),
						new StringDecoder(),
						ClamAVSocket.this);
			}
		});
	}

	/**
	 * Perform virus scan on the specified path
	 * @param path the path that contains files to be scanned
	 * @return true if no virus detected, false otherwise
	 */
	public boolean scan(String path){
		// remove the last '/'
		if (path.lastIndexOf('/') == path.length() -1){
			path = path.substring(0, path.length() - 1);
		}
		this.path = path;
		
		// Start the connection attempt.
		bootstrap.connect(new InetSocketAddress(host, port));
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			log.error( "Interrupted during scan", e);
			Thread.currentThread().interrupt();
			return false;
		}
		// Check the result!
		return isSuccess;
	}

	@Override
	public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e)
			throws Exception {
		super.writeComplete(ctx, e);
		log.info( String.format("Command [MULTISCAN %s] sent to ClamAV [%d bytes]", path, e.getWrittenAmount()));
	}
	
	@Override
	public void channelConnected(
			ChannelHandlerContext ctx, ChannelStateEvent e) {
		log.info( "Connected to ClamAV");
		e.getChannel().write(String.format("MULTISCAN %s", path));
		
	}

	@Override
	public void messageReceived(
			ChannelHandlerContext ctx, MessageEvent e) {
			
		Scanner sc = new Scanner(e.getMessage().toString());
		while(sc.hasNext()){
			lastLine = sc.nextLine();
			log.info(String.format("Received [%s]", lastLine));
		}
	}

	@Override
	public void exceptionCaught(
			ChannelHandlerContext ctx, ExceptionEvent e) {
		log.error( "Unexpected exception from downstream", e.getCause());
		e.getChannel().close();
		latch.countDown();
		
		isSuccess = false;
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		// the expectation result is similar to => [/home/wisanup: OK]
		if (lastLine.equals(String.format("%s: OK", path))){
			//log.info("last line");
			//log.info(String.format("%s: OK", path));
			log.info(String.format("No error detected [%s]", lastLine));
			isSuccess = true;
		}else{
			log.error(String.format("Error detected [%s]", lastLine));
		}
		
		latch.countDown();
		log.info("Channel closed");
		super.channelClosed(ctx, e);
	}
}
