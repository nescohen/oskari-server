package fi.nls.oskari.worker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;

/**
 * Manages workers for different kind of jobs
 */
public class JobQueue
{
	private static final Logger log = LogFactory.getLogger(JobQueue.class);
			
    private final int nWorkers;
    private final Worker[] workers;
    private final Map<String, Job> jobs;
    private final LinkedList<Job> queue;

    private long maxQueueLength = 0;
    private long maxJobLength = 0;
    private long minJobLength = Long.MAX_VALUE;
    private long jobCount = 0;
    private long avgRuntime = 0;
    private String firstCrashedJob = null;
    private long crashedJobCount = 0;

    /**
     * Initializes a queue and workers
     * 
     * @param nWorkers
     */
    public JobQueue(int nWorkers)
    {
        this.nWorkers = nWorkers;
        queue = new LinkedList<Job>();
        workers = new Worker[this.nWorkers];
        jobs = new HashMap<String, Job>();

        for (int i = 0; i < this.nWorkers; i++) {
        	workers[i] = new Worker();
        	workers[i].start();
        }
    }
    public long getMaxQueueLength() {
        return maxQueueLength;
    }

    public long getMaxJobLength() {
        return maxJobLength;
    }

    public long getMinJobLength() {
        return minJobLength;
    }

    public long getJobCount() {
        return jobCount;
    }

    public long getAvgRuntime() {
        return avgRuntime;
    }
    public long getQueueSize() {
        return queue.size();
    }

    public String getFirstCrashedJob() {
        return firstCrashedJob;
    }

    public long getCrashedJobCount() {
        return crashedJobCount;
    }

    /**
     * Adds a new job into queue and notifies workers
     * 
     * @param job
     */
    public void add(Job job) {
    	String key = job.getKey();
    	jobs.put(key, job);
        synchronized(queue) {
            queue.addLast(job);
            queue.notify();
        }
        if(maxQueueLength < queue.size()) {
            maxQueueLength = queue.size();
        }
        log.debug("Added", key);
    }

    /**
     * Removes a job from queue and terminates a running job
     * 
     * @param job
     */
    public void remove(Job job) {
    	String key = job.getKey();
    	Job processedJob = jobs.get(key);
		if(processedJob != null)
			processedJob.terminate();
        synchronized(queue) {
        	queue.remove(job);
        }
        log.debug("Removed", key);
    }
    
    /**
     * Defines a worker thread for queue's job
     */
    private class Worker extends Thread { 
    	
    	/**
    	 * Processes queues jobs
    	 * 
    	 * Waits for the queue to have some jobs.
    	 * Always takes the first job available, runs it and removes it from queue.
    	 * 
    	 */
        public void run() {
            Runnable r;

            while (true) {
                synchronized(queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException ignored) { }
                    }
                    r = queue.removeFirst();
                }
                final long startTime = System.nanoTime();
                jobCount++;
                try {
                    r.run();
                } catch (Exception e) {
                    log.error("Exception while running job:", e.getMessage());
                    log.debug(e, "Here's the stacktrace");
                }
                catch (OutOfMemoryError e) {
                    crashedJobCount++;
                    log.error("OutOfMemory while running job:",((Job) r).getKey(), "- message", e.getMessage());
                    if(firstCrashedJob == null) {
                        firstCrashedJob = ((Job) r).getKey();
                    }
                    throw e;
                }
                finally {
                    ((Job) r).teardown();
                    jobs.remove(((Job) r).getKey());
                    log.debug("Finished", ((Job) r).getKey());
                    final long runTimeMS = (System.nanoTime() - startTime) / 1000000L;
                    if(runTimeMS > maxJobLength) {
                        maxJobLength = runTimeMS;
                    }
                    if(runTimeMS < minJobLength) {
                        minJobLength = runTimeMS;
                    }
                    if(avgRuntime == 0) {
                        avgRuntime = runTimeMS;
                    }
                    else {
                        avgRuntime = ((avgRuntime * (jobCount -1)) + runTimeMS) / jobCount;
                    }
                }
            }
        }
    }
}