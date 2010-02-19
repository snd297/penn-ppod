package edu.upenn.cis.ppod.util;

/**
 * @author Sam Donnelly
 */
public interface IService {

	/**
	 * Starts the service. This method blocks until the service has completely
	 * started.
	 * <p>
	 * No unchecked exceptions will be thrown.
	 * 
	 * @throws Exception if something goes awry this will wrap the original
	 *             cause
	 */
	void start() throws Exception;

	/**
	 * Stops the service. This method blocks until the service has completely
	 * shut down.
	 * 
	 * @throws Exception if something goes awry this will wrap the original
	 *             cause
	 */
	void stop() throws Exception;

}
