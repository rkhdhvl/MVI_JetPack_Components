package com.practice.myapplication.mvi.repository

import android.util.Log
import kotlinx.coroutines.Job

// This class will be extended by each repository
open class JobManager(
    private val className: String
) {
    private val TAG: String = "AppDebug"
    // The hashmap will store the list of all jobs inside of the repository
    // The key of the HashMap will be the method name inside of the repository and its value would be the corresponding job
    private val jobs: HashMap<String, Job> = HashMap()

    fun addJob(methodName: String, job: Job) {
        // cancel in case any previous instance of the job exists, before adding any new one
        cancelJob(methodName)
        jobs[methodName] = job
    }

    fun cancelJob(methodName: String) {
        getJob(methodName)?.cancel()
    }

    // This method can return null in case the job has not been added to the HashMap
    fun getJob(methodName: String): Job? {
        if (jobs.containsKey(methodName)) {
            jobs[methodName]?.let {
                return it
            }
        }
        return null
    }

    fun cancelActiveJobs() {
        for ((methodName, job) in jobs) {
            if (job.isActive) {
                Log.e(TAG, "$className: cancelling job in method: '$methodName'")
                job.cancel()
            }
        }
    }

}