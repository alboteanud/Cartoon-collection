package com.craiovadata.android.mytoons.data

import java.util.*
import java.util.concurrent.Executor

open class AppExecutor(private val executor: Executor) : Executor {
    private val tasks: Queue<Runnable> = ArrayDeque()
    var active: Runnable? = null

    @Synchronized
    override fun execute(r: Runnable) {
        tasks.add(Runnable {
            try {
                r.run()
            } finally {
                scheduleNext()
            }
        })
        if (active == null) {
            scheduleNext()
        }
    }

    @Synchronized
    protected fun scheduleNext() {
        if (tasks.poll().also { active = it } != null) {
            executor.execute(active)
        }
    }
}